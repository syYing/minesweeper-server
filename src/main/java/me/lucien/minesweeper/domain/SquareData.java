package me.lucien.minesweeper.domain;

public class SquareData {

    private int x;
    private int y;
    private int num;

    public SquareData(int x, int y, int num) {
        this.x = x;
        this.y = y;
        this.num = num;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNum() {
        return num;
    }
}
