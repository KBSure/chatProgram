package bs.examples.chat;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Handler extends Thread{
    private MessageCenter messageCenter;
    private RoomManager roomManager;
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private User user;

    public Handler(Socket socket) {
        this.socket = socket;
        try {
            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            user = new User(pw, br);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run() {
        String line = "";
        try {
            line = br.readLine().trim();
            if ("\\quit".equals(line)) {
                return;
            } else {
                user.setNickname(line);
            }

            while ((line = br.readLine().trim()) != null) {
                // 1. 방 만들기
                // 2. 방 선택
                // 3. 나가기
                int select = Integer.parseInt(line); // 숫자가 아닌게 올수도 있다

                if (select == 1) {
                    //"방이름을 입력하시오"
                    //"뒤로가기 \back"
                    if ("\\back".equals(line)) {
                        continue;
                    }
                    String title = line; // 인원수제한 기능 추가할 수 있음
                    int roomId = roomManager.makeRoom(title, user);
                    pw.println(roomId);
                    pw.flush();
                }
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
                }
                if (select == 3) {
                    // "접속을 종료합니다"
                    return;
                }
            }


        } catch (IOException e) {}
    }
}
