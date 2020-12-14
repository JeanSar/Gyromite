/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.*;

//import modele.plateau.Couleur;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;//pour la lecture


import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/** Actuellement, cette classe gère les postions
 * (ajouter conditions de victoire, chargement du plateau, etc.)
 */
public class Jeu {

    public static final int SIZE_X = 20;
    public static final int SIZE_Y = 10;

    // compteur de déplacements horizontal et vertical (1 max par défaut, à chaque pas de temps)
    private HashMap<Entite, Integer> cmptDeplH = new HashMap<Entite, Integer>();
    private HashMap<Entite, Integer> cmptDeplV = new HashMap<Entite, Integer>();

    private Heros hector;
    private Bot smick1,smick2;
    private Corde [] cordes;
    private Colonne [] colonnes;
    private Colonnes colo;
    private Ramassable [] ramassables;

    private int nbBombe;
    private boolean estFini;

    private HashMap<Entite, Point> map = new  HashMap<Entite, Point>(); // permet de récupérer la position d'une entité à partir de sa référence
    private Entite[][] grilleEntites = new Entite[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées

    Gravite g = new Gravite();
    IA ia = new IA();

    private Ordonnanceur ordonnanceur = new Ordonnanceur(this);

    public Jeu() {
        initialisationDesEntites();
    }

    public void resetCmptDepl() {
        cmptDeplH.clear();
        cmptDeplV.clear();
    }

    public void start(long _pause) {
        ordonnanceur.start(_pause);
    }
    
    public Entite[][] getGrille() {
        return grilleEntites;
    }
    
    public Heros getHector() {
        return hector;
    }

    public boolean getestFini(){
        return estFini;
    }
    
    private void initialisationDesEntites() {
        estFini = false;

        hector = new Heros(this);
        addEntite(hector, 2, 6);

        /* tout marche, il suffit de les placer au bon endroits au final(semi-random car les collones doivent etre a cote des plateformes, et les smick sur des plateformes)*/

        /*
        smick1 = new Bot(this);
        addEntite(smick1,9,8);
        ia.addEntiteDynamique(smick1);
        ordonnanceur.add(ia);
*/

        smick2 = new Bot(this);
        addEntite(smick2,9,1);
        ia.addEntiteDynamique(smick2);
        ordonnanceur.add(ia);


        colo = new Colonnes(this);
        colo.setCouleur(Couleur.bleu);
        //colo.setHautOuBas(false);
        // colo.setMove(true);
        addEntite(colo, 11,7);
        Colonne col = new Colonne();
        col.getInstance().addEntiteDynamique(colo);
        ordonnanceur.add(col.getInstance());

        /*
        colonnes = new Colonne[3];
        for(int i=0;i<3;++i){
            colonnes[i] = new Colonne(this);
            addEntite(colonnes[i],12,6+i);
            colonnes[i].setHautOuBas(false);
            Controle4Directions.getInstance().addEntiteDynamique(colonnes[i]);
            ordonnanceur.add(Controle4Directions.getInstance());
        }

*/
        cordes = new Corde[10];
        for(int i=0;i<5;++i){
            cordes[i] = new Corde(this);
            addEntite(cordes[i],5,1+i);
        }

        for(int i=1;i<8;++i){
            cordes[i] = new Corde(this);
            addEntite(new Corde(this),10,9-i);
        }

        /*
        ramassables = new Ramassable[3];
        for(int i=0;i<3;++i){
            ramassables[i] = new Ramassable(this);
            addEntite(ramassables[i],7+i,8);
        }
*/

        addEntite(new Ramassable(this), 2, 3);
        addEntite(new Ramassable(this), 8, 8);


        //g.addEntiteDynamique(smick1);
        //ordonnanceur.add(g);

        g.addEntiteDynamique(smick2);
        ordonnanceur.add(g);

        g.addEntiteDynamique(hector);
        ordonnanceur.add(g);

        Controle4Directions.getInstance().addEntiteDynamique(hector);
        ordonnanceur.add(Controle4Directions.getInstance());

        // murs extérieurs horizontaux
        for (int x = 0; x < 20; x++) {
            addEntite(new Mur(this), x, 0);
            addEntite(new Mur(this), x, 9);
        }

        // murs extérieurs verticaux
        for (int y = 1; y < 9; y++) {
            addEntite(new Mur(this), 0, y);
            addEntite(new Mur(this), 19, y);
        }

        for (int x = 2; x < 8; x++) {
            addEntite(new Mur(this), x, 6);
        }
        for (int x = 0; x < 5; x++) {
            addEntite(new Mur(this), x, 4);
        }

        for (int x = 6; x < 10; x++) {
            addEntite(new Mur(this), x, 3);
        }
    }
/*
    private void initIdentiteCustomLevel(){
        String filePath = "data/custom_level.txt";
        File file= new File(filePath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        }catch(FileNotFoundException e){
            System.out.println("Fichier niveau custom non trouve");
            System.exit(0);
        }

        Gravite g = new Gravite();
        IA ia = new IA();
       // Colonnes c = new Colonnes();

        for(int x=0;x<SIZE_X;++x){
            for(int y=0;y<SIZE_Y;++y){
                String it = scanner.next();
                if(it.equals("M"))
                    addEntite(new Mur(this),x,y);
                //else if(it.equals("V")) //ajouter du vide ???
                  //  addEntite(new Vide(this),x,y);
                else if(it.equals("CB")) {
                    Colonnes col = new Colonnes(this);
                    col.setCouleur(Couleur.bleu);
                    addEntite(col, x,y);
                    Colonne _col = new Colonne();
                    _col.getInstance().addEntiteDynamique(col);
                    ordonnanceur.add(_col.getInstance());
                }
                else if(it.equals("CR")) {
                    Colonnes col = new Colonnes(this);
                    col.setCouleur(Couleur.rouge);
                    addEntite(col, x,y);
                    Colonne _col = new Colonne();
                    _col.getInstance().addEntiteDynamique(col);
                    ordonnanceur.add(_col.getInstance());
                }
                else if(it.equals("CD")) //support colonne droite
                    addEntite(new droiteColonne(this),x,y);
                else if(it.equals("CG")) //support colonne gauche
                    addEntite(new gaucheColonne(this),x,y);
                else if(it.equals("B"))
                    nb_bombe++;
                    addEntite(new Bombe(this),x,y);
                else if(it.equals("R"))
                    addEntite(new Ramassable(this),x,y);
                else if(it.equals("H")){ //heros du jeu
                    hector = new Heros(this);
                    Controle4Directions.getInstance().addEntiteDynamique(hector);
                    g.addEntiteDynamique(hector);
                    addEntite(hector, x, y);
                }
                else if(it.equals("S")){ //smicks
                    Bot smick = new Bot(this);
                    ia.addEntiteDynamique(smick);
                    g.addEntiteDynamique(smick);
                    addEntite(smick, x, y);
                }
            }
        }
        ordonnanceur.add(g);
        ordonnanceur.add(ia);
        ordonnanceur.add(Controle4Directions.getInstance());
    }
*/


    private void addEntite(Entite e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }

    public void supprimerEntite(EntiteDynamique e){
        //Point pt = new Point(map.get(e));
        //map.remove(e);
        //grilleEntites[pt.x][pt.y] = null;
        g.removeEntiteDynamique(e);
        if(e instanceof Bot)
            ia.removeEntiteDynamique(e);
        if(e instanceof Heros)
            Controle4Directions.getInstance().removeEntiteDynamique(e);

    }
    /** Permet par exemple a une entité  de percevoir sont environnement proche et de définir sa stratégie de déplacement
     *
     */
    public Entite regarderDansLaDirection(Entite e, Direction d) {
        Point positionEntite = map.get(e);
        return objetALaPosition(calculerPointCible(positionEntite, d));
    }
    
    /** Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il est réalisé
     * Sinon, rien n'est fait.
     */
    public boolean deplacerEntite(Entite e, Direction d) {
        boolean retour = false;
        
        Point pCourant = map.get(e);
        
        Point pCible = calculerPointCible(pCourant, d);
        
        if (contenuDansGrille(pCible) && objetALaPosition(pCible) == null || (objetALaPosition(pCible) != null && objetALaPosition(pCible).peutPermettreDeMonterDescendre())) { // a adapter (collisions murs, etc.)
            // compter le déplacement : 1 deplacement horizontal et vertical max par pas de temps par entité
            switch (d) {
                case bas, haut:
                    if (cmptDeplV.get(e) == null) {
                        cmptDeplV.put(e, 1);

                        retour = true;
                    }
                    break;
                case gauche, droite:
                    if (cmptDeplH.get(e) == null) {
                        cmptDeplH.put(e, 1);
                        retour = true;

                    }
                    break;
            }
        }

        if (retour) {
            deplacerEntite(pCourant, pCible, e);
        }

        return retour;
    }
    
    
    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;
        
        switch(d) {
            case haut: pCible = new Point(pCourant.x, pCourant.y - 1); break;
            case bas : pCible = new Point(pCourant.x, pCourant.y + 1); break;
            case gauche : pCible = new Point(pCourant.x - 1, pCourant.y); break;
            case droite : pCible = new Point(pCourant.x + 1, pCourant.y); break;
            case diagBasGauche: pCible = new Point(pCourant.x - 1, pCourant.y + 1); break;
            case diagBasDroite: pCible = new Point(pCourant.x + 1, pCourant.y + 1); break;
            case diagHautGauche: pCible = new Point(pCourant.x + 1, pCourant.y - 1); break;
            case diagHautDroite: pCible = new Point(pCourant.x + 1, pCourant.y - 1); break;
            case deuxHaut: pCible = new Point(pCourant.x, pCourant.y - 2); break;
            case deuxBas: pCible = new Point(pCourant.x, pCourant.y + 2); break;
        }
        return pCible;
    }
    
    private void deplacerEntite(Point pCourant, Point pCible, Entite e) {
        grilleEntites[pCourant.x][pCourant.y] = e.prec;
        e.prec = grilleEntites[pCible.x][pCible.y];
        grilleEntites[pCible.x][pCible.y] = e;
        map.put(e, pCible);
    }
    
    /** Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }
    
    private Entite objetALaPosition(Point p) {
        Entite retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }
        return retour;
    }

    public Ordonnanceur getOrdonnanceur() {
        return ordonnanceur;
    }

    public void finPartie(EntiteDynamique e){
        estFini = true;
        //initialisationDesEntites();
    }
}
