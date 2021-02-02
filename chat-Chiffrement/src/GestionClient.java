import java.io.IOException;

public class GestionClient {

    /**
     * Main pour la gestion des clients
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
              String adresseIP = args[0];
              int port = Integer.parseInt(args[1]);
              String monId = String.format(args[2]);

              ThreadClient client = new ThreadClient(adresseIP, port, monId);
              Thread t = new Thread(client);
              t.start();
        } else {
          System.out.println("syntaxe d’appel : java GestionClient adresse_serveur port_serveur nom_du_client\n");
        }
    }

}