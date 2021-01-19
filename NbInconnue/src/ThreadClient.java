import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadClient {
    private int serveurPort = 12000;
    private String serveurIp = "127.0.0.1";
    private final String FINISH = "" + (char) 4;
    private String id;
    
    private DataInputStream mon_entree = null;
    private PrintWriter ma_sortie = null;
    private Socket la_connection = null;
    private Scanner scanner = new Scanner(System.in);

    
    private static final String FIN_CONNECTION = "Fermeture de la connexion";
    private static final String FIN_CLIENT = "end";

    public ThreadClient(String monId) throws IOException {
        this.id = monId;
        
        try {
            la_connection = new Socket(serveurIp, serveurPort);
            mon_entree = new DataInputStream(la_connection.getInputStream());
            ma_sortie = new PrintWriter(la_connection.getOutputStream(), true);
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
                ma_sortie.println(id);
                while(!FIN_CLIENT.equals(msg)){
                    msg = saisieMessage();
                    ma_sortie.println(msg);
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
                    msg = mon_entree.readUTF();
                    while(!FIN_CONNECTION.equals(msg)){
                        System.out.println(msg);
                        msg = mon_entree.readUTF();
                    }
                    System.out.println("Déconnecté du serveur");
                    
                    fermeture();
                } catch(EOFException e) {
             	   System.out.println("Le serveur est fermé !");
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
    
    private void fermeture() {
		try {
			scanner.close();
			mon_entree.close();
			ma_sortie.close();
			la_connection.close();
	    	System.exit(-1);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

