package me.lucien.minesweeper.domain;

public class Square {

    private boolean isMine = false;
    private State state = State.COVERED;

    public static enum State {
        COVERED, UNCOVERED, FLAGGED
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
