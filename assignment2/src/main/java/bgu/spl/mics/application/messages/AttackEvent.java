package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.ArrayList;
import java.util.List;

public class AttackEvent implements Event<Boolean> {
    final List<Integer> serials;
    final int duration;

    public AttackEvent(Attack attack) {
        this.serials = attack.getSerials();
        this.duration=attack.getDuration();
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<Integer> getSerials() {
        return new ArrayList<Integer>(serials);
    }

}
