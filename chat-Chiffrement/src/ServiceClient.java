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
    private CryptographieRSA rsa;


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
        rsa = new CryptographieRSA();
        
        try {
			this.mon_entree = new BufferedReader(new InputStreamReader(this.ma_connexion.getInputStream()));
			this.ma_sortie = new DataOutputStream(this.ma_connexion.getOutputStream());
		} catch(IOException e) {
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
            this.ma_sortie.writeUTF(rsa.chiffrement(MOT_DE_PASSE,newCP));

            newClientManager(newCP);
            cles.add(newCP);

            // Boucle principale //
	        while (true) {
	
	            message_lu = this.mon_entree.readLine();
	            if(!message_lu.startsWith("/")) {
                    String idToSendTo = message_lu;
                    message_lu = this.mon_entree.readLine();
                    notifyOneClient(message_lu,idToSendTo);
                    message_lu = this.mon_entree.readLine();
                    notifyOneClient(message_lu,idToSendTo);
	            }
	            
	            switch(message_lu){
	                case HELP :
                        this.ma_sortie.writeUTF(rsa.chiffrement("--> Wiki des commandes :",newCP));
                        this.ma_sortie.writeUTF(rsa.chiffrement("Serveur",newCP));
                        this.ma_sortie.writeUTF(rsa.chiffrement("--> "+QUITTER+" : permet de vous déconnecter du serveur.",newCP));
                        this.ma_sortie.writeUTF(rsa.chiffrement("Serveur",newCP));
	                    break;
	
	                case QUITTER :
	                    notifyAllClient("[serveur > You] " + newCP.getId() + " vient de se déconnecter");
                        notifyAllClient("Serveur");
	                    System.out.println("[Serveur] Deconnexion de " + newCP.getId() + "!\n");
                        this.ma_sortie.writeUTF(rsa.chiffrement("Deconnexion en cours ...",newCP));
                        this.ma_sortie.writeUTF(rsa.chiffrement("Serveur",newCP));
	                    orderClientsToDeletePKey(newCP.getId());
	                    terminer();
                        deletePKey(newCP.getId());
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
        notifyAllClient("Serveur");
        String jsonClePubliqueArrivant = gson.toJson(newCP);

        //Pour sécuriser l'envoi de la clé privée, on créé un mot de passe.
        //A la reception de ce mot de passe, le client sait qu'il va recevoir une nouvelle clé publique
        notifyAllClientWithoutCypher(MOT_DE_PASSE);
        notifyAllClientWithoutCypher(jsonClePubliqueArrivant);


        // Message de bienvenu pour le client courant + Envoi des clés des anciens clients au nouveau
        try {
            this.ma_sortie.writeUTF(rsa.chiffrement("[serveur > You] Bienvenue dans le chat " + newCP.getId(),newCP));
            this.ma_sortie.writeUTF(rsa.chiffrement("Serveur",newCP));
            for (ClePublique cle:cles) {
                String jsonClePubliqueAncien = gson.toJson(cle);
                this.ma_sortie.writeUTF(MOT_DE_PASSE);
                this.ma_sortie.writeUTF(jsonClePubliqueAncien);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //###########################################################################################################################################
    //              DECONNEXION CLIENT
    private void orderClientsToDeletePKey(String id) throws IOException {
        notifyAllClientWithoutCypher(MOT_DE_PASSE+"-delete");
        notifyAllClientWithoutCypher(id);
    }

    private void deletePKey(String id) {
        int pos = -1;
        for (int i=0; i<cles.size();++i) {
            if(cles.get(i).getId() == id){
                pos = i;
            }
        }

        if(pos != -1){
            cles.remove(pos);
            clients.remove(pos);
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

    //###########################################################################################################################################
    //              NOTIFY
    /**
     * Méthode qui se charge de notifier tous les clients présent dans le serveur.
     * POUR LES MESSAGES SERVEUR (Connexion,Deco...)
     * @param msg
     */
    public void notifyAllClient(String msg) {
        try {
            for ( int i=0; i<clients.size();++i ) {
	            if(clients.get(i) != ma_connexion && !clients.get(i).isClosed()) {
	            	DataOutputStream sortieSocket = new DataOutputStream(clients.get(i).getOutputStream());
					sortieSocket.writeUTF(rsa.chiffrement(msg,cles.get(i)));
	            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui se charge de notifier tous les clients présent dans le serveur
     * POUR L'ENVOI DE CLE OU DE COMMANDES AU CLIENT (demander au client de créé une clé publique ou de la supprimer)
     * @param msg
     */
    public void notifyAllClientWithoutCypher(String msg) {
        try {
            for ( int i=0; i<clients.size();++i ) {
                if(clients.get(i) != ma_connexion && !clients.get(i).isClosed()) {
                    DataOutputStream sortieSocket = new DataOutputStream(clients.get(i).getOutputStream());
                    sortieSocket.writeUTF(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui se charge de notifier tous les clients présent dans le serveur
     * POUR ENVOYER UN MESSAGE A UNE PERSONNE (transmettre un message d'un client à un autre)
     * @param msg id
     */
    public void notifyOneClient(String msg, String id) {
        try {
            for ( int i=0; i<clients.size();++i) {
                if(clients.get(i) != ma_connexion && !clients.get(i).isClosed() & cles.get(i).getId().equals(id)) {
                    DataOutputStream sortieSocket = new DataOutputStream(clients.get(i).getOutputStream());
                    sortieSocket.writeUTF(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
