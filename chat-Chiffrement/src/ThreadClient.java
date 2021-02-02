import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ThreadClient implements Runnable {
    private int port;
    private String adressseIP;
    private String id;
    private String MOT_DE_PASSE = "";
    
    private DataInputStream mon_entree = null;
    private PrintWriter ma_sortie = null;
    private Socket la_connexion = null;
    private Scanner scanner = new Scanner(System.in);
    //CLES
    private ClePublique maClePublique;
    private ClePrive maClePrive;
    private List<ClePublique> cles = new ArrayList<>();
    private CryptographieRSA rsa;

    private String lastMessageSent;
    private String lastMessageReceived;
    
    private static final String FIN_CONNECTION = "Fermeture de la connexion";
    private static final String FIN_CLIENT = "end";

    public ThreadClient(String adresseIP, int port, String monId) throws IOException {
        this.adressseIP = adresseIP;
        this.port = port;
        this.id = monId;

        //CREATION DES CLES ET CHIFFREMENT
        maClePublique = new ClePublique(this.id);
        maClePrive = new ClePrive(maClePublique.getE(), maClePublique.getM(), maClePublique.getN());
        rsa = new CryptographieRSA();

        try {
            la_connexion = new Socket(this.adressseIP, this.port);
            mon_entree = new DataInputStream(la_connexion.getInputStream());
            ma_sortie = new PrintWriter(la_connexion.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("[CLIENT] [ERREUR] Aucun serveur n’est rattaché au port");
	    	System.exit(-1);
        }
    }

    /**
     * Fonction d'envoie de message géré par un thread
     */
    public void envoie() {
        Thread envoyer = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                //Envoi de la clé publique au serveur
                Gson gson = new Gson();
                String jsonClePublique = gson.toJson(maClePublique);
                ma_sortie.println(jsonClePublique);

                // Boucle principale //
                while(!FIN_CLIENT.equals(msg)){
                    msg = saisieMessage();

                    //Envoi de message aux autres clients
                    if(!msg.startsWith("/")) {
                        lastMessageSent = "[" + maClePublique.getId() + "] " + msg;
                        //CHIFFREMENT DE LA DONNEE et envoi au autres clients (via le serveur)
                        for (ClePublique cle: cles) {
                            ma_sortie.println(cle.getId());
                            ma_sortie.println(rsa.chiffrement("[" + maClePublique.getId() + "] " + msg,cle));
                            ma_sortie.println(rsa.chiffrement(maClePublique.getId(),cle));
                        }
                    }
                    //Envoi de commande au serveur
                    else{
                        ma_sortie.println(msg);
                    }

                    ma_sortie.flush();
                }
                
                fermeture();
            }
        });
        envoyer.start();
    }

    /**
     * Gestion de la réception de message, plus affichage
     */
    public void reception() {
        Thread recevoir = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
            	try {
            	    //receive password
                    msg = mon_entree.readUTF();
                    MOT_DE_PASSE = rsa.dechiffrement(msg, maClePrive);

                    msg = mon_entree.readUTF();
                    while(!FIN_CONNECTION.equals(msg)){
                        //NOUVEAU CLIENT CONNECTE : RECEPTION DE SA CLE
                        if(msg.equals(MOT_DE_PASSE)) {
                            msg = mon_entree.readUTF();

                            //Creation de la clé publique
                            Gson gson = new Gson();
                            ClePublique newCP = gson.fromJson(msg, ClePublique.class);

                            //Ajout de la clé
                            cles.add(newCP);

                            //TEST
                            /*
                            System.out.println("_____________________________________________________________________________________________________");
                            System.out.println("[Serveur] Cle publique reçu de "+ cles.get(cles.size()-1).getId() +" : ");
                            System.out.println("");
                            System.out.println(cles.get(cles.size()-1).getN());
                            System.out.println("");
                            System.out.println(cles.get(cles.size()-1).getE());
                            System.out.println("_____________________________________________________________________________________________________");
                            */

                            msg = mon_entree.readUTF();
                        }
                        //RECEPTION D'UNE DEMANDE DE SUPPRESSION DE CLE
                        else if(msg.equals(MOT_DE_PASSE+"-delete")){
                            msg = mon_entree.readUTF();
                            deletePKey(msg);
                            msg = mon_entree.readUTF();
                        }
                        //RECEPTION D'UN MESSAGE TRADITIONNEL
                        else{
                            //Déchiffrement
                            String messageReceived = rsa.dechiffrement(msg, maClePrive);
                            msg = mon_entree.readUTF();
                            String from = rsa.dechiffrement(msg, maClePrive);


                            //Double Handshake
                            if(from.equals("ok")){
                                System.out.println("######### TEST ######### OK");
                                System.out.println(lastMessageReceived);
                                System.out.println();
                            }
                            else if(from.equals("Serveur")){
                                System.out.println(messageReceived);
                            }
                            else if(messageReceived.startsWith("doubleHandshake-")){
                                if(lastMessageSent.equals(messageReceived.split("doubleHandshake-")[1])){
                                    for (ClePublique cle: cles) {
                                        if(from.equals(cle.getId())){
                                            ma_sortie.println(cle.getId());
                                            ma_sortie.println(rsa.chiffrement(lastMessageSent,cle));
                                            ma_sortie.println(rsa.chiffrement("ok",cle));

                                            System.out.println("######### TEST ######### Ce message n'a pas été modifié par un tiers : "+ from +" peut l'afficher");
                                        }
                                    }
                                }
                                else{
                                    System.out.println("ERROR : double handshake false : "+lastMessageSent+"      "+messageReceived.split("doubleHandshake-")[1]);
                                }
                            }
                            else{
                                for (ClePublique cle: cles) {
                                    if(from.equals(cle.getId())){
                                        ma_sortie.println(cle.getId());
                                        ma_sortie.println(rsa.chiffrement("doubleHandshake-"+messageReceived,cle));
                                        ma_sortie.println(rsa.chiffrement(maClePublique.getId(),cle));
                                        lastMessageReceived = messageReceived;
                                        System.out.println("######### TEST ######### Message reçu de "+ from + " : Demande de vérification en double poignée de main");
                                    }
                                }
                            }


                            msg = mon_entree.readUTF();
                        }
                    }
                } catch(EOFException e) {
                   // on entre dans ce catch lors de la deconnexion : ce n'est pas une erreur
                   System.out.println("Deconnexion réussi");
             	   fermeture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recevoir.start();
    }

    /**
     * Fonction run du thread client
     */
    @Override
    public void run(){
        envoie();
        reception();
    }

    /**
     * Méthode de saisie de message du client
     * @return message
     */
    public String saisieMessage(){
        return scanner.nextLine();
    }
    
    
    /**
     * Méthode qui ferme toute les connexions ouverte
     */
    private void fermeture() {
		try {
			scanner.close();
			mon_entree.close();
			ma_sortie.close();
			la_connexion.close();
	    	System.exit(-1);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * Méthode qui supprime une clé du tableau de clés
     */
    private void deletePKey(String id) {
        int pos = -1;
        for (int i=0; i<cles.size();++i) {
            if(cles.get(i).getId().equals(id)){
                pos = i;
            }
        }
        if(pos != -1)
            cles.remove(pos);
    }
}

