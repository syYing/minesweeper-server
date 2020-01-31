package me.lucien.minesweeper.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SquareData {

    private int x;
    private int y;
    private int num;

    public int getNum() {
        return this.num;
    }
}
