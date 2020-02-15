package me.lucien.minesweeper.domain;

import me.lucien.minesweeper.web.HttpException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class RoomManager {

    @Value("${room.capacity}")
    private int capacity;

    @Value("${room.delay}")
    private int delay;

    private Map<Integer, Room> roomMap = new HashMap<>();
    private Map<Integer, ScheduledFuture> taskMap = new HashMap<>();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public String createRoom(int width, int height) throws HttpException {
        if (roomMap.size() >= capacity) {
            throw new HttpException(HttpStatus.SERVICE_UNAVAILABLE, "No available room.");
        }

        Room room = new Room(width, height);
        roomMap.put(room.getId(), room);

        ScheduledFuture future = executorService.schedule(
                () -> { roomMap.remove(room.getId()); }, delay, TimeUnit.SECONDS);
        taskMap.put(room.getId(), future);

        JSONObject o = new JSONObject();
        o.put("id", room.getId());
        o.put("key", room.getKey());
        o.put("mineNum", room.getMineNum());

        return o.toString();
    }

    public List<SquareData> enterRoom(int id, String key) throws HttpException {
        checkLegitimacy(id, key);
        resetSchedule(id);

        Room room = roomMap.get(id);

        return room.getGameData();
    }

    public void deleteRoom(int id, String key) throws HttpException {
        checkLegitimacy(id, key);
        roomMap.remove(id);
    }

    public List<SquareData> leftClick(int id, String key, int x, int y) throws HttpException {
        checkLegitimacy(id, key);
        resetSchedule(id);

        Room room = roomMap.get(id);
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        List<SquareData> res = room.uncover(x, y);

        return res;
    }

    public int rightClick(int id, String key, int x, int y) throws HttpException {
        checkLegitimacy(id, key);
        resetSchedule(id);

        Room room = roomMap.get(id);
        if (x < 0 || x >= room.getWidth() || y < 0 || y >= room.getHeight()) {
            throw new IndexOutOfBoundsException();
        }

        room.flag(x, y);

        return room.getBoard()[x][y].getState().ordinal();
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

    private void resetSchedule(int id) {
        ScheduledFuture future = taskMap.get(id);
        while (!future.isCancelled()) {
            future.cancel(true);
        }

        future = executorService.schedule(() -> { roomMap.remove(id); }, delay, TimeUnit.SECONDS);
        taskMap.put(id, future);
    }
}
