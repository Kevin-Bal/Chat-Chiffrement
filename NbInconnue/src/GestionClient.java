import java.io.IOException;
import java.math.BigInteger;

public class GestionClient {

    /**
     * Main pour la gestion des clients
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String monId = String.format( args[0]);
        ClePublique cp = new ClePublique();
        System.out.println("Cle publique : "+cp.clePublique()[0]+"    "+cp.clePublique()[1]);

        ClePrive clePrive = new ClePrive(BigInteger.valueOf(7), BigInteger.valueOf(4992), cp.getN());
        System.out.println(clePrive.getU());

/*
        ThreadClient currrentClient = new ThreadClient(monId);
        currrentClient.run();
*/

    }

}