package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.ResourcesManager;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService{

    private ResourcesManager mResourcesManager = ResourcesManager.getInstance();

    public HanSoloMicroservice() {
        super("Han");

    }

    /**
     * after registration in {@code run} method this method will be called
     * Han subscribes to {@link AttackEvent} and when a message of this type is received, he executes the
     * following callBack:
     * He tries to acquire ALL Ewoks he needs, and if he can't he waits and tries again.
     * when done he executes the attack
     * finally he releases the EWOKS
     * <P>
     * Also he subscribes to {@link TerminateBroadcast} and a message of this type will call {@code terminate}
     */
    @Override
    protected void initialize() {
        ResourcesManager.latchLeiaInit.countDown();
        subscribeEvent(AttackEvent.class, (AttackEvent attackEvent) -> {
             // acquire Ewoks
             mResourcesManager.acquireEwoks(attackEvent.getSerials());

            // once Han acquired all Ewoks he needs for the attack, he executes the attack
            try {
                Thread.sleep(attackEvent.getDuration());
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            // release ALL ewoks
            mResourcesManager.releaseEwoks(attackEvent.getSerials());

            complete(attackEvent, true);

            // after each attack Han will update his finish time and override the last value
            Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            Diary.getInstance().incTotalAttacks();
        });


        // call the termination function and update termination time in Diary
        subscribeBroadcast(TerminateBroadcast.class, c-> {
            terminate();
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
            ResourcesManager.latchJsonOutput.countDown();
        });
    }

}