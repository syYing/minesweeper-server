package me.lucien.minesweeper.domain;

import org.junit.jupiter.api.Test;

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

        room.spread(x, y, new ArrayList<SquareData>());

        for (int i = 0; i < cx.length; i++) {
            assertEquals(Square.State.UNCOVERED, board[x + cx[i]][y + cy[i]].getState());
        }
        assertEquals(Square.State.COVERED, board[3][3].getState());
    }

    @Test
    public void testUncover() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();
        int x = 1;
        int y = 1;
        board[x][y].setMine(true);

        assertEquals(64, room.uncover(x, y).size());

        int[] cx = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] cy = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

        for (int i = 0; i < cx.length; i++) {
            board[x + cx[i]][y + cy[i]].setMine(false);
        }

        for (int i = 0; i < 4; i++) {
            board[i][3].setMine(true);
            board[3][i].setMine(true);
        }

        assertEquals(9, room.uncover(x, y).size());
    }

    @Test
    public void testFlag() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();
        int x = 1;
        int y = 1;

        room.flag(x, y);
        assertEquals(Square.State.FLAGGED, board[x][y].getState());

        board[x][y].setState(Square.State.UNCOVERED);
        room.flag(x, y);
        assertEquals(Square.State.UNCOVERED, board[x][y].getState());
    }

    @Test
    public void testGameOver() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();

        List<SquareData> res = room.gameOver();
        assertEquals(64, res.size());

        board[1][1].setState(Square.State.UNCOVERED);
        res = room.gameOver();
        assertEquals(63, res.size());
    }

    @Test
    public void testGetGameData() {
        Room room = new Room(8, 8);
        Square[][] board = room.getBoard();
        room.flag(1, 1);
        room.flag(2, 2);
        List<SquareData> res = room.getGameData();

        for (int i = 0; i < res.size(); i++) {
            assertEquals(-1, res.get(i).getNum());
        }
    }
}
