import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class CryptographieRSA {

	/**
	 * Chiffrement RSA d'un message en fonction d'un clé publique 
	 * @param msg
	 * @param cle
	 * @return
	 */
	public String chiffrement(String msg, ClePublique cle) {
		List<String> tuple = new ArrayList<String>() ;

		for(char c : msg.toCharArray()) {			
			BigInteger dec = BigInteger.valueOf(c); // Conversion du caractère en BigInteger correspondant à sa valeur décimal (selon le code ASCCI)

			BigInteger res = dec.modPow(cle.getE(), cle.getN()); // Conversion de "dec" avec la clé publique
			
			tuple.add(res.toString());
		}
		
		return String.join(" ", tuple); // Transformation du tuple (sous forme de tableau) en une chaîne de caractère séparée par des espaces.
	}

	/**
	 * Dechiffrement RSA d'un message en fonction d'une clé privée
	 * @param msg
	 * @param cle
	 * @return
	 */
	public String dechiffrement(String msg, ClePrive cle) {
		String msgDechiffre = "";

		String[] tabMsg = msg.split(" "); // Conversion du msg en tableau de string
		for(String str : tabMsg) {
			BigInteger dec = new BigInteger(str); // Conversion de chaque string en BigInteger

			BigInteger res = dec.modPow(cle.getU(), cle.getN()); // Conversion de "dec" grâce à la clé publique

			msgDechiffre += (char)res.intValue(); // Concaténation des caractères déchiffrés pour former le msg final
		}

		return msgDechiffre;
	}

}
