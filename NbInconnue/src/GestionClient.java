import java.io.IOException;

public class GestionClient {

    public static void main(String[] args) throws IOException {
        String mon_id = String.format( args[0]);
        ThreadClient currrent_client = new ThreadClient(mon_id);
        currrent_client.run();
    }

}