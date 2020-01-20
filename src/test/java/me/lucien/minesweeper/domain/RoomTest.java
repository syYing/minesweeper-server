package me.lucien.minesweeper.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    @Test
    public void testCalculateMineNum() {
        int num = Room.calculateMineNum(8, 8);
        assertEquals(10, num);
    }

    @Test
    public void testInitialize() {
        Room room = new Room(8, 8);
        int num = 0;

        for (Square[] row : room.getBoard()) {
            for (Square square : row) {
                if (square.isMine()) {
                    num++;
                }
            }
        }

        assertEquals(10, num);
    }

    @Test
    public void testCount() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();
        int x = 1;
        int y = 1;
        int[] cx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] cy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < cx.length; i++) {
            board[x + cx[i]][y + cy[i]].setMine(true);
        }
        assertEquals(8, room.count(x, y));

        board[1][0].setMine(false);
        assertEquals(7, room.count(x, y));
    }

    @Test
    public void testSpread() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();
        int x = 1;
        int y = 1;
        int[] cx = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] cy = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

        for (int i = 0; i < cx.length; i++) {
            board[x + cx[i]][y + cy[i]].setMine(false);
        }

        for (int i = 0; i < 4; i++) {
            board[i][3].setMine(true);
            board[3][i].setMine(true);
        }

        room.spread(x, y, new ArrayList<JSONObject>());

        for (int i = 0; i < cx.length; i++) {
            assertTrue(board[x + cx[i]][y + cy[i]].getState() == Square.State.UNCOVERED);
        }
        assertTrue(board[3][3].getState() == Square.State.COVERED);
    }

    @Test
    public void testLeftClick() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();
        int x = 1;
        int y = 1;
        board[x][y].setMine(true);

        assertTrue(room.leftClick(x, y).size() == 64);

        int[] cx = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] cy = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

        for (int i = 0; i < cx.length; i++) {
            board[x + cx[i]][y + cy[i]].setMine(false);
        }

        for (int i = 0; i < 4; i++) {
            board[i][3].setMine(true);
            board[3][i].setMine(true);
        }

        assertTrue(room.leftClick(x, y).size() == 9);
    }

    @Test
    public void testRightClick() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();
        int x = 1;
        int y = 1;

        room.rightClick(x, y);
        assertTrue(board[x][y].getState() == Square.State.FLAGED);

        board[x][y].setState(Square.State.UNCOVERED);
        room.rightClick(x, y);
        assertTrue(board[x][y].getState() == Square.State.UNCOVERED);
    }

    @Test
    public void testGameOver() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();

        List<JSONObject> res = room.gameOver();
        assertTrue(res.size() == 64);

        board[1][1].setState(Square.State.UNCOVERED);
        res = room.gameOver();
        assertTrue(res.size() == 63);
    }
}
