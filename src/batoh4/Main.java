package batoh4;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Reseni problemu batohu hrubou silou a jednoduchou heuristikou
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class Main {

    /* nosnost batohu pro testovaci funkci */
    final static int NOSNOST = 32;
    /* maximalni pocet nactenych instanci (spis pro testovani) */
    final static int MAX_INSTANCES = 1;
    /* soubor s instancema (soubory jsou: 4, 10, 15, 20, 22, 25, 27, 30, 32, 35, 37, 40) */
    final static int FILE_NO = 40;
    /* pocet opakovani celeho vypoctu */
    final static int ITERATION_NO = 1;
    /* pocatecni teplota */
    final static double INIT_TEMP = 500;
    /* minimalni teplota */
    final static double MIN_TEMP = 1;
    /* zchlazovaci koeficient 0,8 - 0,999 */
    final static double ZCHLAZOVACI_KOEF = 0.85;
    /* equilibrum koeficient */
    final static int EQUILIBRUM_KOEF = 100;

    /**
     * Polozky v baraku lze nacist bud z testovaci mnoziny - funkce loadTestItems,
     * nebo ze souboru - funkce loadItemsFromFile
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /* definuju si polozky v baraku */
        Barak barak = new Barak();

        /* definuju si batoh */
        Batoh batoh = new Batoh(NOSNOST);

        /* nacteme testovaci vstupy */
        // loadTestItems(barak);

        /* nacteme instance ze souboru */
        String[][] instanceProblemu = loadFile(FILE_NO, false);

        /* nacteme reseni problemu ze souboru */
        String[][] reseniProblemu = loadFile(FILE_NO, true);

        /* zapnu mereni casu */
        long startTime = System.currentTimeMillis();
        double odchylka = 0d, maximalniOdchylka = 0d, spravneReseni, relativniOdchylka, sumaOdchylek = 0d;
        int expandovanoSuma = 0;

        System.out.println("Vypis polozek: instance, optimalniCena, vypocitanaCena, zatizeni, nosnost");

        /* spustime cely vypocet nekolikrat */
        for (int a = 0; a < ITERATION_NO; a++) {
            /* projdeme vsechny instance a vypocitame je */
            for ( int i = 0; (i < instanceProblemu.length) && (i < MAX_INSTANCES); i++ ) {

                /* preskocime prazdne radky */
                if (instanceProblemu[i][0] == null) break;

                /* naplnime barak polozkami */
                loadItemFromFile(barak, instanceProblemu[i]);

                /* nastavime nosnost batohu */
                batoh.setNosnost(Integer.parseInt(instanceProblemu[i][2]));

                /* ziskej polozky v batohu z baraku */
                SimulatedCooling simCooling = new SimulatedCooling(barak, batoh);
                simCooling.setPocatecniTeplota(INIT_TEMP);
                simCooling.setMinimalniTeplota(MIN_TEMP);
                simCooling.setZchlazeniKoef(ZCHLAZOVACI_KOEF);
                simCooling.setEquilibrumKoef(EQUILIBRUM_KOEF);
                simCooling.computeStolenItems();

                /* vypiseme polozky */
                // System.out.println("Vypis polozek v batohu instance " + instanceProblemu[i][0]);
                // batoh.writeItems();
                System.out.println(instanceProblemu[i][0] + "\t" +
                                   reseniProblemu[i][2] + "\t" +
                                   batoh.getAktualniCena() + "\t" +
                                   batoh.getAktualniZatizeni() + "\t" +
                                   batoh.getNosnost() + "\t" +
                                   batoh.getExpandovano());
                expandovanoSuma += batoh.getExpandovano();

                /* vypocitame odchylku */
                spravneReseni = Double.parseDouble(reseniProblemu[i][2]);
                odchylka = (double) ( spravneReseni - (double) batoh.getAktualniCena() ) / spravneReseni;
                if ( odchylka > maximalniOdchylka ) maximalniOdchylka = odchylka;
                sumaOdchylek += odchylka;

                /* vycistime batoh i barak pred dalsi instanci */
                barak.clear();
                batoh.clear();
            }
        } // iteration

        /* konec mereni casu */
        long endTime = System.currentTimeMillis();
        startTime = endTime - startTime;
        int expandovanoPrumer = expandovanoSuma / 50;
        relativniOdchylka = sumaOdchylek / 50;
        System.out.println("Vypocet souboru " + FILE_NO + " trval " + startTime + "ms, expandovano " + expandovanoPrumer);
        System.out.println("Relativni/max odchylka: " + relativniOdchylka + "\t" + maximalniOdchylka);

    }

    /**
     * Nacteme polozky ze souboru
     * @param i - cislo souboru
     * @return Strin[][] - rozparsovany polozky v baraku
     */
    private static String[][] loadFile(int i, boolean reseni) {
        String soubor = "";
        if ( reseni ) {
            soubor = System.getProperty("user.dir") + "\\data\\knap_" + i + ".sol.dat";
        } else {
            soubor = System.getProperty("user.dir") + "\\data\\knap_" + i + ".inst.dat";
        }
        System.out.println("Nacitam soubor: " + soubor + ", existuje: " + new File(soubor).exists());
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        String[][] instance = new String[100][100];
        try {
            /* Zkopirovano z http://www.roseindia.net/java/beginners/java-read-file-line-by-line.shtml */
            fstream = new FileInputStream(soubor);
            // Get the object of DataInputStream
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            String strLine = "";
            int idecko = 0;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // System.out.println("Ctu radek " + strLine + ", ktery bude na indexu " + idecko);
                String[] radek = strLine.split(" ");
                instance[idecko] = radek;
                idecko++;
            }
            // Close the input stream
            in.close();

        } catch (Exception e) {
            System.err.println("Chyba v nacitani souboru, error: " + e);
            return null;
        }
        return instance;
    }

    /**
     * Z nactene radky souboru naplni barak polozkama
     * @param barak
     * @param parametry
     */
    private static void loadItemFromFile(Barak barak, String[] parametry) {
        /* pocet zaznamu ke cteni */
        int pocetZaznamu = (parametry.length - 3) / 2;
        // System.out.println("Pocet zaznamu k nacteni je " + pocetZaznamu + " z radku instance " + parametry[0]);
        int j = 3;
        for( int i = 0; i < pocetZaznamu; i++, j = j+2 ) {
            // System.out.println("Pridavam polozku {" + parametry[j] + "," + parametry[j+1] + "}");
            barak.addItem(
                    new BatohItem(
                        Integer.parseInt(parametry[j+1]),
                        Integer.parseInt(parametry[j])
                        )
                    );
        }
    }

    /**
     * Nacteme testovaci vstup
     * Vysledek by mel byt 126, nebo 127 (v pripade lokalniho dohledavani)
     */
    public static void loadTestItems(Barak barak) {
        barak.addItem(new BatohItem(15, 17));
        barak.addItem(new BatohItem(26, 1));
        barak.addItem(new BatohItem(4, 29));
        barak.addItem(new BatohItem(18, 11));
        barak.addItem(new BatohItem(11, 15));
        barak.addItem(new BatohItem(22, 6));
        barak.addItem(new BatohItem(25, 6));
        barak.addItem(new BatohItem(17, 7));
        barak.addItem(new BatohItem(35, 8));
        barak.addItem(new BatohItem(1, 2));
        barak.addItem(new BatohItem(5, 32));
    }
}
