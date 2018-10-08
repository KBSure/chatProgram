package bs.examples.chat;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private Map<Integer, Room> roomMap;

    public RoomManager() {
        roomMap = new HashMap<>();
    }

    public void makeRoom(String title, User user) {
        Room room = new Room(title);
        user.enterRoom(room);
        roomMap.put(room.getId(), room);
    }

    public void removeRoom(int id) {
        roomMap.remove(id);
    }

    public Room getRoom(int id) {
        return roomMap.get(id);
    }
}
