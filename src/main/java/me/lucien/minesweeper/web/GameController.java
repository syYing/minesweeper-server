package me.lucien.minesweeper.web;

import me.lucien.minesweeper.domain.Room;
import me.lucien.minesweeper.domain.SquareData;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {

    private Map<Integer, Room> roomMap = new HashMap<>();

    @PostMapping("/room")
    public String createRoom(@RequestBody String input) throws HttpException {
        if (input == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid Params");
        }

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = jsonParser.parseMap(input);

        Room room = new Room((int) map.get("width"), (int) map.get("height"));
        roomMap.put(room.getRoomId(), room);

        JSONObject o = new JSONObject();
        try {
            o.put("roomId", room.getRoomId());
            o.put("roomKey", room.getRoomKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o.toString();
    }

    @PostMapping("/room/{roomId}/square/{x}/{y}")
    public List<SquareData> leftClick(@PathVariable("roomId") int roomId, @PathVariable("x") int x, @PathVariable("y") int y,
                                      @RequestBody String roomKey) throws HttpException {
        checkLegitimacy(roomId, roomKey, x, y);

        Room room = roomMap.get(roomId);
        List<SquareData> res = room.uncover(x, y);

        return res;
    }

    @PatchMapping("/room/{roomId}/square/{x}/{y}")
    public void rightClick(@PathVariable("roomId") int roomId, @PathVariable("x") int x, @PathVariable("y") int y,
                           @RequestBody String roomKey) throws HttpException {
        checkLegitimacy(roomId, roomKey, x, y);

        Room room = roomMap.get(roomId);
        room.flag(x, y);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity handleHttpException(HttpException e) {
        return new ResponseEntity(e.getMessage(), e.getStatus());
    }

    private void checkLegitimacy(int roomId, String roomKey, int x, int y) throws HttpException {
        if (!roomMap.containsKey(roomId)) {
            throw new HttpException(HttpStatus.NOT_FOUND, "The room does't exist.");
        }

        Room room = roomMap.get(roomId);

        if (room.getRoomKey() != roomKey) {
            throw new HttpException(HttpStatus.FORBIDDEN, "The roomKey is wrong.");
        }

        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }
    }
}
