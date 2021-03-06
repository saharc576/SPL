package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.passiveObjects.ResourcesManager;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }


    @Override
    protected void initialize() {
        ResourcesManager.latchLeiaInit.countDown();
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent bomb) ->{
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            complete(bomb, true);

        });

        // call the termination function and update termination time in Diary
        subscribeBroadcast(TerminateBroadcast.class, c-> {
            terminate();
            Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
            ResourcesManager.latchJsonOutput.countDown();
        });
    }

}
