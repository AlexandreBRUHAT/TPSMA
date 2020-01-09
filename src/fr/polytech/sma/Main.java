package fr.polytech.sma;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static int nbAgent = 13;
    public static int gridSize = 5;

    public static void main(String[] args){

        List<Agent> agents = new ArrayList<>();

        Random rand = new Random();

        int[][] start = new int[gridSize][gridSize];
        int[][] end = new int[gridSize][gridSize];

        for(int i = 1; i <= nbAgent; ++i) {
            int xs, ys, xe, ye;

            do {
                xs = rand.nextInt(gridSize);
                ys = rand.nextInt(gridSize);
            } while (start[xs][ys] != 0);

            do {
                xe = rand.nextInt(gridSize);
                ye = rand.nextInt(gridSize);
            } while (end[xe][ye] != 0);

            agents.add(new Agent(i, xs, ys, xe, ye));

            start[xs][ys] = i;
            end[xe][ye] = i;
        }

        for(Agent agent: agents) {
            agent.start();
        };
    }
}
