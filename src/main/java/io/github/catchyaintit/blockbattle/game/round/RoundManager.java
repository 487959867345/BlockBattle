package io.github.catchyaintit.blockbattle.game.round;

public class RoundManager {
    private int round = 1;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    private States state = States.UNKNOWN;
}
