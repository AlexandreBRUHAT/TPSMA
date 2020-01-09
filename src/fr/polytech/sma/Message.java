package fr.polytech.sma;

public class Message {

    private String performatif;
    private String action;
    private int id;

    public Message(String performatif, String action, int id) {
        this.performatif = performatif;
        this.action = action;
        this.id = id;
    }

    public String getPerformatif() {
        return performatif;
    }

    public String getAction() {
        return action;
    }

    public int getId() {
        return id;
    }
}
