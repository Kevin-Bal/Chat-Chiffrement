import java.math.BigInteger;

public class ClePrive {
    // Rajouter un Id CLient
    private BigInteger u;
    private BigInteger e;
    private BigInteger m;
    private BigInteger v;

    public ClePrive(BigInteger e, BigInteger m){
        this.e = e;
        this.m = m;
        euclideEtendu(e,m);
    }

    /**
     * Intégration de l'algorithme d'Euclide étendu
     */
    public void euclideEtendu(BigInteger a, BigInteger b){
        BigInteger rprec = a;
        BigInteger uprec = BigInteger.valueOf(1);
        BigInteger vprec = BigInteger.valueOf(0);

        BigInteger r = b;
        u = BigInteger.valueOf(0);
        v = BigInteger.valueOf(1);

        int i = 2;

        while(true){
            r = rprec.subtract(rprec.divide(r)).multiply(r);
            u = uprec.subtract(rprec.divide(r)).multiply(u);
            v = vprec.subtract(rprec.divide(r)).multiply(v);

            if(rprec.equals(a.gcd(b)) && r.equals(BigInteger.valueOf(0))){
                break;
            }
            rprec = r;
            uprec = u;
            vprec = v;

            i++;
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
}
