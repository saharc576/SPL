package bgu.spl.mics.application.passiveObjects;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private final Ewok[] ewoksArr;
    private static int numOfEwoks;

    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();
    }


    private Ewoks() {
        ewoksArr = new Ewok[numOfEwoks + 1];
        for (int i = 0; i <= numOfEwoks; i++) {
            ewoksArr[i] = new Ewok(i);
        }
    }

    public static Ewoks getInstance(int numOfEwoks) {
        Ewoks.numOfEwoks = numOfEwoks;
        return SingletonHolder.instance;
    }

    public void acquireEwok (int serialNumber) throws IllegalArgumentException{
        // only one thread has the key for this class

        if (serialNumber > getNumOfEwoks() || serialNumber < 0)
            throw new IllegalArgumentException("serial number: " + serialNumber + " not in range");

        ewoksArr[serialNumber].acquire();
    }

    public static int getNumOfEwoks() {
        return numOfEwoks;
    }

    public void releaseEwok(int serialNumber) throws IllegalArgumentException{
        if (serialNumber >= ewoksArr.length || serialNumber < 0)
            throw new IllegalArgumentException("serial number: " + serialNumber + " not in range");

        ewoksArr[serialNumber].release();
    }

    public boolean getAvailability(int serialNumber) throws IllegalArgumentException{
        if (serialNumber >= ewoksArr.length || serialNumber < 0)
            throw new IllegalArgumentException("serial number: " + serialNumber + " not in range");

        return ewoksArr[serialNumber].getAvailability();
    }

}