import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Repartiteur extends ServerSocket {

    private List<Socket> clients = new ArrayList<>();

    public Repartiteur(int port) throws IOException {
        super(port);
        System.out.println("[Serveur] Serveur Chatroom chiffrée lancée sur " + port);
    }

    /**
     * Permet de gérer le service client du serveur pour ajancer les connexion des clients
     * @throws IOException
     */
    public void execute() throws IOException {
        Socket maConnexion;
        System.out.println("[Serveur] En attente de connexion");

        while (true) {
            maConnexion = accept();
            clients.add(maConnexion);

            new Thread(new ServiceClient(maConnexion, clients)).start();
            System.out.println("[Serveur] En attente de connexion");
        }
    }

    /**
     * Main pour la gestion du serveur
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            int port = 0;
            try {
                port = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                System.out.println("Le port doit être un entier strictement positif\n");
                System.exit(-1);
            }

            Repartiteur connexionManager = new Repartiteur(port);
            try {
                connexionManager.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("syntaxe d’appel : java Repartiteur port\n");
        }
    }

}