import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadClient implements Runnable {
    private static final int PORT = 12000;
    private static final String IP_SERVER = "127.0.0.1";
    
    private String id;
    
    private DataInputStream mon_entree = null;
    private PrintWriter ma_sortie = null;
    private Socket la_connexion = null;
    private Scanner scanner = new Scanner(System.in);

    
    private static final String FIN_CONNECTION = "Fermeture de la connexion";
    private static final String FIN_CLIENT = "end";

    public ThreadClient(String monId) throws IOException {
        this.id = monId;
        
        try {
            la_connexion = new Socket(IP_SERVER, PORT);
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
                } catch(EOFException e) {
                   // on entre dans ce catch lors de la dedonnexion : ce n'est pas une erreur
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
}

