import java.io.IOException;

public class GestionClient {

    /**
     * Main pour la gestion des clients
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
		if (args.length == 1) {
	        String monId = String.format( args[0]);
	        ThreadClient client = new ThreadClient(monId);
	        Thread t = new Thread(client);
			t.start();
		} else {
			System.out.println("syntaxe d’appel : java GestionClient nom_du_client\n");
		}
    }

}