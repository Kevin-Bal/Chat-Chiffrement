import java.io.IOException;

public class GestionClient {

    public static void main(String[] args) throws IOException {
        String monId = String.format( args[0]);
        ThreadClient currrentClient = new ThreadClient(monId);
        currrentClient.run();
    }

}