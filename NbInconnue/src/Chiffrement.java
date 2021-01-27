import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Chiffrement {

	
	/**
	 * Chiffrement RSA d'un message en fonction d'un clé publique 
	 * @param msg
	 * @param cp
	 * @return
	 */
	public String chiffrementRSA(String msg, ClePublique cp) {
		List<String> tuple = new ArrayList<String>() ;

		for(char c : msg.toCharArray()) {			
			BigInteger dec = BigInteger.valueOf(c); // Conversion du caractère en BigInteger correspondant à sa valeur décimal (selon le code ASCCI)

			BigInteger res = dec.modPow(cp.getE(), cp.getN()); // Conversion de "dec" avec la clé publique
			
			tuple.add(res.toString());
		}
		
		return String.join(" ", tuple);
	}
	
}
