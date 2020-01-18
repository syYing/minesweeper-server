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

    public Room(int height, int width) {
        this.height = height;
        this.width = width;
        this.board = new Square[this.height][this.width];
        this.mineNum = calculateMineNum(height, width);
        initialize();
    }

    public static int calculateMineNum(int height, int width) {
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

    public List<JSONObject> click(int x, int y) {
        List<JSONObject> res = new ArrayList<>();

        if (board[x][y].isMine()) {
            return res;
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
        board[x][y].setState(Square.State.UNCONVERED);

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
}
