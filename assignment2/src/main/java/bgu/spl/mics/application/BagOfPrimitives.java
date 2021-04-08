package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;

public class BagOfPrimitives {

    private Attack [] attacks;
    private long R2D2;
    private long Lando;
    private int Ewoks;

    public BagOfPrimitives() {}

    public Attack[] getAttacks() {
        return attacks;
    }
    public long getR2D2() {
        return R2D2;
    }
    public long getLando() {
        return Lando;
    }
    public int getEwoks() {
        return Ewoks;
    }

    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }
    public void setR2D2(long r2D2) {
        R2D2 = r2D2;
    }
    public void setLando(long lando) {
        Lando = lando;
    }
    public void setEwoks(int ewoks) {
        Ewoks = ewoks;
    }

}
