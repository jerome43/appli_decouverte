package fr.insensa.decouverte_patrimoine.android;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

// extends MainActivity nécessaire pour récupérer variables globales de l'appli

public class Jeu02 extends MainActivity implements ApplicationListener, GestureDetector.GestureListener {

    ActionResolver actionResolver;
    Jeu02(ActionResolver actionResolver)    {
        this.actionResolver = actionResolver;
    }

    // les font utilisées pour écriture des textes
    private BitmapFont font;

    // les images utilisées (Texture à image PNG)
    private Texture jeu01;
    private Texture petitsBravo;

  //  ShapeRenderer shapeRenderer;

    private Texture bouquetinRVB;
    private Texture chevalRVB;
    private Texture sanglierRVB;
    private Texture vacheRVB;

    private Texture bouquetinGris;
    private Texture chevalGris;
    private Texture sanglierGris;
    private Texture vacheGris;


    // SpriteBatch is given a texture and coordinates for each rectangle to be drawn
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Array<com.badlogic.gdx.math.Rectangle> animauxRectangleTableau;
    private Array<Texture> animauxImagesTableauAffichage;
    private Array<Texture> animauxImagesTableauRVB;
    private Array<Texture> animauxImagesTableauGris;
    // le cadre où placer l'animal gagnant
    private com.badlogic.gdx.math.Rectangle cadre;
    // pour indiquer qu'on a gagné
    private boolean gagne;
    // hauteur et largeur de l'écran du device utilisé
    private int screenWidth;
    private int screenHeight;
    private int origineX;
    private int origineY;
    // les positions du toucher utilisateur à l'écran
    private float touchPosX;
    private float touchPosY;
   // private float deltaPosX;
   // private float deltaPosY;
    // position initiale du touché utilisateur
    private float initTtouchPosX;
    private float initTouchPosY;
    // pour garder coordonnées initiale d'un animal lorsque'on le touche pour la premère fois
    private boolean firstTouchedSoInitTouchPos;
    // connaître l'index de l'animal qui vient d'être touché
    private int indexAnimalTouched;
    // pour arrêter la lecture du tableau de position des animaux (utile dans pan())
    boolean stopParcoursTableau;
    // pour voir si c'est la première fois que l'appli est lançée
    private boolean firstStart;
    // Le numéro de parcours en cours
    private String numeroParcours;


    @Override
    public void create() {

      //  shapeRenderer = new  ShapeRenderer();

        // le fichier de la police utilisée
        font = new BitmapFont(Gdx.files.internal("calibri.fnt"));

        // mise à zéro des position utilisateur
        touchPosX =-1;
        touchPosY =-1;
      //  deltaPosX=-1;
      //  deltaPosY=-1;
        // personne n'a encore gagné
        gagne = false;
        // aucun animal touché, donc on récupèrera ses coordonnées initiales
        firstTouchedSoInitTouchPos = true;
        // on parcourera au premier pan le tableau de position des animaux
        stopParcoursTableau = false;
        // on indique que c'est la première fois que l'appli est lançée
        firstStart = true;

        // récupération de la hauteur et de la largeur de la zone d'affichage en fonction du device
        // cad taille écran moins les barres d'actions du haut et du bas
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        // L'origine de l'image de fond (coin inférieur gauche de l'image et non de son affichage à l'écran) à partir duquel on positionne nos objets
        origineX= screenWidth/2 - 960/2;
        origineY= screenHeight/2-1380/2;

        // pour initialiser la détection des touchers écran
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);

        // récupérartion du numéro de parcours défini dans la classe MainActivity
        numeroParcours = MainActivity.getNumero_parcours_main();
        // associer les textures aux images

        // récupération des images
        String path = "Android/data/fr.insensa.decouverte_patrimoine.android/files/" + numeroParcours + "/";
        jeu01 = new Texture(Gdx.files.external(path + "jeu01.png"));
        petitsBravo = new Texture(Gdx.files.external(path + "petisBravo.png"));

        bouquetinRVB = new Texture(Gdx.files.external(path + "bouquetin.png"));
        chevalRVB = new Texture(Gdx.files.external(path + "cheval.png"));
        sanglierRVB = new Texture(Gdx.files.external(path + "sanglier.png"));
        vacheRVB = new Texture(Gdx.files.external(path + "vache.png"));

