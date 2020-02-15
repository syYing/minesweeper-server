package me.lucien.minesweeper.web;

import me.lucien.minesweeper.domain.RoomManager;
import me.lucien.minesweeper.domain.SquareData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {

    @Autowired
    private RoomManager roomManager;

    @PostMapping("/room")
    public String createRoom(@RequestParam("width") int width, @RequestParam("height") int height) throws HttpException {
        return roomManager.createRoom(width, height);
    }

    @GetMapping("/room/{id}")
    public List<SquareData> enterRoom(@PathVariable("id") int id,
                                      @RequestParam("key") String key) throws HttpException {
        return roomManager.enterRoom(id, key);
    }

    @DeleteMapping("/room/{id}")
    public void deleteRoom(@PathVariable("id") int id,
                           @RequestParam("key") String key) throws HttpException {
        roomManager.deleteRoom(id, key);
    }

    @PostMapping("/room/{id}/square/{x}/{y}")
    public List<SquareData> leftClick(@PathVariable("id") int id, @PathVariable("x") int x, @PathVariable("y") int y,
                                      @RequestParam("key") String key) throws HttpException {
        return roomManager.leftClick(id, key, x, y);
    }

    @PatchMapping("/room/{id}/square/{x}/{y}")
    public int rightClick(@PathVariable("id") int id, @PathVariable("x") int x, @PathVariable("y") int y,
                          @RequestParam("key") String key) throws HttpException {
        return roomManager.rightClick(id, key, x, y);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }
}
