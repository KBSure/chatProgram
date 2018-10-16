package sh.examples.chat.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import sh.examples.chat.domain.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnector extends Thread {
    private String ip;
    private int port;
    private BufferedReader keyBr;
    private BufferedReader br;
    private PrintWriter pw;
    private String nickname;

    public ClientConnector(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            keyBr = new BufferedReader(new InputStreamReader(System.in));

            if (!registerNickname()) return;

            while (true) {
                switch (selectMainMenu()) {
                    case 1:
                        while (true) {
                            System.out.print("\n방 이름 (로비로 돌아가려면 \\back 입력) : ");
                            // TODO 인원수제한 설정 추가하기
                            String title = keyBr.readLine().trim();

                            if ("\\back".equals(title)) {
                                pw.println("BACK");
                                pw.flush();
                                break;
                            }

                            if (title.length() == 0) {
                                System.out.println("\n[WARNING] 방 이름에는 공백이 올 수 없습니다.\n");
                                continue;
                            }

                            System.out.println("\n\""+title+"\" (으)로 새 방을 만드시겠습니까?");
                            System.out.print("[Y/N] > ");
                            String yn = keyBr.readLine().trim();
                            if(!"Y".equals(yn.toUpperCase())){
                                pw.println("BACK");
                                pw.flush();
                                break;
                            }

                            pw.println("CREATE_ROOM");
                            pw.println(title);
                            pw.flush();
                            String line;
                            int roomId = 0;
                            while ((line = br.readLine()) != null) {
                                if ("CREATED".equals(line)) {
                                    roomId = Integer.parseInt(br.readLine());
                                    break;
                                }
                            }
                            enterRoom(roomId, title);
                            break;
                        }
                        break;

                    case 2:
                        pw.println("ROOM_LIST");
                        pw.flush();
                        ObjectMapper objectMapper = new ObjectMapper();
                        while (true) {
                            if ("ROOM_LIST".equals(br.readLine())) {
                                System.out.println("\n============ 방 목록 ============");
                                int roomListSize = Integer.parseInt(br.readLine());
                                if (roomListSize == 0) {
                                    System.out.println("현재 개설된 방이 없습니다.");
                                    System.out.println("(뒤로가려면 \"\\back\" 입력)");
                                    while (true) {
                                        String line = keyBr.readLine().trim();
                                        if ("\\back".equals(line.toLowerCase())) {
                                            pw.println("BACK");
                                            pw.flush();
                                            break;
                                        }
                                    }
                                    break;
                                }
                                for (int i = 0; i < roomListSize; i++) {
                                    String line = br.readLine();
                                    Room room = objectMapper.readValue(line, Room.class);
                                    System.out.printf("%3d. %s [%d명]", room.getId(), room.getTitle(), room.getUserSize());
                                }
                                System.out.println();
                                int roomId;
                                while (true) {
                                    String line2 = "";
                                    System.out.print("\n번호 선택 > ");
                                    try {
                                        line2 = keyBr.readLine().trim();
                                        roomId = Integer.parseInt(line2);
                                    } catch (NumberFormatException ex) {
                                        if ("\\back".equals(line2.toLowerCase())) {
                                            pw.println("BACK");
                                            pw.flush();
                                            break;
                                        }
                                        System.out.println("\n[WARNING] 유효하지 않은 번호 선택\n");
                                        System.out.println("(로비로 돌아가려면 \"\\back\" 입력)\n");
                                        continue;
                                    }
                                    if(roomId > roomListSize || roomId < 1){
                                        if ("\\back".equals(line2.toLowerCase())) {
                                            pw.println("BACK");
                                            pw.flush();
                                            break;
                                        }
                                        System.out.println("\n[WARNING] 유효하지 않은 번호 선택\n");
                                        System.out.println("(로비로 돌아가려면 \"\\back\" 입력)\n");
                                        continue;
                                    }
                                    //입장시키기
                                    pw.println("SELECT_ROOM");
                                    pw.println(roomId);
                                    pw.flush();
                                    while (true) {
                                        if ("JOINED".equals(br.readLine())) {
                                            enterRoom(roomId, br.readLine());
                                            break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                        break;
                    case 3:
                        printExitMessage();
                        return;
                }
            }
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

    private void printExitMessage() {
        System.out.println("\n프로그램을 종료합니다.");
    }

    private boolean registerNickname() throws IOException {
        System.out.println("시작하려면 닉네임을 먼저 입력해주세요.");
        System.out.println("---------------------------------");

        while (true) {
            System.out.print("닉네임 입력 (\\quit 입력 시 종료) > ");
            nickname = keyBr.readLine().trim();
            if (nickname.length() == 0) {
                System.out.println("\n[WARNING] 닉네임에는 공백이 올 수 없습니다\n");
                continue;
            }
            break;
        }

        if ("\\quit".equals(nickname)) {
            pw.println("EXIT");
            pw.flush();
            while (true) {
                if ("EXIT".equals(br.readLine())) {
                    return false;
                }
            }
        }

        pw.println("NICKNAME");
        pw.println(nickname);
        pw.flush();

        return true;
    }

    private int selectMainMenu() throws IOException {
        System.out.println();
        System.out.println();

        System.out.println("  _           _     _           \n" +
                            " | |         | |   | |          \n" +
                            " | |     ___ | |__ | |__  _   _ \n" +
                            " | |    / _ \\| '_ \\| '_ \\| | | |\n" +
                            " | |___| (_) | |_) | |_) | |_| |\n" +
                            " |______\\___/|_.__/|_.__/ \\__, |\n" +
                            "                           __/ |\n" +
                            "                          |___/ ");
        System.out.println();
        System.out.println("1. 방 만들기");
        System.out.println("2. 방 선택");
        System.out.println("3. 나가기");
        System.out.println("---------------------------------");
        int selected;
        while (true) {
            System.out.print("번호 선택 > ");
            try {
                selected = Integer.parseInt(keyBr.readLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("\n[WARNING] 유효하지 않은 번호 선택\n");
                continue;
            }
            if (selected < 0 || selected > 3) {
                System.out.println("\n[WARNING] 유효하지 않은 번호 선택\n");
                continue;
            }
            break;
        }
        pw.println("MAIN_MENU");
        pw.println(selected);
        pw.flush();
        return selected;
    }

    private void enterRoom(int roomId, String title) throws IOException {
        System.out.println("\n*********************************");
        System.out.println("*********************************");
        System.out.println("\"" + title + "\"에 입장하였습니다.");
        System.out.println("(나가기: \\quit)\n");

        InputHandler inputHandler = new InputHandler(br);
        inputHandler.start();

        String line;
        while ((line = keyBr.readLine()) != null) {
            pw.println(line);
            pw.flush();
            if ("\\quit".equals(line.trim())) {
                break;
            }
        }
    }
}