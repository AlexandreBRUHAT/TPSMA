package fr.polytech.sma;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static int nbAgent = 8;
    public static int gridSize = 5;

    public static void main(String[] args){

        List<Agent> agents = new ArrayList<>();

        for(int i = 1; i <= nbAgent; ++i) {

            agents.add(new Agent(i, i % gridSize, i / gridSize, (gridSize -1) - (i % gridSize), (gridSize -1) - (i / gridSize)));
        }

        for(Agent agent: agents) {
            agent.start();
        };

    }
}
