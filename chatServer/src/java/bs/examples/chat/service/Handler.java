package bs.examples.chat.service;

import bs.examples.chat.domain.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
                            break;
                        case 3:
                            System.out.printf("[%s] connection closed\n", Thread.currentThread().getName());
                            return;
                    }
                }
                /*
                if (select == 2) {
                    List<String> roomInfos = roomManager.getRoomList();
                    pw.println(roomInfos.size());
                    for (String info : roomInfos) {
                        pw.println(info);
                    }
                    pw.flush();
                    // 답변을 받아야되
                    int roomId = Integer.parseInt(br.readLine());
                    // 유저를 방에 입장시켜
                    Room room = roomManager.getRoom(roomId);
                    user.enterRoom(room);
                }*/
            }


        } catch (IOException e) {}
    }

    private void startConversation() throws IOException {
        System.out.printf("[%s] start conversation\n", Thread.currentThread().getName());

        String format = String.format("[%s] ", user.getNickname());
        int roomId = user.getCurrentRoom().getId();
        String line;
        while ((line = br.readLine()) != null) {
            if ("\\quit".equals(line)) {
                pw.println("QUIT");
                pw.flush();
                System.out.printf("[%s] exit room %s\n", Thread.currentThread().getName(), user);
                user.exitRoom();
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
