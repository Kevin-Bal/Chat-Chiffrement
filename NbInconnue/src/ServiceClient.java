/* On  importe les  classes  Reseau, Entrees Sorties, Utilitaires */
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class ServiceClient implements Runnable {
    private  Socket ma_connection;
    private  String nomClient;
    private String nbRandom;
    private int reponse;
    private Collection<String> classement ;
    private ArrayList<Socket> clients = new ArrayList<>();
    private HashMap<Socket,Integer> listeNbEssai;
    private int nbVictoire = 0;

    // Constante projet :
    private static final String HELP = "/help";
    private static final String CLASSEMENT = "/classement";
    private static final String PARTIE = "/partie";
    private static final String QUITTER = "/quit";

    public ServiceClient(int nbRandom, Socket la_connection, ArrayList<Socket> clients, Collection<String> classement, HashMap<Socket,Integer> listNbEssaie){
        ma_connection= la_connection;
        this.clients = clients;
        this.nbRandom = Integer.toString(nbRandom);
        this.reponse = nbRandom ;
        this.classement = classement;
        this.listeNbEssai = listNbEssaie;
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
        // Phase d initialisation
        BufferedReader flux_entrant = null;
        PrintWriter ma_sortie = null;
        int nbEssai = 0;

        try{
            InputStreamReader isr = new InputStreamReader(ma_connection.getInputStream(), "UTF-8");
            flux_entrant = new BufferedReader(isr) ;
            ma_sortie = new PrintWriter(ma_connection.getOutputStream() , true);
        }
        catch (Exception e1) {
            System.out.println("Erreur d initialisation") ;
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
        ma_sortie.println("[serveur]: Bienvenue dans le Jeu du Nombre Inconnue "+nomClient);

        // On notifie tout les client de l'arrivée d'une nouvelle personne
        notifyAllClient("[serveur]: "+nomClient+" vient d'entrer dans la chatroom.");

        // Boucle principale //
        while ( true )
        {
            int propal =  0;

            if(listeNbEssai.get(ma_connection) < 4){
                try {
                    message_lu = flux_entrant.readLine();
                    if(message_lu.matches("^-?\\d+$")){
                        propal = Integer.parseInt(message_lu);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (propal == reponse) {
                    nbVictoire++;

                    ma_sortie.println("Gagné");
                    notifyAllClient(nomClient+" a gagné !!");

                    classement.add(nomClient+" a gagné.e "+nbVictoire);

                    nbEssai = 0;

                    listeNbEssai.forEach((key, value) -> listeNbEssai.replace(key,0));

                    reponse = tirageNombreAlea();
                    System.out.println("[Serveur] nouveau nomre tiré : "+ reponse) ;
                }
                else{
                    if(propal != 0){
                        nbEssai++;
                        listeNbEssai.replace(ma_connection,nbEssai);

                        notifyAllClient(nomClient+" a proposé " + propal);
                        if (propal > reponse) {
                            ma_sortie.println("-> Le nombre inconnu est compris entre " + 1 + " et " + propal + ".");
                            notifyAllClient("-> Le nombre inconnu est compris entre " + 1 + " et " + propal + ".");

                        } else {
                            ma_sortie.println("-> Le nombre inconnu est compris entre " + propal + " et " + 10 + ".");
                            notifyAllClient("-> Le nombre inconnu est compris entre " + propal + " et " + 10 + ".");

                        }
                    }
                }

                switch(message_lu){
                    case HELP :
                        ma_sortie.println("--> Principe :");
                        ma_sortie.println("-> Vous disposez de trois éssaies pour trouver le nombre inconnue.");
                        ma_sortie.println("-> Le nombre inconnu est compris entre 1 et 1 000 000.");
                        ma_sortie.println("--> Toutes les commande sont répertoriées ici :");
                        ma_sortie.println("-> " + CLASSEMENT + " pour voir le classement des parties jouées dur le serveur.");
                        ma_sortie.println("-> " + PARTIE +" pour connaître l'état de la partie en cours.");
                        break;

                    case CLASSEMENT :
                        PrintWriter finalMa_sortie = ma_sortie;
                        classement.stream().forEach(niveau -> finalMa_sortie.println(niveau));
                        break;

                    case QUITTER :
                        notifyAllClient("[serveur] " + nomClient + " vient de se déconnecter");
                        System.out.format ("[%s] :  [%s] recu, Transmission finie\n", nomClient, message_lu);
                        ma_sortie.println("Fermeture de la connexion");
                        terminer();
                         

                    case PARTIE :
                        ma_sortie.println("[Serveur]: Vous avez joué : "+nbEssai+" coups.");

                        break;

                    default :
                        if(!message_lu.matches("^-?\\d+$"))
                            ma_sortie.println("[Serveur]:Saisissez " + HELP + "pour avoir plus d'informations.");
                }
            }


        }


    }

    public void notifyAllClient(String message_lu) {
        try {
            for (Socket client : clients ) {
                if(client != ma_connection) {

                    if(!client.isClosed()){
                        PrintWriter sotie_client = new PrintWriter(client.getOutputStream(), true);
                        sotie_client.println(message_lu);
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

    public static int tirageNombreAlea(){
        return (int)(Math.random() * 10);
    }

}
