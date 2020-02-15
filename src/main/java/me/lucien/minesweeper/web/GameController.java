package me.lucien.minesweeper.web;

import me.lucien.minesweeper.domain.Room;
import me.lucien.minesweeper.domain.SquareData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class GameController {

    private Map<Integer, Room> roomMap = new HashMap<>();
    private Map<Integer, Timer> timerMap = new HashMap<>();

    @Value("${room.capacity}")
    private int capacity;

    @PostMapping("/room")
    public String createRoom(@RequestParam("width") int width, @RequestParam("height") int height) throws HttpException {
        if (roomMap.size() >= capacity) {
            throw  new HttpException(HttpStatus.SERVICE_UNAVAILABLE, "No available room.");
        }

        Room room = new Room(width, height);
        roomMap.put(room.getId(), room);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                roomMap.remove(room.getId());
            }
        }, 60 * 60 * 1000);
        timerMap.put(room.getId(), timer);

        JSONObject o = new JSONObject();
        o.put("id", room.getId());
        o.put("key", room.getKey());
        o.put("mineNum", room.getMineNum());

        return o.toString();
    }

    @GetMapping("/room/{id}")
    public List<SquareData> enterRoom(@PathVariable("id") int id, @RequestParam("key") String key) throws HttpException {
        checkLegitimacy(id, key);

        Room room = roomMap.get(id);
        reTimer(id);

        return room.getGameData();
    }

    @DeleteMapping("/room/{id}")
    public void deleteRoom(@PathVariable("id") int id, @RequestParam("key") String key) throws HttpException {
        checkLegitimacy(id, key);
        roomMap.remove(id);
    }

    @PostMapping("/room/{id}/square/{x}/{y}")
    public List<SquareData> leftClick(@PathVariable("id") int id, @PathVariable("x") int x, @PathVariable("y") int y,
                                      @RequestParam("key") String key) throws HttpException {
        checkLegitimacy(id, key);

        Room room = roomMap.get(id);
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }
        List<SquareData> res = room.uncover(x, y);

        reTimer(id);

        return res;
    }

    @PatchMapping("/room/{id}/square/{x}/{y}")
    public int rightClick(@PathVariable("id") int id, @PathVariable("x") int x, @PathVariable("y") int y,
                          @RequestParam("key") String key) throws HttpException {
        checkLegitimacy(id, key);

        Room room = roomMap.get(id);
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }
        room.flag(x, y);

        reTimer(id);

        return room.getBoard()[x][y].getState().ordinal();
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> handleHttpException(HttpException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    private void checkLegitimacy(int id, String key) throws HttpException {
        if (!roomMap.containsKey(id)) {
            throw new HttpException(HttpStatus.NOT_FOUND, "The room does't exist.");
        }

        Room room = roomMap.get(id);

        if (!room.getKey().equals(key)) {
            throw new HttpException(HttpStatus.FORBIDDEN, "The key is wrong.");
        }
    }

    public void reTimer(int id) {
        Timer timer = timerMap.get(id);
        timer.cancel();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                roomMap.remove(id);
            }
        }, 60 * 60 * 1000);
    }
}
