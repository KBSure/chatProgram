package bs.examples.chat;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private static int count;
    private int id;
    private String title;
    private List<User> users;

    public Room(String title) {
        this.title = title;
        users = new ArrayList<>();
        id = ++count;
    }

    public int getId() {
        return id;
    }

    public void addUser(User user) {
        this.users.add(user);
    }
}
