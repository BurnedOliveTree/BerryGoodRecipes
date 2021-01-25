package main.userModel;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final Integer ID;
    private final String name;
    private List<String> participants;

    public Group(Integer ID, String name) {
        this.ID = ID;
        this.name = name;
        this.participants = new ArrayList<>();
    }

    public Integer getID() {
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
    public boolean equals(Object r) {
        // overload of the base method comparing objects by ID
        if (r == this) {
            return true;
        } else if (!(r instanceof Group)) {
            return false;
        } else if (this.ID == null) {
            return ((Group) r).getID() == null;
        } else  {
            return this.ID.equals(((Group) r).getID());
        }
    }

    @Override
    public String toString() {
        return name;
    }
}