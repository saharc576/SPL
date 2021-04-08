package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.ResourcesManager;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private List<Future<Boolean>> futureList;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        this.futureList = new LinkedList<>();
    }

    /**
     * after registration in {@code run} method this method will be called
     * First leia waits for the other threads to register to {@link bgu.spl.mics.MessageBusImpl}
     */
    @Override
    protected void initialize() {
        // call the termination function and update termination time in Diary
        subscribeBroadcast(TerminateBroadcast.class, c-> {
            terminate();
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
            ResourcesManager.latchJsonOutput.countDown();
        });

        for (Attack a : attacks) {
            Future<Boolean> attackEventFuture = sendEvent(new AttackEvent(a));
            futureList.add(attackEventFuture);
        }

        /** Leia will go through the list of futures and check if they are all done
         * as soon as they are, she will send the deactivation event
         */
        Boolean res;
        for (Future<Boolean> f : futureList) {
            res = null;
            while (res == null) {
                res = f.get(10, TimeUnit.MILLISECONDS);
                if (res != null)
                    res = true;
            }
        }

        Future<Boolean> deactivationEventFuture = sendEvent(new DeactivationEvent());

        /** As soon as the {@link DeactivationEvent} {@code deactivationEventFuture} resolved Leia will send
         * the {@link BombDestroyerEvent} {@code bombEventFuture} */
        res = null;
        while (res == null) {
            res = deactivationEventFuture.get(100,TimeUnit.MILLISECONDS);
            if (res != null)
                res = true;
        }

        Future<Boolean> bombEventFuture = sendEvent(new BombDestroyerEvent());

        /** As soon as the {@link BombDestroyerEvent} {@code bombEventFuture} resolved Leia will send
         * the {@link TerminateBroadcast} broadcast to finish the game */
        res = null;
        while (res == null) {
            res = bombEventFuture.get(100,TimeUnit.MILLISECONDS);
            if (res != null)
                res = true;
        }

        sendBroadcast(new TerminateBroadcast());
    }

}
