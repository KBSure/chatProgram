package bs.examples.chat.service;

import bs.examples.chat.domain.Room;
import bs.examples.chat.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomManager {
    private Map<Integer, Room> roomMap;

    public RoomManager() {
        roomMap = new HashMap<>();
    }

    public int makeRoom(String title, User user) {
        Room room = new Room(title);
        user.enterRoom(room);
        roomMap.put(room.getId(), room);
        System.out.printf("[%s] created %s\n", Thread.currentThread().getName(), room);
        return room.getId();
    }

    public void removeRoom(int id) {
        System.out.printf("[%s] removed %s\n", Thread.currentThread().getName(), roomMap.get(id));
        roomMap.remove(id);
    }

    public Room getRoom(int id) {
        return roomMap.get(id);
    }

    public List<Room> getRoomList() {
        List<Room> rooms = new ArrayList<>();
        for (Integer key : roomMap.keySet()) {
            rooms.add(roomMap.get(key));
        }
        return rooms;
    }
}
