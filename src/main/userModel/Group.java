package main.userModel;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final int ID;
    private final String name;
    private List<String> participants;

    public Group(int ID, String name) {
        this.ID = ID;
        this.name = name;
        this.participants = new ArrayList<>();
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants, String activeUsername) {
        // all group participants without active user
        this.participants = participants;
        this.participants.remove(activeUsername);
    }

    @Override
    public String toString() {
        return name;
    }
}
