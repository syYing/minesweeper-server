package me.lucien.minesweeper.web;

import me.lucien.minesweeper.domain.Room;
import me.lucien.minesweeper.domain.SquareData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {

    private Map<Integer, Room> roomMap = new HashMap<>();

    @Value("${room.capacity}")
    private int capacity;

    @PostMapping("/room")
    public String createRoom(@RequestParam("width") int width, @RequestParam("height") int height) throws HttpException {
        if (roomMap.size() >= capacity) {
            throw  new HttpException(HttpStatus.SERVICE_UNAVAILABLE, "No available room.");
        }

        Room room = new Room(width, height);
        roomMap.put(room.getId(), room);

        JSONObject o = new JSONObject();
        try {
            o.put("roomId", room.getId());
            o.put("roomKey", room.getKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o.toString();
    }

    @GetMapping("/room/{roomId}")
    public List<SquareData> enterRoom(@PathVariable("roomId") int roomId,
                                      @RequestParam("roomKey") String roomKey) throws HttpException {
        checkLegitimacy(roomId, roomKey);

        Room room = roomMap.get(roomId);

        return room.getGameData();
    }

    @DeleteMapping("/room/{roomId}")
    public void deleteRoom(@PathVariable("roomId") int roomId,
                           @RequestParam("roomKey") String roomKey) throws HttpException {
        checkLegitimacy(roomId, roomKey);
        roomMap.remove(roomId);
    }

    @PostMapping("/room/{roomId}/square/{x}/{y}")
    public List<SquareData> leftClick(@PathVariable("roomId") int roomId, @PathVariable("x") int x, @PathVariable("y") int y,
                                      @RequestParam("roomKey") String roomKey) throws HttpException {
        checkLegitimacy(roomId, roomKey);
        Room room = roomMap.get(roomId);

        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        List<SquareData> res = room.uncover(x, y);

        return res;
    }

    @PatchMapping("/room/{roomId}/square/{x}/{y}")
    public void rightClick(@PathVariable("roomId") int roomId, @PathVariable("x") int x, @PathVariable("y") int y,
                           @RequestParam("roomKey") String roomKey) throws HttpException {
        checkLegitimacy(roomId, roomKey);
        Room room = roomMap.get(roomId);

        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        room.flag(x, y);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity handleHttpException(HttpException e) {
        return new ResponseEntity(e.getMessage(), e.getStatus());
    }

    private void checkLegitimacy(int roomId, String roomKey) throws HttpException {
        if (!roomMap.containsKey(roomId)) {
            throw new HttpException(HttpStatus.NOT_FOUND, "The room does't exist.");
        }

        Room room = roomMap.get(roomId);

        if (room.getKey() != roomKey) {
            throw new HttpException(HttpStatus.FORBIDDEN, "The roomKey is wrong.");
        }
    }
}
