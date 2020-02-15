package me.lucien.minesweeper.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
    private Map<Integer, ScheduledFuture<?>> taskMap = new HashMap<>();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public Room createRoom(int width, int height) {
        if (roomMap.size() >= capacity) {
            return null;
        }

        Room room = new Room(width, height);
        int id = room.getId();
        roomMap.put(id, room);

        ScheduledFuture<?> future = executorService.schedule(
                () -> deleteRoom(id),
                delay, TimeUnit.SECONDS);
        taskMap.put(room.getId(), future);

        return room;
    }

    public Room getRoom(int id) {
        if (!roomMap.containsKey(id)) {
            return null;
        } else {
            resetSchedule(id);

            return roomMap.get(id);
        }
    }

    public void deleteRoom(int id) {
        roomMap.remove(id);
        ScheduledFuture<?> future = taskMap.get(id);
        future.cancel(false);
        taskMap.remove(id);
    }

    private void resetSchedule(int id) {
        ScheduledFuture<?> future = taskMap.get(id);
        future.cancel(false);

        future = executorService.schedule(
                () -> deleteRoom(id),
                delay, TimeUnit.SECONDS);
        taskMap.put(id, future);
    }
}
