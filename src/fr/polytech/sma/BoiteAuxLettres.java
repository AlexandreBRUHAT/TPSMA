package fr.polytech.sma;

import java.util.ArrayList;
import java.util.LinkedList;

public class BoiteAuxLettres {

    private static BoiteAuxLettres instance;

    private ArrayList<LinkedList<Message>> boite;

    public static BoiteAuxLettres getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new BoiteAuxLettres(Main.nbAgent);
            return instance;
        }
    }

    private BoiteAuxLettres(int nbAgent) {
        boite = new ArrayList<LinkedList<Message>>();

        for(int i = 0; i < nbAgent; ++i) {
            boite.add(new LinkedList<>());
        }
    }

    public ArrayList<LinkedList<Message>> getBoite() {
        return boite;
    }

    public void envoyerMsg(int id, Message msg) {
        boite.get(id - 1).add(msg);
    }

    public Message lireMsg(int id) {
        return boite.get(id - 1).poll();
    }
}
