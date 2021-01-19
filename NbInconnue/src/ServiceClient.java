/* On  importe les  classes  Reseau, Entrees Sorties, Utilitaires */
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ServiceClient implements Runnable {
    private Socket ma_connection;
    private String nomClient;
    private List<Socket> clients;
    private int nbVictoire = 0;

    // Constante projet :
    private static final String HELP = "/help";
    private static final String QUITTER = "/quit";

    /**
     * Constructeur de la classe
     * @param la_connection
     * @param clients
     */
    public ServiceClient(Socket la_connection, ArrayList<Socket> clients){
        this.ma_connection= la_connection;
        this.clients = clients;
    }

    private void terminer(){
        try{
            if (ma_connection != null) {
                System.out.format("Deconnexion pour %s\n", nomClient);
                ma_connection.close();
            }
        }
        catch (IOException e) {
            System.out.format("Déconnexion pour %s\n", nomClient);
            e.printStackTrace();
        }
        return;
    }

    public  void run(){
        // Phase d'initialisation
        BufferedReader flux_entrant = null;
        PrintWriter maSortie = null;
        int nbEssai = 0;

        try{
            InputStreamReader isr = new InputStreamReader(ma_connection.getInputStream(), "UTF-8");
            flux_entrant = new BufferedReader(isr) ;
            maSortie = new PrintWriter(ma_connection.getOutputStream() , true);
        }
        catch (Exception e1) {
            System.out.println("Erreur d'initialisation") ;
            e1.printStackTrace();
        }


        String  message_lu = new String();

        // Initialisation du nom du client
        try {
            nomClient = flux_entrant.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[Serveur]: Connexion de : "+ nomClient );

        // Message de bienvenu :
        maSortie.println("[serveur]: Bienvenue dans le chat "+nomClient);

        // On notifie tout les client de l'arrivée d'une nouvelle personne
        notifyAllClient("[serveur]: "+nomClient+" vient d'entrer dans la chatroom.");

        // Boucle principale //
        while ( true )
        {
            int propal =  0;

            try {
                message_lu = flux_entrant.readLine();
                notifyAllClient(message_lu);

            } catch (IOException e) {
                e.printStackTrace();
            }


            switch(message_lu){
                case HELP :
                    maSortie.println("--> Principe :");
                    maSortie.println("-> Vous disposez de trois éssaies pour trouver le nombre inconnue.");
                    maSortie.println("-> Le nombre inconnu est compris entre 1 et 1 000 000.");
                    maSortie.println("--> Toutes les commandes sont répertoriées ici :");
                    break;

                case QUITTER :
                    notifyAllClient("[serveur] " + nomClient + " vient de se déconnecter");
                    System.out.format ("[%s] :  [%s] reçu, Transmission finie !\n", nomClient, message_lu);
                    maSortie.println("Fermeture de la connexion");
                    terminer();

                default :
                    break;
            }
        }
    }

    public void notifyAllClient(String message_lu) {
        try {
            for (Socket client : clients ) {
                if(client != ma_connection) {

                    if(!client.isClosed()){
                        PrintWriter sotieClient = new PrintWriter(client.getOutputStream(), true);
                        sotieClient.println("["+nomClient+"] : "+message_lu);
                    }
                    else{
                        clients.remove(client);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
