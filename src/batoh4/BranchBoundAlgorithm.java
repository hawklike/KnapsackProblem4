package batoh4;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementace metodou vetvi a hranic
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class BranchBoundAlgorithm implements IAlgorithm {

    private Batoh batoh;
    private Barak barak;
    private int bestCena = 0;
    private int bestCenaVaha = 0;
    private List<BatohItem> bestPolozky = null;
    private List<int[]> stavyBatohu = null;
    private int expandovano = 0;

    public BranchBoundAlgorithm(Barak barak, Batoh batoh) {
        this.barak = barak;
        this.batoh = batoh;
        stavyBatohu = new ArrayList<int[]>();
        this.expandovano = 0;
    }

    /**
     * Spustime algoritmus prochazeni stavoveho prostoru
     */
    public void computeStolenItems() {
        /* zjistime si kolik je celkem polozek v baraku */
        int celkemPolozek = this.barak.getItemsCount();
        /* kazda polozka v batohu je, nebo neni */
        int celkemMoznychStavu = (int) Math.pow(2, celkemPolozek);
        // System.out.println("Celkem moznych polozek je " + celkemPolozek + ", celkem moznych stavu je " + celkemMoznychStavu);

        /* init */
        List<BatohItem> polozky = this.barak.getPolozky();
        int aktualniCena = 0;
        int[] poleBitu = null;

        /* vlozime na zasobnik pocatecni stav */
        stavyBatohu.add(new int[celkemPolozek]);

        /* musime projit vsechny stavy stavoveho prostoru */
        while( !stavyBatohu.isEmpty() ) {
            /* zjistime si pole bitu - hodne zalezi na tom, jestli jdeme do sirky, nebo do hloubky */
            // poleBitu = stavyBatohu.remove(stavyBatohu.size() - 1);
            poleBitu = stavyBatohu.remove(0);
            expandovano++;
            // System.out.print("Taham ze zasobniku polozku: "); this.printState(poleBitu);
            /* zjistime si jestli ma cenu ho prochazet */
            if ( !this.isStateSuitable(poleBitu)) {
                // System.out.println("Tato polozka nam nezaruci lepsi cenu, takze pokracujeme.");
                continue;
            }
            /* vysypeme batoh */
            batoh.clear();
            /* naplnime batoh */
            fillBatoh(polozky, poleBitu);
            /* mrkneme kolik se podarilo ukradnout a pokud je to nejlepsi vysledek, tak ulozime */
            aktualniCena = this.batoh.getAktualniCena();
            if (aktualniCena > this.bestCena ) {
                // System.out.println("Nasli jsme lepsi reseni s cenou " + aktualniCena + " a vahou " + this.batoh.getAktualniZatizeni());
                this.bestCena = aktualniCena;
                this.bestCenaVaha = this.batoh.getAktualniZatizeni();
                this.bestPolozky = this.batoh.getPolozky();
            }
            /* musime expandovat stavy */
            stavyBatohu.addAll(0, expandStates(poleBitu));
        }

        /* konec algoritmu, takze musime do batohu dat nejlepsi vysledek */
        this.batoh.setPolozky(this.bestPolozky);
        this.batoh.setAktualniCena(this.bestCena);
        this.batoh.setAktualniZatizeni(this.bestCenaVaha);
        this.batoh.setExpandovano(this.expandovano);
    }

    /**
     * Zjisti jestli ma cenu tento stav expandovat
     * @param poleBitu
     * @return
     */
    private boolean isStateSuitable(int[] poleBitu) {
        /* zkusime doplnit jednicky z prava, takze nejdeme pozici posledni jednicky */
        int last = getLastOne(poleBitu);
        // System.out.print("Testujeme stav jestli je vhodny: "); printState(poleBitu);
        int cenaStavu = getStatePrice(poleBitu, last + 1);
        // System.out.println("Maximalni mozna cena stavu je " + cenaStavu);
        return (cenaStavu > this.bestCena);
    }

    /**
     * Provede expanzi stavu dle polohy posledni jednicky
     * - pro stav 000 se provede expanze takto:
     * - 100, 010, 001
     *
     * @param int[] poleBitu
     * @return List<int[]> poleStavu
     */
    public List<int[]> expandStates(int[] poleBitu) {
        /* init */
        List<int[]> returnPole = new ArrayList<int[]>();
        /* zjistime si, kde je posledni jednicka v poli */
        int first = getLastOne(poleBitu);
        // System.out.println("Nasli jsme posledni jednicku na indexu " + first);
        /* od jednicky doprava pridavame jednicky */
        for (int i = first + 1; i < poleBitu.length; i++) {
            int[] novy = poleBitu.clone();
            novy[i] = 1;
            // System.out.print("Vytvarim novy stav a davam na zasobnik: "); printState(novy);
            returnPole.add(novy);
        }
        return returnPole;
    }

    /**
     * Zjistime si, kde je prvni jednicka v poli
     * @param poleBitu
     * @return
     */
    private int getLastOne(int[] poleBitu) {
        for (int i = poleBitu.length - 1; i > -1 ; i--) {
            if ( poleBitu[i] == 1 ) return i;
        }
        return -1;
    }

    /**
     * Projde poleBitu, ktere reprezentuje polozky batohu a naplni ho
     * @param batoh
     * @param poleBitu
     */
    private void fillBatoh(List<BatohItem> polozky, int[] poleBitu) {
        /* init */
        BatohItem item = null;
        /* projdeme vsechny polozky */
        // System.out.print("Zkusime naplnit batoh pomoci pole bitu: ");
        // printState(poleBitu);
        for (int j = 0; j < poleBitu.length; j++) {
            /* pokud je ve vektoru jednicka, pridame polozku */
            if ( poleBitu[j] == 1 ) {
                // System.out.println("Zkusime pridat polozku " + j + " coz je v/c " + polozky.get(j).getHodnota() + "/" + polozky.get(j).getVaha());
                /* pokud je uz batoh plny, tak break */
                if ( batoh.isFull() ) break;
                /* jinak pridame dalsi polozku */
                item = polozky.get(j);
                batoh.addItem(item);
            }
        }
    }

    /**
     * Vypise pole reprezentujici stav batohu
     * @param stav
     */
    public void printState(int[] stav) {
        System.out.print("[");
        for (int i = 0; i < stav.length; i++) {
            int j = stav[i];
            System.out.print(j + ",");
        }
        System.out.println("]");
    }

    /**
     * Vypise vice stavu
     * @param stavy
     */
    public void printStates(List<int[]> stavy) {
        for (int i = 0; i < stavy.size(); i++) {
            int[] stav = stavy.get(i);
            printState(stav);
        }
    }

    /**
     * Zjistime maximalni moznou cenu daneho stavu
     * Stavy od posledni jednicky doleva jsou jiz dane, od posledni jednicky tam jeste mohou byt
     * @param poleBitu
     * @return int suma
     */
    private int getStatePrice(int[] poleBitu, int last) {
        int suma = 0;
        for (int i = 0; i < poleBitu.length; i++) {
            if ( (poleBitu[i] == 1) || (i >= last) ) {
                suma += barak.polozky.get(i).getHodnota();
            }
        }
        return suma;
    }

}
