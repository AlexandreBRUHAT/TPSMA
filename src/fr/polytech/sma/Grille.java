package fr.polytech.sma;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Grille {

    private static Grille instance;
    private int coups;

    private int[][] grille;

    private ArrayList<Agent> agents;

    public static Grille getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new Grille(Main.gridSize);
            return instance;
        }
    }

    private Grille(int gridSize) {
        grille = new int[gridSize][gridSize];
        agents = new ArrayList<>();
        coups = 0;
    }

    public synchronized void setAgent(int Xprec, int Yprec, int Xnew, int Ynew, int id) {
        grille[Xprec][Yprec] = 0;
        grille[Xnew][Ynew] = id;
    }

    public synchronized int[][] getGrille() {
        return grille;
    }

    public synchronized int getAgentId(int X, int Y) {
        if(X > -1 && X < Main.gridSize && Y > -1 && Y < Main.gridSize)
            return grille[X][Y];
        else
            return 0;
    }

    public synchronized void afficherGrille() {

        for (int i = 0; i < Main.gridSize; ++i) {
            for (int j = 0; j < Main.gridSize; ++j) {
                System.out.print(grille[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    public synchronized boolean move(int id, int oldX, int oldY, int newX, int newY) {
        if(oldX > -1 && oldX < Main.gridSize && oldY > -1 && oldY < Main.gridSize && newX > -1 && newX < Main.gridSize && newY > -1 && newY < Main.gridSize) {
            if (grille[newX][newY] == 0 && grille[oldX][oldY] == id) {
                grille[oldX][oldY] = 0;
                grille[newX][newY] = id;
                incCoups();
                return true;
            }
        }

        return false;
    }

    public synchronized void addAgent(Agent agent) {
        grille[agent.getX()][agent.getY()] = agent.getMyId();
        agents.add(agent);
    }

    public boolean isEnded() {
        boolean result = true;
        for(Agent agent: agents) {
            result = result && agent.isArrived();
        }

        return result;
    }

    public synchronized int getCoups() {
        return coups;
    }

    public synchronized void incCoups() {
        ++coups;
    }
}
