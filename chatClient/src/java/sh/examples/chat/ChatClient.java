package sh.examples.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient extends Thread {
    private String ip;
    private int port;
    private BufferedReader br;
    private PrintWriter pw;

    public ChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        BufferedReader keyBr = null;
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

            InputHandler inputHandler = new InputHandler(br);
            inputHandler.start();

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
}
