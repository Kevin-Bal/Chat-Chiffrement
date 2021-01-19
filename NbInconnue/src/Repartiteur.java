import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Repartiteur extends ServerSocket {
    private final static int port = 12000; /* Port d'écoute */
    private ArrayList<Socket> clients = new ArrayList<>();

    public Repartiteur() throws IOException {
        super(port);
        System.out.println("[Serveur]: Serveur Jouet lancé sur " + (port));
    }

    public void execute() throws IOException {
        Socket maConnection;
        while (true) {
            System.out.println("[Serveur]: En attente de connexion");
            maConnection = accept();
            clients.add(maConnection);

            new Thread(new ServiceClient(maConnection, clients)).start();
        }

    }

    public static void main(String[] args) throws IOException {
        Repartiteur connectionManager = new Repartiteur();
        connectionManager.execute();
    }

}