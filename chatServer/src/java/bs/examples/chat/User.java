package bs.examples.chat;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class User {

    private static int count;
    static{
        count = 0;
    }
    private int id;
    private String nickname;
    private PrintWriter pw;
    private BufferedReader br;
    private Room currentRoom;


    public User(PrintWriter pw, BufferedReader br){
        this.pw = pw;
        this.br = br;
        this.id = ++count;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void enterRoom(Room room) {
        this.currentRoom = room;
        this.currentRoom.inUser(this);
    }

    public void exitRoom() {
        this.currentRoom.outUser(this);
        this.currentRoom = null;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public PrintWriter getPrintWriter() {
        return pw;
    }
}
