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

            //TEST CHIFFREMENT/DECHIFFREMENT
            /*
            ClePublique cp = new ClePublique(monId);
            ClePrive clePrive = new ClePrive(cp.getE(), cp.getM(), cp.getN());

            System.out.println("Cle publique : "+cp.clePublique()[0]+"    "+cp.clePublique()[1]+"\n\n");
            System.out.println("Cle privé : u : "+clePrive.getU()+"\n n : "+clePrive.getE());

            System.out.println();
            System.out.println();

            CryptographieRSA rsa = new CryptographieRSA();
            System.out.println("Chiifrement / Dechiffrement RSA : " + rsa.dechiffrement(rsa.chiffrement("Bonjour Kevinoulle et Etienouille !!?", cp), clePrive));

            */

            ThreadClient client = new ThreadClient(monId);
            Thread t = new Thread(client);
            t.start();
        } else {
          System.out.println("syntaxe d’appel : java GestionClient nom_du_client\n");
        }
    }

}