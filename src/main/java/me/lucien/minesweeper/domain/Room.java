package me.lucien.minesweeper.domain;

import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class Room {

    private int width;
    private int height;
    private int mineNum;
    private Square[][] board;
    private int traveledNum;

    public Room(int width, int height) {
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

    public List<JSONObject> leftClick(int x, int y) {
        List<JSONObject> res = new ArrayList<>();

        if (board[x][y].isMine()) {
            return gameOver();
        }

        spread(x, y, res);

        return res;
    }

    public void spread(int x, int y, List<JSONObject> res) {
        if (x < 0 || x >= this.height || y < 0 || y >= this.width || board[x][y].getState() != Square.State.COVERED) {
            return;
        }

        int num = count(x, y);
        JSONObject o = new JSONObject();
        try {
            o.put("x", x);
            o.put("y", y);
            o.put("num", num);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        res.add(o);
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

    public void rightClick(int x, int y) {
        if (board[x][y].getState() == Square.State.COVERED) {
            board[x][y].setState(Square.State.FLAGED);
        }
    }

    public List<JSONObject> gameOver() {
        List<JSONObject> res = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getState() != Square.State.UNCOVERED) {
                    JSONObject o = new JSONObject();
                    try {
                        o.put("x", i);
                        o.put("y", j);

                        if (board[i][j].isMine()) {
                            o.put("num", -1);
                        } else {
                            o.put("num", count(i, j));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    res.add(o);
                }
            }
        }

        return res;
    }
}
