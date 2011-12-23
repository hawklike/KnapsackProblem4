package batoh4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Spolecny predek pro batoh a barak
 * Obe entity totiz umoznuji skladovat polozky, pridavat, ubirat
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class ItemsContainer {

    /**
     * Polozky kontejneru
     */
    public List<BatohItem> polozky;

    /**
     * Konstruktor
     */
    public ItemsContainer() {
        this.polozky = new ArrayList<BatohItem>();
    }

    /**
     * Vrati pole polozek
     * @return
     */
    public List<BatohItem> getPolozky() {
        return polozky;
    }

    /**
     * Nastavi pole polozek
     * @param polozky
     */
    public void setPolozky(List<BatohItem> polozky) {
        this.polozky = polozky;
    }

    public void setPolozky() {
        this.polozky = new ArrayList<BatohItem>();
    }

    /**
     * Seradi polozky dle pomeru cena/vaha
     */
    public void orderItems() {
        Collections.sort(this.polozky);
    }

    /**
     * Vymaze vsechny veci
     */
    public void clear() {
        this.polozky.clear();
    }

    /**
     * Vypise polozky batohu/baraku
     * @param polozky
     */
    public void writeItems() {
        Iterator iterator = this.polozky.iterator();
        BatohItem item = null;
        while ( iterator.hasNext() ) {
            item = (BatohItem) iterator.next();
            System.out.print("{" + item.getHodnota() + "," + item.getVaha() + "," + item.getPomer() + "},");
        }
        System.out.println("");
    }

}
