package batoh4;

import java.util.List;
import java.util.Random;

/**
 * Implementace metodou simulovaneho ochlazovani
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class SimulatedCooling implements IAlgorithm {

    /* v baraku jsou veci a davaji se do batohu */
    private Batoh   batoh;
    private Barak   barak;
    /* ceny a vahy */
    private int     bestCena = 0;
    private int     bestCenaVaha = 0;
    private List<BatohItem> bestPolozky = null;
    private int[]   aktualniStav = null;
    private int     expandovano = 0;
    /* telpoty pro ochlazovani */
    private double  pocatecniTeplota;
    private double  minimalniTeplota;
    private double  aktualniTeplota;
    /* settings  - setujeme pres settery */
    private double  zchlazeniKoef; // 0,8 - 0,999
    private int     equilibrumKoef;

    public SimulatedCooling(Barak barak, Batoh batoh) {
        this.barak = barak;
        this.batoh = batoh;
        aktualniStav = new int[barak.getItemsCount()];
        this.expandovano = 0;
    }

    /**
     * Spustime algoritmus prochazeni stavoveho prostoru
     */
    @Override
    public void computeStolenItems() {

        /* init */
        List<BatohItem> polozky = this.barak.getPolozky();
        int kapacita = this.batoh.getNosnost();
        int aktualniCena = 0;
        int innerCycle = 0;
        int[] novyStav = new int[this.barak.getItemsCount()];

        /* vlozime na zasobnik pocatecni stav */
        aktualniStav = getInitialState();
        aktualniTeplota = (pocatecniTeplota * kapacita) / Math.log(2);
        // printState(aktualniStav);

        /* dokud mame spravnou teplotu */
        while( !isFrozen(aktualniTeplota) ) {

            System.out.println("Aktualni teplota je " + aktualniTeplota);

            innerCycle = 0;

            while ( equilibrum(innerCycle, polozky.size()) ) {
                /* pocet stavu counter */
                expandovano++; innerCycle++;
                /* ziskame dalsi stav */
                novyStav = getNextState(aktualniStav, polozky);

                /* vysypeme batoh */
                batoh.clear();
                /* naplnime batoh */
                fillBatoh(polozky, novyStav);
                /* mrkneme kolik se podarilo ukradnout a pokud je to nejlepsi vysledek, tak ulozime */
                aktualniCena = this.batoh.getAktualniCena();
                if ( isBetter(aktualniCena) ) {
                    // System.out.println("Nasli jsme lepsi reseni s cenou " + aktualniCena + " a vahou " + this.batoh.getAktualniZatizeni());
                    this.bestCena = aktualniCena;
                    this.bestCenaVaha = this.batoh.getAktualniZatizeni();
                    this.bestPolozky = this.batoh.getPolozky();
                }
            }
            /* zchladime */
            aktualniTeplota = coolDown(aktualniTeplota);
        }

        /* konec algoritmu, takze musime do batohu dat nejlepsi vysledek */
        this.batoh.setPolozky(this.bestPolozky);
        this.batoh.setAktualniCena(this.bestCena);
        this.batoh.setAktualniZatizeni(this.bestCenaVaha);
        this.batoh.setExpandovano(this.expandovano);
    }

    /**
     * Vratime pocatecni stav
     * @return
     */
    private int[] getInitialState() {
        int[] pole = new int[barak.getItemsCount()];
        pole[0] = 1;
        return pole;
    }

    /**
     * Vrati stav, ktery se lisi v nahodnem bitu
     * @param aktualniStav
     * @return
     */
    private int[] getNextState(int[] aktualniStav, List<BatohItem> polozky) {

        // puvodni stav
        batoh.clear();
        fillBatoh(polozky, aktualniStav);
        int cena1 = this.batoh.getAktualniCena();

        // ziskame novy vedlejsi stav
        int[] novyStav = getRandomState(aktualniStav);
        batoh.clear();
        fillBatoh(polozky, aktualniStav);
        int cena2 = this.batoh.getAktualniCena();

        // pokud je novy stav lepsi, tak vratime lepsi
        if ( cena2 > cena1 ) {
            return novyStav;
        
        // pokud novy stav neni lepsi
        } else {
            int delta = cena2 - cena1;
            Random randomObj = new Random();
            double x = randomObj.nextDouble();
            // x < exp(-delta/T)?
            return ( x < Math.exp(-delta / this.zchlazeniKoef) ) ? novyStav : aktualniStav;
        }
    }

    /**
     * Vrati stav, ktery se lisi v nahodnem bitu
     * @param aktualniStav
     * @return
     */
    private int[] getRandomState(int[] aktualniStav) {
        int[] novyStav = aktualniStav;
        // System.out.println("getNextState vstup:"); printState(novyStav);
        // zvolime nahodne index
        Random index = new Random();
        int random = index.nextInt(novyStav.length);
        // odeberu, nebo pridam polozku na nahodnem indexu
        novyStav[random] = (novyStav[random] == 0) ? 1 : 0;
        // System.out.println("getNextState výstup:"); printState(novyStav);
        return novyStav;
    }

    /**
     * Urci equilibrum
     * @param innerCycle
     * @param kapacita
     * @return
     */
    private boolean equilibrum(int innerCycle, double kapacita) {
        return ( innerCycle < ( equilibrumKoef * kapacita ));
    }

    /**
     * Vrati, jestli jsme prekrocili minimalni teplotu
     * @param aktualniTeplota
     * @return boolean
     */
    private boolean isFrozen(double aktualniTeplota) {
        return aktualniTeplota < this.minimalniTeplota;
    }

    /**
     * Provede zchlazeni
     * @param temperature
     * @return
     */
    private double coolDown(double aktualniTeplota) {
        return (aktualniTeplota * this.zchlazeniKoef);
    }

    /**
     * Vrati jestli je stav lepsi, nez aktualne nejlepsi
     * @param aktualniCena
     * @return
     */
    private boolean isBetter(int aktualniCena) {
        return (aktualniCena > this.bestCena);
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

    public void setPocatecniTeplota(double pocatecniTeplota) {
        this.pocatecniTeplota = pocatecniTeplota;
    }

    public void setMinimalniTeplota(double minimalniTeplota) {
        this.minimalniTeplota = minimalniTeplota;
    }

    public double getAktualniTeplota() {
        return aktualniTeplota;
    }

    public void setZchlazeniKoef(double zchlazeniKoef) {
        this.zchlazeniKoef = zchlazeniKoef;
    }

    public void setEquilibrumKoef(int equilibrumKoef) {
        this.equilibrumKoef = equilibrumKoef;
    }

}
