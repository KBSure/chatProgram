package bs.examples.chat;

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
        return room.getId();
    }

    public void removeRoom(int id) {
        roomMap.remove(id);
    }

    public Room getRoom(int id) {
        return roomMap.get(id);
    }

    public List<String> getRoomList() {
        List<String> roomInfos = new ArrayList<>();
        for (Integer key : roomMap.keySet()) {
            StringBuilder info = new StringBuilder();
            // 방번호|제목|인원수
            info.append(key).append("|").append(roomMap.get(key).getTitle()).append("|").append(roomMap.get(key).getUserSize());
            roomInfos.add(info.toString());
        }
        return roomInfos;
    }
}
