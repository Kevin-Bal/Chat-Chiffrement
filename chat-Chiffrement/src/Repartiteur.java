import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;


public class Repartiteur extends ServerSocket {

    private List<Socket> clients = new ArrayList<>();
    private List<ClePublique> cles = new ArrayList<>();
    private String MOT_DE_PASSE = "motDePasse";

    public Repartiteur(int port) throws IOException {
        super(port);
        System.out.println("[Serveur] Serveur Chatroom chiffrée lancée sur " + port);
        MOT_DE_PASSE = generatePassayPassword();
    }


    /**
     * Permet de générer un mot de passe aléatoire, grâce à la librairie Passay
     */
    public String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);
        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);
        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);
        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "ERROR_CODE";
            }

            public String getCharacters() {
                return "ALLOWED_SPL_CHARACTERS";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);
        String password = gen.generatePassword(50, splCharRule, lowerCaseRule, upperCaseRule, digitRule);
        return password;
    }

    /**
     * Permet de gérer le service client du serveur pour ajancer les connexion des clients
     * @throws IOException
     */
    public void execute() throws IOException {
        Socket maConnexion;
        System.out.println("[Serveur] En attente de connexion");

        while (true) {
            maConnexion = accept();
            clients.add(maConnexion);

            new Thread(new ServiceClient(maConnexion, clients, cles, MOT_DE_PASSE)).start();
            System.out.println("[Serveur] En attente de connexion");
        }
    }

    /**
     * Main pour la gestion du serveur
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            int port = 0;
            try {
                port = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                System.out.println("Le port doit être un entier strictement positif\n");
                System.exit(-1);
            }

//            String adresseIPPublic = "";
//            try {
//                adresseIPPublic = findIP("http://www.monip.org/","<BR>IP : ","<br>");
//                System.out.println(adresseIPPublic);
//            } catch (Exception e) {
//                e.printStackTrace();
//            };

            Repartiteur connexionManager = new Repartiteur(port);
            try {
                connexionManager.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("syntaxe d’appel : java Repartiteur port\n");
        }
    }

    public static String findIP(String site, String prefixe, String suffixe) throws Exception {
        Scanner sc = new Scanner(new URL(site).openStream());
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            int a = line.indexOf(prefixe);
            if (a!=-1) {
                int b = line.indexOf(suffixe,a);
                if (b!=-1) {
                    sc.close();
                    return line.substring(a+prefixe.length(),b);
                }
            }
        }

        sc.close();
        return null;
    }
}