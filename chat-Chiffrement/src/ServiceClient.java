/* On  importe les  classes  Reseau, Entrees Sorties, Utilitaires */
import com.google.gson.Gson;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceClient implements Runnable {
    // Constante projet :
    private static final String HELP = "/help";
    private static final String QUITTER = "/quit";
    private String MOT_DE_PASSE;


    private Socket ma_connexion;
	private BufferedReader mon_entree;
	private DataOutputStream ma_sortie;
    private List<Socket> clients;
    private List<ClePublique> cles;


    /**
     * Constructeur de la classe
     * @param la_connection
     * @param clients
     * @param cles
     * @param MOT_DE_PASSE
     */
    public ServiceClient(Socket la_connection, List<Socket> clients, List<ClePublique> cles, String MOT_DE_PASSE){
        this.ma_connexion= la_connection;
        this.clients = clients;
        this.cles = cles;
        this.MOT_DE_PASSE = MOT_DE_PASSE;
        
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
	        // Initialisation du nouveau client
            Gson gson = new Gson();
            ClePublique newCP = gson.fromJson(this.mon_entree.readLine(), ClePublique.class);
	        System.out.println("[Serveur] Connexion de : " + newCP.getId());
            System.out.println("_____________________________________________________________________________________________________");
            System.out.println("[Serveur] Cle publique reçu de "+ newCP.getId() +" : ");
            System.out.println("");
            System.out.println(newCP.getN());
            System.out.println("");
            System.out.println(newCP.getE());
            System.out.println("_____________________________________________________________________________________________________");

            //Send Password to new Client
            this.ma_sortie.writeUTF(MOT_DE_PASSE);

            newClientManager(newCP);
            cles.add(newCP);

            // Boucle principale //
	        while (true) {
	
	            message_lu = this.mon_entree.readLine();
	            if(!message_lu.startsWith("/")) {
	            	notifyAllClient("[" + newCP.getId() + "] " + message_lu);
	            }
	            
	            switch(message_lu){
	                case HELP :
	                	this.ma_sortie.writeUTF("--> Wiki des commandes :");
	                	this.ma_sortie.writeUTF("--> "+QUITTER+" : permet de vous déconnecter du serveur.");
	                    break;
	
	                case QUITTER :
	                    notifyAllClient("[serveur > You] " + newCP.getId() + " vient de se déconnecter");
	                    System.out.println("[Serveur] Deconnexion de " + newCP.getId() + "!\n");
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
     * Méthode qui se charge d'ajouter un nouveau client à sa liste et d'envoyer sa clé publique aux anciens clients
     * @param newCP
     */
    private void newClientManager(ClePublique newCP) {
        Gson gson = new Gson();

        // On notifie tout les clients de l'arrivée d'une nouvelle personne + Envoi de la clé publique du nouvel arrivant aux autres clients
        notifyAllClient("[serveur > You] " + newCP.getId() + " vient d'entrer dans la chatroom.");
        String jsonClePubliqueArrivant = gson.toJson(newCP);

        //Pour sécuriser l'envoi de la clé privée, on créé un mot de passe.
        //A la reception de ce mot de passe, le client sait qu'il va recevoir une nouvelle clé publique
        notifyAllClient(MOT_DE_PASSE);
        notifyAllClient(jsonClePubliqueArrivant);


        // Message de bienvenu pour le client courant + Envoi des clés des anciens clients au nouveau
        try {
            this.ma_sortie.writeUTF("[serveur > You] Bienvenue dans le chat " + newCP.getId());
            for (ClePublique cle:cles) {
                String jsonClePubliqueAncien = gson.toJson(cle);
                this.ma_sortie.writeUTF(MOT_DE_PASSE);
                this.ma_sortie.writeUTF(jsonClePubliqueAncien);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Méthode qui se charge de notifier tous les clients présent dans le serveur
     * @param msg
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