        bouquetinGris = new Texture(Gdx.files.internal("bouquetinGris.png"));
        chevalGris = new Texture(Gdx.files.internal("chevalGris.png"));
        sanglierGris = new Texture(Gdx.files.internal("sanglierGris.png"));
        vacheGris = new Texture(Gdx.files.internal("vacheGris.png"));

        // camera
        camera = new OrthographicCamera();
        // Sets this camera to an orthographic projection using a viewport fitting the screen resolution,
        // centered at (Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2), with the y-axis pointing up or down (boolean yDown)
        camera.setToOrtho(false);


        System.out.println( screenWidth + " / " + screenHeight);

        batch = new SpriteBatch();

        // rectangle des fenetres
        // les rectangles doivent être réglés aux mêmes dimensions que les images qu'ils contiennent si l'on veut gérer les intersections adaptées à ce que voit l'utilisateur
        // la taille du rectangle n'a rien à voir avec la taille de l'image car il ne la contient pas, on leur affecte juste les mêmes coordonnées géographiques dans le batch
        cadre = new com.badlogic.gdx.math.Rectangle();
        cadre.set(origineX + 290, origineY + 640, 300, 400);

       animauxRectangleTableau = new Array<com.badlogic.gdx.math.Rectangle>();
       animauxImagesTableauAffichage = new Array<Texture>();
       animauxImagesTableauGris = new Array<Texture>();
       animauxImagesTableauRVB = new Array<Texture>();
       creerTableauImageAnimauxAffichage();
       creerTableauAnimauxRVB();
       creerTableauImageAnimauxGris();

