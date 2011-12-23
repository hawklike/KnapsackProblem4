package batoh4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Batoh - potomek ItemsContainer, akorat navic ma nosnost a aktualni zatizeni
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class Batoh extends ItemsContainer implements Cloneable {

    /**
     * Batoh ma navic nosnost a aktualni zatizeni
     */
    private int nosnost;
    private int aktualniZatizeni;
    private int aktualniCena;
    private boolean DEBUG = false;
    private int expandovano;

    /**
     * Konstruktor, nastavuje nosnost
     * @param nosnost
     */
    public Batoh(int nosnost) {
        super();
        this.nosnost = nosnost;
        this.aktualniCena = 0;
        this.aktualniZatizeni = 0;
        this.expandovano = 0;
    }

    /**
     * Nastavi pole polozek a prepocita zatizeni a cenu
     * @param polozky
     */
    @Override
    public void setPolozky(List<BatohItem> polozky) {
        this.polozky = polozky;
        BatohItem item;
        Iterator it = this.polozky.iterator();
        while ( it.hasNext() ) {
            item = (BatohItem) it.next();
            this.aktualniCena += item.getHodnota();
            this.aktualniZatizeni += item.getVaha();
        }
    }

    /**
     * Nastavi pole polozek a prepocita zatizeni a cenu
     * @param polozky
     */
    @Override
    public void setPolozky() {
        this.polozky = new ArrayList<BatohItem>();
        this.aktualniCena = 0;
        this.aktualniZatizeni = 0;
    }

    /**
     * Prida polozku do batohu - wrapper pro predani pouze parametru
     * @param hodnota
     * @param vaha
     * @return boolean - povedlo se?
     */
    public boolean addItem(int hodnota, int vaha) {
        return addItemExec(new BatohItem(hodnota,vaha));
    }

    /**
     * Prida polozku do batohu - wrapper pro pretizeni funkce addItem
     * @param item
     * @return boolean - povedlo se?
     */
    public boolean addItem(BatohItem item) {
        return addItemExec(item);
    }

    /**
     * Prida polozku do batohu
     * @param item
     * @return boolean - povedlo se?
     */
    private boolean addItemExec(BatohItem item) {
        /* pokud je batoh plny, tak nejde */
        if ( this.isFull() ) {
            if ( DEBUG ) System.out.println("Batoh je jiz plny. Polozka (" + item.getVaha() + ") se jiz nevejde.");
            return false;
        }
        /* pokud je polozka moc velka, tak nejde */
        if ( this.zbyvaKapacita() < item.getVaha() ) {
            if ( DEBUG ) System.out.println("Polozka je moc velka na pridani (" + item.getVaha() + "), kapacita je " + this.zbyvaKapacita());
            return false;
        }
        this.polozky.add(item);
        this.aktualniZatizeni += item.getVaha();
        this.aktualniCena += item.getHodnota();
        return true;
    }

    /**
     * Odstrani polozku
     * @param item
     * @return
     */
    public boolean removeItem(BatohItem item) {
        if (this.polozky.remove(item)) {
            this.aktualniZatizeni -= item.getVaha();
            this.aktualniCena -= item.getHodnota();
            return true;
        }
        return false;
    }

    /**
     * Vrati jestli je batoh plny, nebo ne
     * @return
     */
    public boolean isFull() {
        return ( this.nosnost <= this.aktualniZatizeni );
    }

    /**
     * Vrati kolik jeste zbyva kapacita batohu
     * @return
     */
    public int zbyvaKapacita() {
        return this.nosnost - this.aktualniZatizeni;
    }

    /**
     * Vrati pole polozek - u batohu staci polozky seradit az pri vyberu
     * @return
     */
    @Override
    public List<BatohItem> getPolozky() {
        // this.orderItems();
        return polozky;
    }

    /**
     * Vycistime batoh
     */
    @Override
    public void clear() {
        this.polozky.clear();
        this.aktualniCena = 0;
        this.aktualniZatizeni = 0;
    }

    @Override
    public Batoh clone() {
        Batoh kopie = new Batoh(this.nosnost);
        kopie.setPolozky(this.polozky);
        kopie.setAktualniCena(this.aktualniCena);
        kopie.setAktualniZatizeni(this.aktualniZatizeni);
        if ( kopie.getPolozky().size() != this.getPolozky().size() ) {
            System.err.println("Clone: Pocet polozek se neshoduje!!!");
        }
        if ( kopie.getAktualniCena() != this.getAktualniCena() ) {
            System.err.println("Clone: Aktualni ceny se neshoduji!!!");
        }
        if ( kopie.getAktualniZatizeni() != this.getAktualniZatizeni() ) {
            System.err.println("Clone: Aktualni zatizeni se neshoduje!!!");
        }
        return kopie;
    }

    public int getAktualniZatizeni() {
        return aktualniZatizeni;
    }

    public void setAktualniZatizeni(int aktualniZatizeni) {
        this.aktualniZatizeni = aktualniZatizeni;
    }

    public int getAktualniCena() {
        return this.aktualniCena;
    }

    public void setNosnost(int nosnost) {
        this.nosnost = nosnost;
    }

    public int getNosnost() {
        return this.nosnost;
    }

    public void setAktualniCena(int aktualniCena) {
        this.aktualniCena = aktualniCena;
    }

    public void setExpandovano(int expandovano) {
        this.expandovano = expandovano;
    }

    public int getExpandovano() {
        return this.expandovano;
    }

}
