package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * This is a helper Singleton class, Thread safe, to manage all resources of the app.
 * this implementation is more generic and can be easily modified to manage more
 * resources
 */

public class ResourcesManager{
    private final Ewoks mEwoks;
    private static int mNumOfEwoks;
    public static CountDownLatch latchLeiaInit = new CountDownLatch(4);
    public static CountDownLatch latchJsonOutput = new CountDownLatch(5);



    private static class SingletonHolder {
        private static ResourcesManager instance = new ResourcesManager();
    }
    private ResourcesManager() {
        mEwoks = Ewoks.getInstance(mNumOfEwoks);
    }

    public static ResourcesManager getInstance() {
        return SingletonHolder.instance;
    }

    public static ResourcesManager getInstance(int numOfEwoks) {
        mNumOfEwoks = numOfEwoks;
        return SingletonHolder.instance;
    }


    /**
     * a synchronized method to acquire Ewoks.
     * @param wantedEwoks an array list of ALL Ewoks that are needed.
     * @return false if at least one of the Ewoks is not available, else return true
     * @throws IllegalArgumentException in case the serial number {@code i} is not in range
     */
    public synchronized void acquireEwoks(ArrayList<Integer> wantedEwoks) throws IllegalArgumentException{

        for(int i: wantedEwoks) {

            if(i <= Ewoks.getNumOfEwoks() && i >= 1){
                if (!mEwoks.getAvailability(i))
                    try {
                        wait();
                    } catch (InterruptedException ignored){}
            } else
                throw new IllegalArgumentException("serial number: " + i + " not in range");
        }
        // in case all Ewoks are available we acquire them
        for(int i: wantedEwoks) {
            mEwoks.acquireEwok(i);
        }
    }

    /**
     * a synchronized method to release Ewoks.
     * @param wantedEwoks an array list of ALL Ewoks that need to be released.
     * @return false if at least one of the Ewoks is already available
     * else return true
     * @throws IllegalArgumentException in case the serial number {@code i} is not in range
     */
    public synchronized void releaseEwoks(ArrayList<Integer> wantedEwoks) throws IllegalArgumentException {

        for(int serialNumber: wantedEwoks){
            if (serialNumber > mNumOfEwoks || serialNumber < 0)
                throw new IllegalArgumentException("serial number: " + serialNumber + " not in range");

            // when releasing an EWOK the thread will notify all threads who are waiting to acquire resources
            mEwoks.releaseEwok(serialNumber);
            notifyAll();
        }
    }
}
