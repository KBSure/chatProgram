package bs.examples.chat;

import java.util.HashMap;
import java.util.Map;

public class MessageCenter {
    private Map<Integer, Room> rooms;

    public MessageCenter() {
        rooms = new HashMap<>();
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }
}