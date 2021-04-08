package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */

public class Diary {
    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;


    private static class SingletonHolder {
        private static Diary instance = new Diary();
    }

    private Diary() {
        totalAttacks = new AtomicInteger(0);
    }

    public static Diary getInstance(){
        return Diary.SingletonHolder.instance;
    }


    public void incTotalAttacks() {
        totalAttacks.incrementAndGet();
    }

    public void setTotalAttacks () {
        totalAttacks = new AtomicInteger(0);
    }
    public void setHanSoloFinish(long timeStamp){
        this.HanSoloFinish = timeStamp;
    }
    public void setC3POFinish(long timeStamp){
        this.C3POFinish = timeStamp;
    }
    public void setR2D2Deactivate(long timeStamp){
        this.R2D2Deactivate = timeStamp;
    }
    public void setLeiaTerminate(long timeStamp){
        this.LeiaTerminate = timeStamp;
    }
    public void setHanSoloTerminate(long timeStamp){
        this.HanSoloTerminate = timeStamp;
    }
    public void setR2D2Terminate(long timeStamp){
        this.R2D2Terminate = timeStamp;
    }
    public void setC3POTerminate(long timeStamp){
        this.C3POTerminate = timeStamp;
    }
    public void setLandoTerminate(long timeStamp){
        this.LandoTerminate = timeStamp;
    }

    public AtomicInteger getTotalAttacks(){
        return totalAttacks;
    }
    public long getHanSoloFinish(){
        return HanSoloFinish;
    }
    public long getC3POFinish(){
        return C3POFinish;
    }
    public long getR2D2Deactivate(){
        return R2D2Deactivate;
    }
    public long getLeiaTerminate(){
        return LeiaTerminate;
    }
    public long getHanSoloTerminate(){
        return HanSoloTerminate;
    }
    public long getR2D2Terminate(){
        return R2D2Terminate;
    }
    public long getC3POTerminate(){
        return C3POTerminate;
    }
    public long getLandoTerminate(){
        return LandoTerminate;
    }

}
