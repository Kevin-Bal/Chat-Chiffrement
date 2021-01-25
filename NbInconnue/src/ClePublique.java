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
        }while(!e.gcd(m).equals(BigInteger.valueOf(1)));
    }

    public BigInteger[] clePublique(){
        BigInteger[] cp = new BigInteger[2];
        cp[0] = n;
        cp[1] = e;
        return cp;
    }

    public BigInteger getM() {
        return m;
    }

    public void setM(BigInteger m) {
        this.m = m;
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }
}

