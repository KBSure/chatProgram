package bs.examples.chat.server;

import bs.examples.chat.service.Handler;
import bs.examples.chat.service.MessageCenter;
import bs.examples.chat.service.RoomManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector {
    private int port;
    private MessageCenter messageCenter;
    private RoomManager roomManager;

    public Connector(int port) {
        this.port = port;
        this.roomManager = new RoomManager();
        this.messageCenter = new MessageCenter(roomManager);
    }

    public void start() {
        System.out.println("[SERVER] get started");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = null;
            while ((socket = serverSocket.accept()) != null) {
                Handler handler = new Handler(socket, messageCenter, roomManager);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}