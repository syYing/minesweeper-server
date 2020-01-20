package me.lucien.minesweeper.web;

import me.lucien.minesweeper.domain.Room;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GameController {

    private Room room;

    @PostMapping("/room")
    public void createRoom(@RequestBody String input) throws HttpException {
        if (input == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid Params");
        }

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = jsonParser.parseMap(input);

        this.room = new Room((int) map.get("width"), (int) map.get("height"));
    }

    @PostMapping("/square/{x}/{y}")
    public List<JSONObject> leftClick(@PathVariable("x") int x, @PathVariable("y") int y) {
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        List<JSONObject> res = leftClick(x, y);

        return res;
    }

    @PatchMapping("/square/{x}/{y}")
    public void rightClick(@PathVariable("x") int x, @PathVariable("y") int y) {
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        room.rightClick(x, y);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity handleHttpException(HttpException e) {
        return new ResponseEntity(e.getMessage(), e.getStatus());
    }
}
