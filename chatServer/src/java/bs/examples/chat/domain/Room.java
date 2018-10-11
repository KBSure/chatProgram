package bs.examples.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public String getTitle() {
        return title;
    }

    public int getUserSize() {
        return users.size();
    }

    public void inUser(User user) {
        this.users.add(user);
    }

    public void outUser(User user) {
        this.users.remove(user);
    }

    @JsonIgnore
    public List<User> getUserList() {
        return users;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
