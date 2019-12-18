package fr.polytech.sma;

import org.omg.CORBA.portable.IndirectionException;

import java.util.Random;

public class Agent extends Thread {

    private int X;
    private int Y;

    private int xEnd;
    private int yEnd;

    private int id;

    private boolean waitingForMovement;


    private Grille sharedGrille;
    private BoiteAuxLettres boiteAuxLettres;

    public Agent(int id, int xStart, int yStart, int xEnd, int yEnd) {
        this.X = xStart;
        this.Y = yStart;

        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.id = id;

        waitingForMovement = false;

        sharedGrille = Grille.getInstance();
        boiteAuxLettres = BoiteAuxLettres.getInstance();

        sharedGrille.addAgent(this);
    }

    @Override
    public void run() {

        Dir direction = Dir.BOT;

        while (!sharedGrille.isEnded()) {

            // Communication -> Décision -> Déplacement
            direction = null;
            Message message = boiteAuxLettres.lireMsg(id);

            if (message != null) {
                // If message read it
                direction = interpMessage(message);

            } else {
                // Else chose your own dir
                direction = choseDir();
            }

            if(direction != null) {
                // Move if you want to
                move(direction);
                // You moved so you're not waiting anymore and you can send new messages without spamming for nothing
                waitingForMovement  = false;
            }

            sharedGrille.afficherGrille();
        }

        System.out.println("NB coups = " + sharedGrille.getCoups());
    }

    public void move(Dir dir) {
        if (sharedGrille.move(id, X, Y, X + dir.getMoveX(), Y + dir.getMoveY())) {
            X = X + dir.getMoveX();
            Y = Y + dir.getMoveY();
        }
    }

    public boolean isArrived() {
        return X == xEnd && Y == yEnd;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getxEnd() {
        return xEnd;
    }

    public int getyEnd() {
        return yEnd;
    }

    public int getMyId() {
        return id;
    }

    /*Actions
        - Bouger
        - Calculer son chemin
        - Communiquer
        -

     */

    public Dir interpMessage(Message msg) {
        if (msg.getPerformatif().equals("REQUEST") && msg.getAction().equals("MOVE"))
            return randomDir();
        else return null;
    }

    public Dir randomDir() {

        Dir direction;

        switch (new Random().nextInt() % 4) {
            case 0: direction = Dir.BOT;
                break;
            case 1: direction = Dir.TOP;
                break;
            case 2: direction = Dir.LEFT;
                break;
            default: direction = Dir.RIGHT;
                break;
        }

        return direction;
    }

    public Dir choseDir() {
        Dir direction;

        if (isArrived()) {
            direction = null;
        } else {
            int distX = Math.abs(X - xEnd);
            int distY = Math.abs(Y - yEnd);

            if (distX > distY) {
                direction = (X - xEnd < 0 ? Dir.BOT : Dir.TOP);
            } else {
                direction = (Y - yEnd < 0 ? Dir.RIGHT : Dir.LEFT);
            }
        }

        if (direction != null) {
            int agentId = sharedGrille.getAgentId(X + direction.getMoveX(), Y + direction.getMoveY());
            if (agentId != 0 && !waitingForMovement) {

                boiteAuxLettres.envoyerMsg(agentId,
                        new Message("REQUEST", "MOVE", X + direction.getMoveX(), Y + direction.getMoveY()));
                waitingForMovement = true;
                direction = null;
            }
        }

        return direction;
    }
}
