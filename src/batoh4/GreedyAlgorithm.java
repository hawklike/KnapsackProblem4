package batoh4;

import java.util.Iterator;

/**
 * Greedy algoritmus pro vypocet problemu batohu
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class GreedyAlgorithm implements IAlgorithm {

    private Batoh batoh;
    private Barak barak;
    public int expandovano = 0;

    public GreedyAlgorithm(Barak barak, Batoh batoh) {
        this.barak = barak;
        this.batoh = batoh;
        this.expandovano = 0;
    }

    /**
     * Implementace Greedy Algoritmu pro problem batohu
     */
    public void computeStolenItems() {
        barak.orderItems();
        Iterator iterator = barak.getPolozky().iterator();
        BatohItem item = null;
        /* prochazime vsechny polozky, dokud neni batoh plny */
        while( !batoh.isFull() && iterator.hasNext() ) {
            item = (BatohItem) iterator.next();
            expandovano++;
            // System.out.println("Zkusim ukrast polozku {" + item.getHodnota() + "," + item.getVaha() + "," + item.getPomer() + "}, akt zatizeni je " + batoh.getAktualniZatizeni());
            if ( !batoh.addItem(item) ) {
                // System.out.println("Polozka {" + item.getHodnota() + "," + item.getVaha() + "," + item.getPomer() + "} se uz nevejde do batohu.");
                // break; // pokud je plny, stop
            }
        }
        this.batoh.setExpandovano(expandovano);
    }

}
