package batoh4;

/**
 * Barak - kde jsou na zacatku veci k ukradnuti
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class Barak extends ItemsContainer {

    /**
     * Prida polozku do baraku
     * @param hodnota
     * @param vaha
     * @return boolean - povedlo se?
     */
    public boolean addItem(int hodnota, int vaha) {
        this.polozky.add(new BatohItem(hodnota, vaha));
        this.orderItems();
        return true;
    }

    /**
     * Prida polozku do baraku
     * @param hodnota
     * @param vaha
     * @return boolean - povedlo se?
     */
    public boolean addItem(BatohItem item) {
        this.polozky.add(item);
        this.orderItems();
        return true;
    }

    /**
     * Vrati pocet polozek
     * @return
     */
    public int getItemsCount() {
        return this.polozky.size();
    }

}
