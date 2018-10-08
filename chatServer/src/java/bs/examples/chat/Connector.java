package bs.examples.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connector {
    private int port;
    private Map<Integer, List<PrintWriter>> pwByRoomMap;

    public Connector(int port){
        this.port = port;
        pwByRoomMap = new HashMap<>();
    }

    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = null;
            while((socket = serverSocket.accept()) != null){

                Handler handler = new Handler(socket);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
