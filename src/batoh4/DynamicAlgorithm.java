package batoh4;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementace problemu batohu pomoci dynamickeho programovani
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class DynamicAlgorithm implements IAlgorithm {

    // batoh a barak pro uchovani polozek
    private Batoh batoh;
    private Barak barak;
    // tabulka pro uchovani vypocitanych instanci [nosnost][cena]
    private Batoh[][] vypocitane;
    // debug promenna pro vypis hlasek
    private boolean DEBUG = false;
    // pocitadlo expandovanych stavu
    public int expandovane = 0;

    public DynamicAlgorithm(Batoh batoh, Barak barak) {
        this.batoh = batoh;
        this.barak = barak;
        this.vypocitane = new Batoh[barak.polozky.size() + 1][batoh.getNosnost() + 1];
        this.expandovane = 0;
    }

    public void computeStolenItems() {
        // vytvorime plny batoh, kde budou vsechny polozky z baraku
        Batoh startovni = new Batoh(this.batoh.getNosnost());
        startovni.setPolozky(barak.getPolozky());
        // spustime rekurzivni vypocet
        Batoh cilovy = solveInstance(startovni);
        /* konec algoritmu, takze musime do batohu dat nejlepsi vysledek */
        if ( DEBUG ) {
            System.out.println("Rekurze skoncila.");
            printStateDebug("Cilovy stav je", cilovy);
        }
        this.batoh.setPolozky(cilovy.getPolozky());
        this.batoh.setAktualniCena(cilovy.getAktualniCena());
        this.batoh.setAktualniZatizeni(cilovy.getAktualniZatizeni());
        this.batoh.setExpandovano(this.expandovane);
    }

    /**
     * Rekurzivni funkce, ktera vypocita jednu instanci batohu, pseudokod:
     *
     * if isTrivial(V, C, M) return trivialKNAP(V, C, M); // ukončení rekurze, je-li instance triviální
     * (X0, C0, m0) = KNAP(V-{vn}, C-{cn}, M);            // vyřeš batoh, ve kterém n-tá věc není
     * (X1, C1, m1) = KNAP(V-{vn}, C-{cn}, M-vn);         // vyřeš batoh, ve kterém n-tá věc je
     * if (C1+cn) > C0 return(X1.1,  C1+cn, m1+vn);       // jaká varianta je lepší?
     *      else return(X0.0, C0, m0);
     *
     * (V, C, M) = V vahy veci, C ceny veci, M zatizeni batohu
     *
     * @param state
     * @return Batoh state
     */
    private Batoh solveInstance(Batoh state) {

        if ( DEBUG ) printStateDebug("Vstupuji do instance", state);
        this.expandovane++;

        // pokud je to trivialni reseni, tak vrat trivialni
        if ( isTrivialInstance(state) ) return getTrivialInstance(state);

        // pokud stav uz zname, vratime stav z tabulky reseni
        if ( isStateSolved(state) ) return getSolvedState(state);

        // aktualne resene polozky (jejich kopie) a definice polozky pro odebrani
        List<BatohItem> aktualniPolozky = new ArrayList<BatohItem>(state.getPolozky());
        BatohItem odebiranaPolozka = aktualniPolozky.get(aktualniPolozky.size() - 1);
        int vahaOdebiranePolozky = odebiranaPolozka.getVaha();
        int cenaOdebiranePolozky = odebiranaPolozka.getHodnota();
        aktualniPolozky.remove(odebiranaPolozka);

        // spustime jednu vetev rekurze, kde n-ta polozka *JE* a snizime mozne zatizeni batohu
        // (X1, C1, m1) = KNAP(V-{vn}, C-{cn}, M-vn)
        Batoh novyBatoh1 = new Batoh(state.getNosnost() - vahaOdebiranePolozky);
        novyBatoh1.setPolozky(aktualniPolozky);
        Batoh stavKdePolozkaJe = solveInstance(novyBatoh1);
        // pokud neni takovy stav ulozeny v tabulce, tak udelame jeho kopii a ulozime ho
        if ( !isStateSolved(novyBatoh1) /* & (stavKdePolozkaJe.getPolozky().size() <= barak.polozky.size())*/ ) {
            // ulozime kopii do tabulky vypocitanych stavu
            saveSolvedState(stavKdePolozkaJe, novyBatoh1);
        }

        // spustime druhou vetev rekurze, kde n-ta polozka *NENI*
        // (X0, C0, m0) = KNAP(V-{vn}, C-{cn}, M)
        Batoh novyBatoh2 = new Batoh(state.getNosnost());
        novyBatoh2.setPolozky(aktualniPolozky);
        Batoh stavKdePolozkaNeni = solveInstance(novyBatoh2);
        // pokud neni takovy stav ulozeny v tabulce, tak udelame jeho kopii a ulozime ho
        if ( !isStateSolved(novyBatoh2) /*& (stavKdePolozkaNeni.getPolozky().size() <= barak.polozky.size())*/ ) {
            // ulozime kopii do tabulky vypocitanych stavu
            saveSolvedState(stavKdePolozkaNeni, novyBatoh2);
        }

        // porovname oba vracene stavy - pseudokod:
        // if (C1+cn) > C0 return(X1.1, C1+cn, m1+vn)
        //            else return(X0.0, C0, m0)
        if ( ((stavKdePolozkaJe.getAktualniCena() + cenaOdebiranePolozky) >
               stavKdePolozkaNeni.getAktualniCena()) ) {

            // zkusime tam vratit polozku zpet a kdy to projde, tak vratime novy stav
            stavKdePolozkaJe.setNosnost(state.getNosnost());
            if ( stavKdePolozkaJe.addItem(odebiranaPolozka) ) {
                if ( DEBUG ) printStateDebug("Vracim polozku kde JE", stavKdePolozkaJe);
                return stavKdePolozkaJe;

            } else {
                if ( DEBUG ) printStateDebug("Nepovedlo se pridat, vracim polozku kde NENI", stavKdePolozkaNeni);
                return stavKdePolozkaNeni;
            }

        } else {
            if ( DEBUG ) printStateDebug("Vracim polozku kde NENI", stavKdePolozkaNeni);
            return stavKdePolozkaNeni;
        }
    }

    /**
     * Je to trivialni reseni?
     * Pseudokod:
     * - return(isEmpty(V) or M=0 or M<0)
     *
     * @param state
     * @return boolean
     */
    private boolean isTrivialInstance(Batoh state) {
        return ( (state.getNosnost() < 1) ||
                ( state.polozky.isEmpty() ) );
    }

    /**
     * Zjistime jestli mame stav jiz vyreseny
     * @param state
     * @return boolean
     */
    private boolean isStateSolved(Batoh state) {
        return (vypocitane[state.getPolozky().size()][state.getNosnost()] != null);
    }

    /**
     * Vrati vyreseny stav z tabulky reseni
     * @param state
     * @return Batoh state
     */
    private Batoh getSolvedState(Batoh state) {
        if ( DEBUG ) { System.out.println("Nasel jsem stav (" + state.getPolozky().size() +
                            "," + state.getNosnost() + ") v tabulce, vracim kopii."); }
        return vypocitane[state.getPolozky().size()][state.getNosnost()].clone();
    }

    /**
     * Ulozi nove vyreseny stav do tabulky reseni
     * @param solution
     * @param problem
     */
    private void saveSolvedState(Batoh solution, Batoh problem) {
        Batoh solutionCopy = solution.clone();
        if ( DEBUG ) { System.out.println("Ukladam stav (" + solution.getPolozky().size() +
                                "," + solution.getNosnost() + ") do tabulky."); }
        vypocitane[problem.getPolozky().size()][problem.getNosnost()] = solutionCopy;
    }

    /**
     * Vrati trivialni reseni
     * @param state
     * @return
     */
    private Batoh getTrivialInstance(Batoh state) {
        if ( DEBUG ) {
            System.out.println("Vracim polozku TRIV (M=" + state.getNosnost() +
                                ", n=" + state.getPolozky().size() +
                                ", sumaV=" + state.getAktualniZatizeni() +
                                ", sumaC=" + state.getAktualniCena() +
                                "), polozky nuluji a nosnost take pokud byla mensi jak 0.");
        }
        // TODO return vypocitane[0][0];
        if ( state.getNosnost() <= 0 ) state.setNosnost(0);
        state.setPolozky();
        return state;
    }

    /**
     * Vypise vypocitane stavy
     */
    private void printVypocitane() {
        System.out.println("Vypisuji vypocitane stavy:");
        for (int i = 0; i < vypocitane.length; i++) {
            for (int j = 0; j < vypocitane[i].length; j++) {
                if ( vypocitane[i][j] != null ) {
                    Batoh batohs = vypocitane[i][j];
                    System.out.print("{" + batohs.getAktualniCena() + "," + batohs.getAktualniZatizeni() + "}");
                }
            }
        }
    }

    /**
     * Vypise hlasku spolecne s vypisem stavu
     * @param hlaska
     * @param state
     */
    private void printStateDebug(String hlaska, Batoh state) {
        System.out.println(hlaska + " (M=" + state.getNosnost() +
                            ", n=" + state.getPolozky().size() +
                            ", sumaV=" + state.getAktualniZatizeni() +
                            ", sumaC=" + state.getAktualniCena() + ")");
    }

}
