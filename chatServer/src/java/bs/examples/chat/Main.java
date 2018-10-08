package bs.examples.chat;

public class Main {
    public static void main(String[] args) {
        Connector connector = new Connector(8888);
        connector.start();
    }
}
