import java.io.BufferedReader;
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
    private BufferedReader mon_entree = null;
    private PrintWriter ma_sortie = null;
    private  Socket la_connection = null;
    private static final String FIN_CONNECTION = "Fermeture de la connexion";
    private static final String FIN_CLIENT = "end";

    public ThreadClient( String monId) throws IOException {
        this.id = monId;
        try {
            la_connection = new Socket(serveurIp, serveurPort);
            mon_entree = new BufferedReader(new InputStreamReader(la_connection.getInputStream()));
            ma_sortie = new PrintWriter(la_connection.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }



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

            }
        });
        envoyer.start();
    }

    public void reception() {
        Thread recevoir = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {

                try {
                    msg = mon_entree.readLine();
                    while(!FIN_CONNECTION.equals(msg)){
                        System.out.println(msg);
                        msg = mon_entree.readLine();
                    }
                    System.out.println("Déconnecté du serveur");
                    ma_sortie.close();
                    la_connection.close();
                    System.exit(-1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recevoir.start();
    }

    public void run(){
        envoie();
        reception();
    }

    public static String saisieMessage(){
        Scanner sc = new Scanner(System.in);

        return sc.nextLine();
    }
}

