import java.io.IOException;

public class GestionClient {

    /**
     * Main pour la gestion des clients
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	ClePublique cp = new ClePublique();
        //ClePrive clePrive = new ClePrive(cp.getE(), cp.getM(), cp.getN());

        System.out.println("Cle publique : "+cp.clePublique()[0]+"    "+cp.clePublique()[1]);
        //System.out.println(clePrive.getU());
        
        System.out.println();
        System.out.println();

        Chiffrement ch = new Chiffrement();
        System.out.println(ch.chiffrementRSA("Bonjour !", cp));
    	
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