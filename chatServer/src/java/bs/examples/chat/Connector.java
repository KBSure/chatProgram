package bs.examples.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector {
    private int port;

    public Connector(int port) {
        this.port = port;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = null;
            while ((socket = serverSocket.accept()) != null) {
                Handler handler = new Handler(socket);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
