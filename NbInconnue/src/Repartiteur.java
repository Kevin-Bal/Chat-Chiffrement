import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class Repartiteur extends ServerSocket {
    private final static int port = 12000; // Port d'écoute
    private List<Socket> clients = new ArrayList<>();

    public Repartiteur() throws IOException {
        super(port);
        System.out.println("[Serveur]: Serveur Chatroom chiffrée lancée sur " + (port));
    }

    /**
     * Permet de gérer le service client du serveur pour ajancer les connexion des clients
     * @throws IOException
     */
    public void execute() throws IOException {
        Socket maConnection;
        while (true) {
            System.out.println("[Serveur]: En attente de connexion");
            maConnection = accept();
            clients.add(maConnection);

            new Thread(new ServiceClient(maConnection, clients)).start();
        }
    }

    /**
     * Main pour la gestion du serveur
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Repartiteur connectionManager = new Repartiteur();
        connectionManager.execute();
    }

}