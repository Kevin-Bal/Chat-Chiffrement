import java.math.BigInteger;
import java.util.Random;

public class ClePublique {
    private BigInteger p;
    private BigInteger q;

    private BigInteger n;
    private BigInteger m;

    private BigInteger e;

    public ClePublique() {
        // 2 grands entiers premiers
        p = BigInteger.probablePrime(500, new Random());
        do{
            q = BigInteger.probablePrime(500, new Random());
        }while(p.equals(q));

        // n & m
        n = p.multiply(q);
        m = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));

        // petit entier impair
        do{
            e = BigInteger.valueOf(Math.abs(new Random().nextInt()));
            System.out.println(e);
        }while(BigInteger.valueOf(0).equals(e.divideAndRemainder(BigInteger.valueOf(2))[1]) || !sontPremiersEntreEux(e,m));
    }

    public boolean sontPremiersEntreEux(BigInteger e,BigInteger m){
        return e.gcd(m).equals(1);
    }

    public BigInteger[] clePublique(){
        BigInteger[] cp = new BigInteger[2];
        cp[0] = n;
        cp[1] = e;
        return cp;
    }
}

