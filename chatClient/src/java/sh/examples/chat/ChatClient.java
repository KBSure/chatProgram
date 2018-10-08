package sh.examples.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient extends Thread {
    private String ip;
    private int port;
    private BufferedReader br;
    private PrintWriter pw;
    private BufferedReader keyBr;

    public ChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());

            System.out.println("< \\quit 입력 시 종료 >");
            System.out.print("닉네임 입력 : ");

            keyBr = new BufferedReader(new InputStreamReader(System.in));

            String nickName = keyBr.readLine();
            if ("\\quit".equals(nickName)) {
                return;
            }
            pw.println(nickName);
            pw.flush();

            //선택해야 함

            System.out.println("1. 방 만들기");
            System.out.println("2. 방 선택");
            System.out.println("3. 나가기");
            System.out.print("> ");
            int select = Integer.parseInt(keyBr.readLine());
            pw.println(select);
            pw.flush();

            System.out.println("---------------------------------");
            int roomId = 0;
            String title = null;
            switch (select) {
                case 1:
                    System.out.println("< 뒤로 가려면 \\back 입력 >");
                    //
                    System.out.print("방 이름 : ");
                    title = keyBr.readLine();
                    pw.println(title);
                    pw.flush();
                    roomId = Integer.parseInt(br.readLine());
                    break;
                case 2:
                    String info = "";
                    info = br.readLine().trim();
                    int size = Integer.parseInt(info);
                    Map<Integer, RoomInfo> roomInfoMap = new HashMap<>();
                    for (int i = 0; i < size; i++) {
                        info = br.readLine();
                        String[] token = info.split("|");
                        RoomInfo roomInfo = new RoomInfo(Integer.parseInt(token[0]), token[1], Integer.parseInt(token[2]));
                        roomInfoMap.put(roomInfo.id, roomInfo);
                        System.out.printf("%3d. %s [%d명]", roomInfo.id, roomInfo.title, roomInfo.userSize);
                    }
                    System.out.println("< 뒤로 가려면 \\back 입력 >");
                    System.out.print("입장할 방 번호 입력: ");
                    roomId = Integer.parseInt(keyBr.readLine());
                    pw.println(roomId);
                    pw.flush();
                    title = roomInfoMap.get(roomId).title;
                    break;
                case 3:
                    System.out.println("프로그램을 종료합니다.");
                    return;
            }

            enterRoom(roomId, title);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                keyBr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            pw.close();
        }
    }

    private void enterRoom(int roomId, String title) {
        String line = null;
        System.out.println("<" + title + ">에 입장했습니다.");

        InputHandler inputHandler = new InputHandler(br);
        inputHandler.start();

        try {
            System.out.print("> ");
            while ((line = keyBr.readLine()) != null) {
                pw.println(line);
                pw.flush();
                System.out.print("> ");
            }
        } catch (Exception ex) {

        }
    }

    private class RoomInfo {
        int id;
        String title;
        int userSize;

        public RoomInfo(int id, String title, int userSize) {
            this.id = id;
            this.title = title;
            this.userSize = userSize;
        }
    }
}
