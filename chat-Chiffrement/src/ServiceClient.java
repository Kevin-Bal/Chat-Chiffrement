/* On  importe les  classes  Reseau, Entrees Sorties, Utilitaires */
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ServiceClient implements Runnable {
	
    private Socket ma_connexion;
	private BufferedReader mon_entree;
	private DataOutputStream ma_sortie;
    private String nomClient;
    private List<Socket> clients;

    // Constante projet :
    private static final String HELP = "/help";
    private static final String QUITTER = "/quit";

    /**
     * Constructeur de la classe
     * @param la_connection
     * @param clients
     */
    public ServiceClient(Socket la_connection, List<Socket> clients){
        this.ma_connexion= la_connection;
        this.clients = clients;
        
        try {
			this.mon_entree = new BufferedReader(new InputStreamReader(this.ma_connexion.getInputStream()));
			this.ma_sortie = new DataOutputStream(this.ma_connexion.getOutputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * Méthode de gestion de déconnexion du client
     */
    private void terminer(){
        try{
            if (ma_connexion != null) {
                ma_connexion.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode de gestion de la réception et de l'envoie des messages
     */
    public void run(){
        String message_lu = new String();

        try {
	        // Initialisation du nom du client
	        this.nomClient = this.mon_entree.readLine();
	        System.out.println("[Serveur] Connexion de : " + nomClient );
	
	        // Message de bienvenu pour le client courant
	        this.ma_sortie.writeUTF("[serveur > You] Bienvenue dans le chat " + nomClient);
	        // On notifie tout les clients de l'arrivée d'une nouvelle personne
	        notifyAllClient("[serveur > You] " + nomClient + " vient d'entrer dans la chatroom.");
	
	        // Boucle principale //
	        while (true) {
	
	            message_lu = this.mon_entree.readLine();
	            if(!message_lu.startsWith("/")) {
	            	notifyAllClient("[" + this.nomClient + "] " + message_lu);
	            }
	            
	            switch(message_lu){
	                case HELP :
	                	this.ma_sortie.writeUTF("--> Wiki des commandes :");
	                	this.ma_sortie.writeUTF("--> "+QUITTER+" : permet de vous déconnecter du serveur.");
	                    break;
	
	                case QUITTER :
	                    notifyAllClient("[serveur > You] " + nomClient + " vient de se déconnecter");
	                    System.out.println("[Serveur] Deconnexion de " + this.nomClient + "!\n");
	                    this.ma_sortie.writeUTF("Deconnexion en cours ...");
	                    terminer();
	                    break;
	                default :
	                    break;
	            }
	        }
	        
        } catch (IOException e) {
			System.out.println("problème\n"+e);
			terminer();
		}
    }

    /**
     * Méthode qui se charge de notifier tous les clients présent dans le serveur
     * @param message_lu
     */
    public void notifyAllClient(String msg) {
        try {
            for (Socket client : clients ) {
	            if(client != ma_connexion && !client.isClosed()) {
	            	DataOutputStream sortieSocket = new DataOutputStream(client.getOutputStream());
					sortieSocket.writeUTF(msg);
	            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
