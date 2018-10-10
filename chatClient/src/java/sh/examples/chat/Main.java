package sh.examples.chat;

import sh.examples.chat.client.ClientConnector;

public class Main {
    public static void main(String[] args) {
        ClientConnector connector = new ClientConnector("127.0.0.1", 8888);
        connector.start();
    }
}