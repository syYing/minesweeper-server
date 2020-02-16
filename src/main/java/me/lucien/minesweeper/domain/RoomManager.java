package me.lucien.minesweeper.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class RoomManager {

    @Value("${room.capacity:1000}")
    private int capacity;

    @Value("${room.delay:3600}")
    private int delay;

    private ConcurrentMap<Integer, Room> roomMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public Room createRoom(int width, int height) {
        if (roomMap.size() >= capacity) {
            return null;
        }

        Room room = new Room(width, height);
        int id = room.getId();
        roomMap.put(id, room);
        resetSchedule(id);

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
        if (future != null) {
            future.cancel(false);
        }

        future = executorService.schedule(
                () -> {
                    roomMap.remove(id);
                    taskMap.remove(id);
                },
                delay, TimeUnit.SECONDS);
        taskMap.put(id, future);
    }
}
