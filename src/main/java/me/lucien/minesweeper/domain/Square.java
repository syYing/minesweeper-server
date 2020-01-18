package me.lucien.minesweeper.domain;

import lombok.Data;

@Data
public class Square {

    private boolean isMine = false;
    private State state = State.COVERED;

    public static enum State {
        COVERED, UNCONVERED, FLAGED
    }
}
