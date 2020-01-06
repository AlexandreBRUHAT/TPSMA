package fr.polytech.sma;

import javafx.util.Pair;
import org.omg.CORBA.portable.IndirectionException;

import java.util.Random;

public class Agent extends Thread {

    private int X;
    private int Y;

    private int xEnd;
    private int yEnd;

    private int id;

    private boolean waitingForAnswer;


    private Grille sharedGrille;
    private BoiteAuxLettres boiteAuxLettres;

    public Agent(int id, int xStart, int yStart, int xEnd, int yEnd) {
        this.X = xStart;
        this.Y = yStart;

        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.id = id;

        waitingForAnswer = false;

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
            int propagation = 0;

            if (message != null) {
                // If message read it
                Pair<Dir, Integer> pair = interpMessage(message);

                if(pair != null) {
                    direction = pair.getKey();
                    propagation = pair.getValue();
                }

            } else {
                // Else chose your own dir
                direction = choseDir();
            }

            // Command other / propagate
            if (direction != null) {
                direction = commandIfNeeded(direction, propagation);
            }

            // Move if you want to
            if(direction != null) {
                move(direction);
                // You moved so you're not waiting anymore and you can send new messages without spamming for nothing
            }
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

    public Pair<Dir, Integer> interpMessage(Message msg) {
        if (msg.getPerformatif().equals("REQUEST") && msg.getAction().equals("MOVE") && msg.getPropagation() < 5) {
            boiteAuxLettres.envoyerMsg(msg.getId(),
                    new Message("ANSWER", "MOVE", id, 0));
            return new Pair<>(randomDir(), msg.getPropagation());
        }
        else if (msg.getPerformatif().equals("ANSWER")) {
            waitingForAnswer  = false;
            return null;
        }
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

            // Core of the chosing dir part -> Shortest without collisions / Shortest
            direction = coreDirShortest();
        }
        return direction;
    }

    public Dir coreDirShortest() {
        int distX = Math.abs(X - xEnd);
        int distY = Math.abs(Y - yEnd);

        if (distX > distY) {
            return  (X - xEnd < 0 ? Dir.BOT : Dir.TOP);
        } else {
            return (Y - yEnd < 0 ? Dir.RIGHT : Dir.LEFT);
        }
    }

    public Dir commandIfNeeded(Dir direction, int propagation) {

        int agentId = sharedGrille.getAgentId(X + direction.getMoveX(), Y + direction.getMoveY());
        if (agentId != 0 && !waitingForAnswer) {

            boiteAuxLettres.envoyerMsg(agentId,
                    new Message("REQUEST", "MOVE", id, propagation));
            waitingForAnswer = true;
            direction = null;
        }

        return direction;
    }
}
