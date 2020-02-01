package me.lucien.minesweeper.domain;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {

    private static int roomId = 1;

    private int id;
    private String key;

    private int width;
    private int height;
    private int mineNum;
    private Square[][] board;
    private int traveledNum;

    public Room(int width, int height) {
        this.id = roomId++;

        byte[] bytes = new byte[32];
        new Random().nextBytes(bytes);
        this.key = new String(bytes, Charset.forName("UTF-8"));

        this.width = width;
        this.height = height;
        this.board = new Square[this.height][this.width];
        this.mineNum = calculateMineNum(height, width);
        this.traveledNum = 0;
        initialize();
    }

    public static int calculateMineNum(int width, int height) {
        return height * width * 10 / 64;
    }

    public void initialize() {
        for (Square[] row : board) {
            for (int i = 0; i < row.length; i++) {
                row[i] = new Square();
            }
        }

        for (int i = 0; i < this.mineNum; i++) {
            int x = (int)(Math.random() * this.height);
            int y = (int)(Math.random() * this.width);

            while (board[x][y].isMine()) {
                x = (int)(Math.random() * this.height);
                y = (int)(Math.random() * this.width);
            }

            board[x][y].setMine(true);
        }
    }

    public List<SquareData> uncover(int x, int y) {
        List<SquareData> res = new ArrayList<>();

        if (board[x][y].isMine()) {
            return gameOver();
        }

        spread(x, y, res);

        return res;
    }

    public void spread(int x, int y, List<SquareData> res) {
        if (x < 0 || x >= this.height || y < 0 || y >= this.width || board[x][y].getState() != Square.State.COVERED) {
            return;
        }

        int num = count(x, y);
        res.add(new SquareData(x, y, num));
        board[x][y].setState(Square.State.UNCOVERED);

        if (num == 0) {
            int[] cx = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] cy = {-1, 0, 1, -1, 1, -1, 0, 1};

            for (int i = 0; i < cx.length; i++) {
                int newx = x + cx[i];
                int newy = y + cy[i];

                spread(newx, newy, res);
            }
        }
    }

    public int count(int x, int y) {
        int[] cx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] cy = {-1, 0, 1, -1, 1, -1, 0, 1};
        int num = 0;

        for (int i = 0; i < cx.length; i++) {
            int newx = x + cx[i];
            int newy = y + cy[i];

            if (newx < 0 || newx >= this.height || newy < 0 || newy >= this.width) {
                continue;
            }

            if (board[newx][newy].isMine()) {
                num++;
            }
        }

        return num;
    }

    public void flag(int x, int y) {
        if (board[x][y].getState() == Square.State.COVERED) {
            board[x][y].setState(Square.State.FLAGGED);
        }
    }

    public List<SquareData> gameOver() {
        List<SquareData> res = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getState() != Square.State.UNCOVERED) {
                    if (board[i][j].isMine()) {
                        res.add(new SquareData(i, j, -1));
                    } else {
                        res.add(new SquareData(i, j, count(i, j)));
                    }
                }
            }
        }

        return res;
    }

    public List<SquareData> getGameData() {
        List<SquareData> res = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getState() == Square.State.UNCOVERED) {
                    res.add(new SquareData(i, j, count(i, j)));
                } else if (board[i][j].getState() == Square.State.FLAGGED) {
                    res.add(new SquareData(i, j, -1));
                }
            }
        }

        return res;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Square[][] getBoard() {
        return board;
    }
}
