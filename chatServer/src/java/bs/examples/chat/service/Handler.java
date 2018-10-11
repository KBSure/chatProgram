package bs.examples.chat.service;

import bs.examples.chat.domain.Room;
import bs.examples.chat.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Handler extends Thread{
    private MessageCenter messageCenter;
    private RoomManager roomManager;
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private User user;

    public Handler(Socket socket, MessageCenter messageCenter, RoomManager roomManager) {
        this.messageCenter = messageCenter;
        this.roomManager = roomManager;
        this.socket = socket;
        try {
            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
        System.out.printf("[%s] connection established\n", Thread.currentThread().getName());
        try {
            if (!userCreated()){
                System.out.printf("[%s] connection closed\n", Thread.currentThread().getName());
                return;
            }

            while (true) {
                // 1. 방 만들기
                // 2. 방 선택
                // 3. 나가기
                if ("MAIN_MENU".equals(br.readLine())) {
                    int selected = Integer.parseInt(br.readLine());
                    switch (selected) {
                        case 1:
                            String line = br.readLine();
                            if ("BACK".equals(line)) {
                                continue;
                            }
                            if ("CREATE_ROOM".equals(line)) {
                                String title = br.readLine();
                                int roomId = roomManager.makeRoom(title, user);
                                pw.println("CREATED");
                                pw.println(roomId);
                                pw.flush();
                            }
                            startConversation();
                            break;
                        case 2:
                            line = br.readLine();
                            if ("ROOM_LIST".equals(line)) {
                                List<Room> roomList = roomManager.getRoomList();
                                ObjectMapper objectMapper = new ObjectMapper();
                                pw.println("ROOM_LIST");
                                pw.println(roomList.size());
                                for (Room room : roomList) {
                                    String json = objectMapper.writeValueAsString(room);
                                    pw.println(json);
                                }
                                pw.flush();
                            }

                            String s;
                            while ((s = br.readLine()) != null) {
                                if ("BACK".equals(s)) {
                                    break;
                                }
                                if ("SELECT_ROOM".equals(s)) {
                                    int roomId = Integer.parseInt(br.readLine());
                                    Room selectedRoom = roomManager.getRoom(roomId);
                                    user.enterRoom(selectedRoom);
                                    pw.println("JOINED");
                                    pw.println(selectedRoom.getTitle());
                                    pw.flush();
                                    startConversation();
                                    break;
                                }
                            }
                            break;
                        case 3:
                            System.out.printf("[%s] connection closed\n", Thread.currentThread().getName());
                            return;
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void startConversation() throws IOException {
        System.out.printf("[%s] enter room %s\n", Thread.currentThread().getName(), user);

        String format = String.format("[%s] ", user.getNickname());
        Room currentRoom = user.getCurrentRoom();
        int roomId = currentRoom.getId();
        messageCenter.enterNotice(roomId, user.getNickname());

        String line;
        while ((line = br.readLine()) != null) {
            if ("\\quit".equals(line)) {
                pw.println("QUIT");
                pw.flush();
                System.out.printf("[%s] exit room %s\n", Thread.currentThread().getName(), user);
                user.exitRoom();
                messageCenter.exitNotice(roomId, user.getNickname());
                if (currentRoom.getUserSize() == 0) {
                    roomManager.removeRoom(roomId);
                }
                return;
            }
            messageCenter.broadcast(roomId, format + line);
        }
    }

    private boolean userCreated() throws IOException {
        String line = br.readLine();

        if ("EXIT".equals(line)) {
            pw.println("EXIT");
            pw.flush();
            return false;
        }

        if ("NICKNAME".equals(line)) {
            user = new User(br.readLine(), pw, br);
            System.out.printf("[%s] created %s\n", Thread.currentThread().getName(), user);
        }

        return true;
    }
}
