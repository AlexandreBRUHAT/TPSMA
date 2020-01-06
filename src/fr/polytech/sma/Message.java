package fr.polytech.sma;

public class Message {

    private String performatif;
    private String action;
    private int id;
    private int propagation;

    public Message(String performatif, String action, int id, int propagation) {
        this.performatif = performatif;
        this.action = action;
        this.id = id;
        this.propagation = propagation;
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

    public int getPropagation() {
        return propagation;
    }
}