        for(int i =1; i<5; i++) {
            creerRectangleAnimaux();
        }
    }

    private void creerRectangleAnimaux() {
        com.badlogic.gdx.math.Rectangle rectangleAnimal = new com.badlogic.gdx.math.Rectangle();
        rectangleAnimal.setPosition(MathUtils.random(120, screenWidth - 120), MathUtils.random(150, screenHeight/2 -250));
        rectangleAnimal.width = 240;
        rectangleAnimal.height = 160;
        animauxRectangleTableau.add(rectangleAnimal);
      //  lastDropTime = TimeUtils.nanoTime();
    }

    private void creerTableauAnimauxRVB() {
        animauxImagesTableauRVB.add(bouquetinRVB);
        animauxImagesTableauRVB.add(chevalRVB);
        animauxImagesTableauRVB.add(sanglierRVB);
        animauxImagesTableauRVB.add(vacheRVB);
    }

    private void creerTableauImageAnimauxAffichage() {
        animauxImagesTableauAffichage.add(bouquetinRVB);
        animauxImagesTableauAffichage.add(chevalRVB);
        animauxImagesTableauAffichage.add(sanglierRVB);
        animauxImagesTableauAffichage.add(vacheRVB);
        //  lastDropTime = TimeUtils.nanoTime();
    }


    private void creerTableauImageAnimauxGris() {
        animauxImagesTableauGris.add(bouquetinGris);
        animauxImagesTableauGris.add(chevalGris);
        animauxImagesTableauGris.add(sanglierGris);
        animauxImagesTableauGris.add(vacheGris);
    }


    @Override
    public void render() {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        if (firstStart) {

            actionResolver.showLongToast("Trouves le bon animal et replace le au centre du panneau");

            firstStart=false;
        }

        // begin a new batch
        batch.begin();
        // affichage et centrage de l'image du jeu (de dimension 960*1381)
        batch.draw(jeu01,  origineX, origineY);

        if (gagne == true)  {
            //libération des ressources de jeu : ça efface jeu01
            jeu01.dispose();
            // libération des images des animaux
                for (Texture animauxImages : animauxImagesTableauAffichage) {
                animauxImages.dispose();
            }
            for (Texture animauxImages : animauxImagesTableauGris) {
                animauxImages.dispose();
            }

            for (Texture animauxImages : animauxImagesTableauRVB) {
                animauxImages.dispose();
            }
            // affichage et centrage de l'image bravo (de dimension 960*1381)
            batch.draw(petitsBravo,  origineX, origineY);
            font.setColor(0,0,0,1);
            // le cadre est décalé par rapport au centre de l'image originale de -147 / -543
            font.draw(batch, "bravo, tu as gagné", screenWidth/2 -147, screenHeight/2+543);
        }
        // tant qu'on a pas gagné, on met à jour la position des animaux
        // en récupérant leur valeur dans le tableau de position des animaux
        else {
            int i=0;
            for(com.badlogic.gdx.math.Rectangle animalRectangleTableau: animauxRectangleTableau) {
                // affichage des animaux
                batch.draw(animauxImagesTableauAffichage.get(i), animalRectangleTableau.getX(), animalRectangleTableau.getY());
                i++;
             /*    utile pour visualiser les rectangles des animaux
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0, 1, 0, 1);
                shapeRenderer.rect(animalRectangleTableau.getX(), animalRectangleTableau.getY(), animalRectangleTableau.getWidth(), animalRectangleTableau.getHeight());
                shapeRenderer.rect(origineX + 290, origineY + 640, 300, 400);
                shapeRenderer.end();*/
            }
        }
            batch.end();
    }

    @Override
    public void dispose() {
    }
    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void resize(int width, int height) {

    }

    // ----------------------------------- detection des interactions écran avec l'utilisateur -------------------------------------

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        System.out.println("touchdown");
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        System.out.println("tap");
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    // lancement avec le doigt
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    // déplacement du doigt à l'écran
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        touchPosX =x;
        // on décale touchPosY de 73 car ses coordonnées débutent à 0 au bas de l'écran du device alors que les coordonnées du batch débutent à 0 à la zone d'affichag du jeu,
        // cad au dessus de la barre d'action du bas, d'une hauteur de 73px environ
        touchPosY =y+73;
        System.out.println("pan");

        // si c'est la première fois qu'on touche l'animal, on va garder en mémoire ses coordonnées
        if (firstTouchedSoInitTouchPos) {
            initTtouchPosX = x;
            initTouchPosY = y+73;
        }
        // puis on remets firstTouch à false pour ne pas réécrire la variable
        firstTouchedSoInitTouchPos = false;

        // tant qu'on a pas encore touché un animal et stoppé le parcours du tableau de position des animaux
        if (!stopParcoursTableau) {
            // parcours du tableau de position des animaux pour voir si l'un d'eux est touché
            for (int i=0; i<animauxRectangleTableau.size; i++) {
                // si l'animal contient les coordonnées x, il est touché
                if(animauxRectangleTableau.get(i).contains(touchPosX, screenHeight- touchPosY)) {
                    // on met la variable à false pour ne pas reparcourir le tableau des position au prochain pan
                    stopParcoursTableau =true;
                    // on indique l'index de l'animal qui est touché
                    indexAnimalTouched = i;

                    // et on déplace l'animal
                    animauxRectangleTableau.get(indexAnimalTouched).setCenter(touchPosX, screenHeight - touchPosY);
                }
            }
        }

        // si on a déjà touché un animal, pas besoin de reparcourir le tableau de positionnement
        // pour voir si l'un d'eux est touché
        else {
            // on change la couleur de l'animal en gris
            animauxImagesTableauAffichage.set(indexAnimalTouched, animauxImagesTableauGris.get(indexAnimalTouched));
            // puis on déplace l'animal
            animauxRectangleTableau.get(indexAnimalTouched).setCenter(touchPosX, screenHeight - touchPosY);
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        System.out.println("panStop");

        // si l'animal a été relaché dans le cadre
        if(animauxRectangleTableau.get(indexAnimalTouched).overlaps(cadre)) {
            System.out.println("overlapsed");
            // et si c'est l'animal d'index 0 (le gagnant)
            if(animauxRectangleTableau.get(indexAnimalTouched).equals(animauxRectangleTableau.get(0)))  {
                // mise à jour de la variable gagne
                gagne=true;
            }
            // si ce n'est pas l'animal gagnant, il repart à son point d'origine
            else {
                // on remet d'abord l'image RVB
                animauxImagesTableauAffichage.set(indexAnimalTouched, animauxImagesTableauRVB.get(indexAnimalTouched));
                animauxRectangleTableau.get(indexAnimalTouched).setCenter(initTtouchPosX, screenHeight - initTouchPosY);
            }
        }

        // on indique qu'on peut parcourir à nouveau le tableau des animaux pour savoir si l'un d'eux est touché
        stopParcoursTableau = false;

        // on mets firstTouch à true pour pouvoir réécrire la variable au prochain pan
        firstTouchedSoInitTouchPos =true;

        // on remet l'image RVB
        animauxImagesTableauAffichage.set(indexAnimalTouched, animauxImagesTableauRVB.get(indexAnimalTouched));
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    // pincement
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
