package main.controller;

public enum Status {
    added,  // when it was added in the current session
    deleted, // when it would be deleted from the current_list
    loaded, // when loaded from the shopping_list
    edited, //  when quantity changes
    none   // when other than above

}
