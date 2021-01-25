import java.io.IOException;

public class GestionClient {

    /**
     * Main pour la gestion des clients
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String monId = String.format( args[0]);
        ClePublique cp = new ClePublique();
        System.out.println(cp.clePublique()[0]+"    "+cp.clePublique()[1]);

        /*
        ThreadClient currrentClient = new ThreadClient(monId);
        currrentClient.run();
         */

    }

}