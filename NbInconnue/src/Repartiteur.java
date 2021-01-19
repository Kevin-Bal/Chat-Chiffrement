import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Repartiteur extends ServerSocket {
    private final static int port = 12000; /* Port d'écoute */
    private final int nbRandom;
    private ArrayList<Socket> clients = new ArrayList<>();
    private Collection<String> classement = new ArrayList<>();
    private HashMap<Socket,Integer> listNbEssaie =  new HashMap<Socket, Integer>();

    public Repartiteur() throws IOException {
        super(port);
        System.out.println("[Serveur]: Serveur Jouet lancé sur " + (port));
        this.nbRandom = tirageNombreAlea();
        System.out.println("[Serveur]: le nombre tiré est "+ nbRandom);
    }

    public void execute() throws IOException {
        Socket maConnection;
        while (true) {
            System.out.println("[Serveur]: En attente de connexion");
            maConnection = accept();
            clients.add(maConnection);

            listNbEssaie.put(maConnection,0);

            new Thread(new ServiceClient(nbRandom, maConnection, clients, classement, listNbEssaie)).start();
        }

    }
    public static void main(String[] args) throws IOException {
        Repartiteur connectionManager = new Repartiteur();
        connectionManager.execute();
    }

    public static int tirageNombreAlea(){
        return (int)(Math.random() * 10);
    }

}