package batoh4;

/**
 * Polozka batohu - implementuje Comparable, abychom mohli vytahovat polozky
 * vzdy s nejvetsim pomerem cena/vaha
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class BatohItem implements Comparable<BatohItem> {

    private int hodnota;
    private int vaha;
    private float pomer;

    public BatohItem(int hodnota, int vaha) {
        this.hodnota = hodnota;
        this.vaha = vaha;
        this.pomer = (float) hodnota / (float) vaha;
    }

    public int getHodnota() {
        return hodnota;
    }

    public void setHodnota(int hodnota) {
        this.hodnota = hodnota;
    }

    public int getVaha() {
        return vaha;
    }

    public void setVaha(int vaha) {
        this.vaha = vaha;
    }

    public float getPomer() {
        return pomer;
    }

    public int compareTo(BatohItem o) {
        float pom = o.getPomer();
        if( pom > this.pomer ) return 1;
        if( pom == this.pomer ) return 0;
        return -1;
    }

}
