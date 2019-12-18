package fr.polytech.sma;

public class Message {

    private String performatif;
    private String action;
    private int caseX;
    private int caseY;

    public Message(String performatif, String action, int caseX, int caseY) {
        this.performatif = performatif;
        this.action = action;
        this.caseX = caseX;
        this.caseY = caseY;
    }

    public String getPerformatif() {
        return performatif;
    }

    public String getAction() {
        return action;
    }

    public int getCaseX() {
        return caseX;
    }

    public int getCaseY() {
        return caseY;
    }
}
