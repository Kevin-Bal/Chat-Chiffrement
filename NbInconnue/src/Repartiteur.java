import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Repartiteur extends ServerSocket {
    private final static int PORT = 12000; // Port d'écoute

    private List<Socket> clients = new ArrayList<>();

    public Repartiteur() throws IOException {
    	super(PORT);
        System.out.println("[Serveur] Serveur Chatroom chiffrée lancée sur " + PORT);
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
        Repartiteur connexionManager = new Repartiteur();
        try {
        	connexionManager.execute();
	    } catch (IOException e) {
			e.printStackTrace();
		}
    }

}