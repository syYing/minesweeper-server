package me.lucien.minesweeper.web;

import me.lucien.minesweeper.domain.Room;
import me.lucien.minesweeper.domain.RoomManager;
import me.lucien.minesweeper.domain.SquareData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {

    @Autowired
    private RoomManager roomManager;

    @PostMapping("/room")
    public String createRoom(@RequestParam("width") int width, @RequestParam("height") int height) throws HttpException {
        Room room = roomManager.createRoom(width, height);
        if (room == null) {
            throw new HttpException(HttpStatus.SERVICE_UNAVAILABLE, "No available room.");
        }

        JSONObject o = new JSONObject();
        o.put("id", room.getId());
        o.put("key", room.getKey());
        o.put("mineNum", room.getMineNum());

        return o.toString();
    }

    @GetMapping("/room/{id}")
    public List<SquareData> enterRoom(@PathVariable("id") int id, @RequestParam("key") String key) throws HttpException {
        Room room = findRoom(id, key);

        return room.getGameData();
    }

    @DeleteMapping("/room/{id}")
    public void deleteRoom(@PathVariable("id") int id, @RequestParam("key") String key) throws HttpException {
        findRoom(id, key);

        roomManager.deleteRoom(id);
    }

    @PostMapping("/room/{id}/square/{x}/{y}")
    public List<SquareData> leftClick(@PathVariable("id") int id, @PathVariable("x") int x, @PathVariable("y") int y,
                                      @RequestParam("key") String key) throws HttpException {
        Room room = findRoom(id, key);
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        return room.uncover(x, y);
    }

    @PatchMapping("/room/{id}/square/{x}/{y}")
    public int rightClick(@PathVariable("id") int id, @PathVariable("x") int x, @PathVariable("y") int y,
                          @RequestParam("key") String key) throws HttpException {
        Room room = findRoom(id, key);
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }
        room.flag(x, y);

        return room.getBoard()[x][y].getState().ordinal();
    }

    @PostMapping("room/{id}/outspread")
    public List<SquareData> doubleClick(@PathVariable("id") int id, @RequestParam("key") String key,
                                        @RequestParam("x") int x, @RequestParam("y") int y) throws HttpException {
        Room room = findRoom(id, key);
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        return room.outspread(x, y);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    private Room findRoom(int id, String key) throws HttpException {
        Room room = roomManager.getRoom(id);
        if (room == null) {
            throw new HttpException(HttpStatus.NOT_FOUND, "The room does't exist.");
        }

        if (!room.getKey().equals(key)) {
            throw new HttpException(HttpStatus.FORBIDDEN, "The key is wrong.");
        }

        return room;
    }
}
