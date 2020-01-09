package fr.polytech.sma;

import javafx.util.Pair;
import org.omg.CORBA.portable.IndirectionException;
import sun.font.DelegatingShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent extends Thread {

    private int X;
    private int Y;

    private int xEnd;
    private int yEnd;

    private int id;

    private Grille sharedGrille;
    private BoiteAuxLettres boiteAuxLettres;

    public Agent(int id, int xStart, int yStart, int xEnd, int yEnd) {
        this.X = xStart;
        this.Y = yStart;

        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.id = id;

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

            // Command other / propagate
            if (direction != null) {
                direction = commandIfNeeded(direction);
            }

            // Move if you want to
            if(direction != null) {
                move(direction);

                if(message != null) {
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    message = null;
                }
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
        if (msg.getPerformatif().equals("REQUEST") && msg.getAction().equals("MOVE")) {
            Dir direction = coreDirShortest();

            if (!sharedGrille.canMove(id, X, Y, X + direction.getMoveX(), Y + direction.getMoveY())) {
                List<Dir> list = new ArrayList<>();
                for(Dir dir: Dir.values()) {
                    if (sharedGrille.canMove(id, X, Y, X + dir.getMoveX(), Y + dir.getMoveY()) && sharedGrille.getAgentId(X + dir.getMoveX(), Y + dir.getMoveY()) != msg.getId()) {
                        list.add(dir);
                    }
                }

                if (list.size() > 0) {
                    return list.get(new Random().nextInt(list.size()));
                } else {

                    if (sharedGrille.getAgentId(X + direction.getMoveX(), Y + direction.getMoveY()) == -1 || sharedGrille.getAgentId(X + direction.getMoveX(), Y + direction.getMoveY()) == msg.getId()) {
                        return randomDir();
                    }
                }
            }

            return direction;
        }
        else return null;
    }

    public Dir randomDir() {

        Dir direction;

        switch (new Random().nextInt(4)) {
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

        Dir direction;

        if (distX > distY) {
            direction =  (X - xEnd < 0 ? Dir.BOT : Dir.TOP);
            if (!sharedGrille.canMove(id, X, Y, X + direction.getMoveX(), Y + direction.getMoveY()) && distY != 0) {
                direction = (Y - yEnd < 0 ? Dir.RIGHT : Dir.LEFT);
            }
        } else {
            direction = (Y - yEnd < 0 ? Dir.RIGHT : Dir.LEFT);
            if (!sharedGrille.canMove(id, X, Y, X + direction.getMoveX(), Y + direction.getMoveY()) && distX != 0) {
                direction =  (X - xEnd < 0 ? Dir.BOT : Dir.TOP);
            }
        }

        return direction;
    }

    public Dir commandIfNeeded(Dir direction) {

        int agentId = sharedGrille.getAgentId(X + direction.getMoveX(), Y + direction.getMoveY());
        if (agentId > 0) {

            boiteAuxLettres.envoyerMsg(agentId,
                    new Message("REQUEST", "MOVE", id));

            int counter = 0;
            while (counter < 200 && !sharedGrille.canMove(id, X, Y, X + direction.getMoveX(), Y + direction.getMoveY())) {
                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                counter +=5;
            }
        }

        return direction;
    }
}
