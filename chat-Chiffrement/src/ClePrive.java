import java.math.BigInteger;

public class ClePrive {
    // Rajouter un Id CLient
    private BigInteger u;
    private BigInteger e;
    private BigInteger n;
    private BigInteger m;
    private BigInteger v;

    public ClePrive(BigInteger e, BigInteger m, BigInteger n){
        this.e = e;
        this.m = m;
        this.n = n;
        euclideEtendu(e,m);
    }

    /**
     * Intégration de l'algorithme d'Euclide étendu
     */
    public void euclideEtendu(BigInteger a, BigInteger b){

        BigInteger r1 = a;
        BigInteger r2 = b;

        BigInteger u1 = BigInteger.valueOf(1);
        BigInteger v1 = BigInteger.valueOf(0);

        BigInteger u2 = BigInteger.valueOf(0);
        BigInteger v2 = BigInteger.valueOf(1);

        BigInteger q;
        BigInteger r3;
        BigInteger u3;
        BigInteger v3;

        while(!r2.equals(BigInteger.valueOf(0))){

            q = r1.divide(r2);

            r3 = r1;
            u3 = u1;
            v3 = v1;
            r1 = r2;
            u1 = u2;
            v1 = v2;

            r2 = r3.subtract(q.multiply(r2));
            u2 = u3.subtract(q.multiply(u2));
            v2 = v3.subtract(q.multiply(v2));

        }

        if(BigInteger.valueOf(2).min(u1).equals(u1) || m.max(u1).equals(u1)){
            BigInteger k = BigInteger.valueOf(-1);
            u1 = u1.subtract(k.multiply(m));
            while(BigInteger.valueOf(2).min(u1).equals(u1) || m.max(u1).equals(u1)){
                k = k.subtract(BigInteger.ONE);
                u1 = u1.subtract(k.multiply(m));
            }
            u = u1;
        } else {
            u = u1;
        }

    }

    ///////////GETTER ET SETTER////////////

    public void setU(BigInteger u) {
        this.u = u;
    }

    public BigInteger getU() {
        return u;
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getM() {
        return m;
    }

    public void setM(BigInteger m) {
        this.m = m;
    }

    public BigInteger getV() {
        return v;
    }

    public void setV(BigInteger v) {
        this.v = v;
    }

    public BigInteger getN() {
        return n;
    }
}
