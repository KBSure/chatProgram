package bs.examples.chat.service;

import bs.examples.chat.domain.Room;
import bs.examples.chat.domain.User;

import java.io.PrintWriter;
import java.util.List;

public class MessageCenter {
    private RoomManager roomManager;
    private final String IN = "님이 입장하셨습니다.";
    private final String OUT = "님이 퇴장하셨습니다.";

    public MessageCenter(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public void broadcast(int roomId, String message) {
        Room targetRoom = roomManager.getRoom(roomId);
        List<User> userList = targetRoom.getUserList();
        for (User user : userList) {
            PrintWriter pw = user.getPrintWriter();
            pw.println(message);
            pw.flush();
        }
    }

    public void enterNotice(int id, String nickname) {
        broadcast(id, nickname + IN);
    }

    public void exitNotice(int id, String nickname) {
        broadcast(id, nickname + OUT);
    }
}