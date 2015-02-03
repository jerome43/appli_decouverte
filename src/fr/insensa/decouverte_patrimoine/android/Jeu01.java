package fr.insensa.decouverte_patrimoine.android;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


// extends MainActivity nécessaire pour récupérer variables globales de l'appli

public class Jeu01 extends MainActivity implements ApplicationListener, GestureDetector.GestureListener {

    ActionResolver actionResolver;
    Jeu01(ActionResolver actionResolver)    {
        this.actionResolver = actionResolver;
    }

    private Texture chenilleImage;
    private Texture bravoImage;
    private Texture ovaleImage;
    private Texture ovaleVertImage;

    private Texture fondJeu01;
    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Array<Circle> ovalePositionTableau;
    private Array<BitmapFont> ovaleTextesTableau;
    private Array<Texture> ovaleImageTableau;
    private Array<String> ovaleContenuTextesTableau;

    private com.badlogic.gdx.math.Rectangle chenillePosition;
    private Circle fenetre1Position;
    private Circle fenetre2Position;
    private Circle fenetre3Position;
    private Circle fenetre4Position;

    private boolean text1Placed;
    private boolean text2Placed;
    private boolean text3Placed;
    private boolean text4Placed;
    private boolean gagne;
    private int screenWidth;
    private int screenHeight;
    private int origineX;
    private int origineY;

    // Le numéro de parcours en cours
    private String numero;


    // les positions du toucher utilisateur à l'écran
    private float touchPosX;
    private float touchPosY;
    // position initiale du touché utilisateur
    private float initTtouchPosX;
    private float initTouchPosY;
    // pour garder coordonnées initiale d'un animal lorsque'on le touche pour la premère fois
    private boolean firstTouchedSoInitTouchPos;
    // connaître l'index de l'animal qui vient d'être touché
    private int indexOvaleTouched;
    // pour arrêter la lecture du tableau de position des animaux (utile dans pan())
    private boolean stopParcoursTableau;
    // pour voir si c'est la première fois que l'appli est lançée
    private boolean firstStart;


    //  private Music sonTrain;

    @Override
    public void create() {

        // pour initialiser la détection des touchers écran
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);

        // mise à zéro des position utilisateur
        touchPosX =-1;
        touchPosY =-1;

        // personne n'a encore gagné
        gagne = false;
        // aucun animal touché, donc on récupèrera ses coordonnées initiales
        firstTouchedSoInitTouchPos = true;
        // on parcourera au premier pan le tableau de position des animaux
        stopParcoursTableau = false;
        // on indique que c'est la première fois que l'appli est lançée
        firstStart = true;


        // récupérartion du numéro de parcours défini dans la classe MainActivity
        numero = MainActivity.getNumero_parcours_main();
        // associer les textures aux images

        String path = "Android/data/fr.insensa.decouverte_patrimoine.android/files/" + numero + "/";
        fondJeu01 = new Texture(Gdx.files.internal("jeu01.png"));
        chenilleImage = new Texture(Gdx.files.internal("chenille.png"));
        bravoImage = new Texture(Gdx.files.internal("ecran_moyens.png"));
        ovaleImage = new Texture(Gdx.files.internal("ovale.png"));
        ovaleVertImage = new Texture(Gdx.files.internal("ovale_vert.png"));
        BitmapFont texteFont = new BitmapFont(Gdx.files.internal("calibri.fnt"));

        camera = new OrthographicCamera();

        // camera
        // Sets this camera to an orthographic projection using a viewport fitting the screen resolution,
        // centered at (Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2), with the y-axis pointing up or down (boolean yDown)
        camera.setToOrtho(false);

        // récupération de la hauteur et de la largeur de la zone d'affichage en fonction du device
        // cad taille écran moins les barres d'actions du haut et du bas
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        System.out.println( screenWidth + " / " + screenHeight);

        // L'origine de l'image de fond (coin inférieur gauche de l'image et non de son affichage à l'écran) à partir duquel on positionne nos objets
        origineX= screenWidth/2 - 960/2;
        origineY= screenHeight/2-1380/2;

        batch = new SpriteBatch();

        // position de la chenille
        // les coordonnées sont le coin inférieur gauche
        chenillePosition = new com.badlogic.gdx.math.Rectangle();
        //chenillePosition.x = screenWidth / 2 - 600 / 2;
        chenillePosition.x = screenWidth / 2 - 600 / 2-1500;
        chenillePosition.y = screenHeight/2+165 ;

        // position des fenetres
        // les coordonnées sont le centre du cercle
        // les cercles doivent être réglés aux mêmes dimensions que les images qu'ils contiennent si l'on veut gérer les intersections adaptées à ce que voit l'utilisateur
        // la taille du rectangle ou du cercle n'a rien à voir avec la taille de l'image car il ne la contient pas, on leur affecte juste les mêmes coordonnées géographiques dans le batch
        // décalés de 50px pour centrer sur le cercle
        fenetre1Position = new com.badlogic.gdx.math.Circle();
        fenetre2Position = new com.badlogic.gdx.math.Circle();
        fenetre3Position = new com.badlogic.gdx.math.Circle();
        fenetre4Position = new com.badlogic.gdx.math.Circle();
        fenetre1Position.set(screenWidth/2 - 238, screenHeight/2+180+50, 50 );
        fenetre2Position.set(screenWidth/2 - 120, screenHeight/2+180+50, 50 );
        fenetre3Position.set(screenWidth/2 - 10, screenHeight/2+180+50, 50 );
        fenetre4Position.set(screenWidth/2 +100, screenHeight/2+180+50, 50 );

        // création du tableau contenant les textes de chaque ovale
       ovaleTextesTableau = new Array<BitmapFont>();
        ovaleTextesTableau.add(texteFont);
        ovaleTextesTableau.add(texteFont);
        ovaleTextesTableau.add(texteFont);
        ovaleTextesTableau.add(texteFont);
        ovaleTextesTableau.add(texteFont);
        ovaleTextesTableau.add(texteFont);
        ovaleTextesTableau.add(texteFont);
        ovaleTextesTableau.add(texteFont);
        //  lastDropTime = TimeUtils.nanoTime();

        String contenuTexte1 = "co";
        String contenuTexte2 = "que";
        String contenuTexte3 = "li";
        String contenuTexte4 = "cot";
        String contenuTexte5 = "a";
        String contenuTexte6 = "né";
        String contenuTexte7 = "mo";
        String contenuTexte8 = "ne";

        ovaleContenuTextesTableau = new Array<String>();
        ovaleContenuTextesTableau.add(contenuTexte1);
        ovaleContenuTextesTableau.add(contenuTexte2);
        ovaleContenuTextesTableau.add(contenuTexte3);
        ovaleContenuTextesTableau.add(contenuTexte4);
        ovaleContenuTextesTableau.add(contenuTexte5);
        ovaleContenuTextesTableau.add(contenuTexte6);
        ovaleContenuTextesTableau.add(contenuTexte7);
        ovaleContenuTextesTableau.add(contenuTexte8);

        ovaleImageTableau = new Array<Texture>();
        ovaleImageTableau.add(ovaleImage);
        ovaleImageTableau.add(ovaleImage);
        ovaleImageTableau.add(ovaleImage);
        ovaleImageTableau.add(ovaleImage);
        ovaleImageTableau.add(ovaleImage);
        ovaleImageTableau.add(ovaleImage);
        ovaleImageTableau.add(ovaleImage);
        ovaleImageTableau.add(ovaleImage);

        // création du tableau contenant les positions de chaque ovale
        ovalePositionTableau = new Array<Circle>();
        for(int i =1; i< 9; i++) {
            creerOvalesPositions();
        }

       text1Placed = false;
       text2Placed = false;
       text3Placed = false;
       text4Placed = false;
    }

    private void creerOvalesPositions() {
        Circle ovale = new com.badlogic.gdx.math.Circle();
        ovale.set(MathUtils.random(100, screenWidth - 100), MathUtils.random(100, screenHeight/2), 50);
        ovalePositionTableau.add(ovale);
      //  lastDropTime = TimeUtils.nanoTime();
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

        if (firstStart) {

            chenillePosition.x = chenillePosition.x + 150 * Gdx.graphics.getDeltaTime();
            if (chenillePosition.x> screenWidth / 2 - 600 / 2) {
                actionResolver.showLongToast("Reconstitue le nom de la fleur tenue par le personnage dans sa main droite.\n"+
                        "Fais glisser les syllabes sur le corps de la chenille !");
                firstStart=false;
            }
        }
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        batch.begin();
        // affichage et centrage de l'image du jeu (de dimension 960*1381)
        // les coordonnées sont le coin inférieur gauche
        batch.draw(fondJeu01,  origineX, origineY);
        batch.draw(chenilleImage, chenillePosition.x, chenillePosition.y);

        // on met à jour la position des ovales et de leurs textes
            int i=0;
            for(com.badlogic.gdx.math.Circle ovaleParcoursTableau: ovalePositionTableau) {
                // décalé de -50px pour centrer sur le cercle
                batch.draw(ovaleImageTableau.get(i), ovaleParcoursTableau.x-50 , ovaleParcoursTableau.y-50);
                // décalé pour centrer sur ovale
                ovaleTextesTableau.get(i).draw(batch, ovaleContenuTextesTableau.get(i), ovaleParcoursTableau.x-25, ovaleParcoursTableau.y+15);
                i++;
            }


        if (gagne == true)  {
            chenillePosition.x = chenillePosition.x - 150 * Gdx.graphics.getDeltaTime();
            ovalePositionTableau.get(0).x = ovalePositionTableau.get(0).x - 150 * Gdx.graphics.getDeltaTime();
            ovalePositionTableau.get(1).x = ovalePositionTableau.get(1).x - 150 * Gdx.graphics.getDeltaTime();
            ovalePositionTableau.get(2).x = ovalePositionTableau.get(2).x - 150 * Gdx.graphics.getDeltaTime();
            ovalePositionTableau.get(3).x = ovalePositionTableau.get(3).x - 150 * Gdx.graphics.getDeltaTime();
            if (chenillePosition.x<screenWidth / 2 - 600 / 2-600) {
                for (Texture ovale_image : ovaleImageTableau) {
                    ovale_image.dispose();
                }
                for (BitmapFont font : ovaleTextesTableau) {
                    font.dispose();
                }
                chenilleImage.dispose();
                batch.draw(bravoImage, origineX, origineY);
            }
        }
            batch.end();

        //    utile pour visualiser les cercles des animaux
        // le cercle créé par ShapeRenderer se positionne au milieu et au dessus de la coordonnée fournie
       /* ShapeRenderer shapeRenderer = new  ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.circle(screenWidth/2 - 238, screenHeight/2+165, 50 );
        shapeRenderer.circle(screenWidth/2 - 120, screenHeight/2+165, 50 );
        shapeRenderer.circle(screenWidth/2 - 10, screenHeight/2+165, 50 );
        shapeRenderer.circle(screenWidth/2 +100, screenHeight/2+165, 50 );
        shapeRenderer.end();*/
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

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        System.out.println("pan");
        touchPosX =x;
        // on décale touchPosY de 73 car ses coordonnées débutent à 0 au bas de l'écran du device alors que les coordonnées du batch débutent à 0 à la zone d'affichag du jeu,
        // cad au dessus de la barre d'action du bas, d'une hauteur de 73px environ
        touchPosY =y+73;

        // si c'est la première fois qu'on touche l'animal, on va garder en mémoire ses coordonnées
        if (firstTouchedSoInitTouchPos) {
            initTtouchPosX = x;
            // on décale touchPosY de 73 car ses coordonnées débutent à 0 au bas de l'écran du device alors que les coordonnées du batch débutent à 0 à la zone d'affichag du jeu,
            // cad au dessus de la barre d'action du bas, d'une hauteur de 73px environ
            initTouchPosY = y+73;
        }
        // puis on remets firstTouch à false pour ne pas réécrire la variable
        firstTouchedSoInitTouchPos = false;

        // tant qu'on a pas encore touché un animal et stoppé le parcours du tableau de position des animaux
        if (!stopParcoursTableau) {
            // parcours du tableau de position des ovales pour voir si l'un d'eux est touché
            for (int i=0; i<ovalePositionTableau.size; i++) {
                // si l'ovale contient les coordonnées x, il est touché
                if(ovalePositionTableau.get(i).contains(touchPosX, screenHeight - touchPosY)) {
                    // on met la variable à false pour ne pas reparcourir le tableau des position au prochain pan
                    stopParcoursTableau =true;
                    // on indique l'index de l'animal qui est touché
                    indexOvaleTouched = i;

                    // et on déplace l'ovale
                    ovalePositionTableau.get(indexOvaleTouched).setPosition(touchPosX, screenHeight - touchPosY);
                }
            }
        }
        // si on a déjà touché un ovale, pas besoin de reparcourir le tableau de positionnement
        // pour voir si l'un d'eux est touché
        else {
            // on change la couleur de l'ovale en vert
            ovaleImageTableau.set(indexOvaleTouched, ovaleVertImage);
            // puis on déplace l'ovale
            ovalePositionTableau.get(indexOvaleTouched).setPosition(touchPosX, screenHeight - touchPosY);
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        System.out.println("panStop" + x + " / " + y);

        // on mets firstTouch à true pour pouvoir réécrire la variable au prochain pan
        firstTouchedSoInitTouchPos =true;

        // on indique qu'on peut parcourir à nouveau le tableau des animaux pour savoir si l'un d'eux est touché
        stopParcoursTableau = false;

        // si l'ovale a été relaché dans le cadre 1 et si c'est le texte 1 (le gagnant)
        if (ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre1Position) && ovalePositionTableau.get(indexOvaleTouched).equals(ovalePositionTableau.get(0))) {
            System.out.println("overlapsed");
            text1Placed=true;
            ovalePositionTableau.get(indexOvaleTouched).setPosition(screenWidth/2 - 238, screenHeight/2+180+50);
        }
        // si l'ovale a été relaché dans le cadre 2 et si c'est le texte2 (le gagnant)
        else if (ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre2Position) && ovalePositionTableau.get(indexOvaleTouched).equals(ovalePositionTableau.get(1))) {
            System.out.println("overlapsed");
            text2Placed=true;
            ovalePositionTableau.get(indexOvaleTouched).setPosition(screenWidth/2 - 120, screenHeight/2+180+50);
        }
        // si l'ovale a été relaché dans le cadre 3 et si c'est le texte 3 (le gagnant)
        else if (ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre3Position) && ovalePositionTableau.get(indexOvaleTouched).equals(ovalePositionTableau.get(2))) {
            System.out.println("overlapsed");
            text3Placed=true;
            ovalePositionTableau.get(indexOvaleTouched).setPosition(screenWidth/2 - 10, screenHeight/2+180+50);
        }
        // si l'ovale a été relaché dans le cadre 4 et si c'est le texte 4 (le gagnant)
        else if (ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre4Position) && ovalePositionTableau.get(indexOvaleTouched).equals(ovalePositionTableau.get(3))) {
            System.out.println("overlapsed");
            text4Placed=true;
            ovalePositionTableau.get(indexOvaleTouched).setPosition(screenWidth/2 +100, screenHeight/2+180+50);
        }

        // si lovale a été relaché dans le cadre mais que ce n'est pas l'animal gagnant, il repart à son point d'origine
        else if (ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre1Position) || ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre2Position)
                || ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre3Position) || ovalePositionTableau.get(indexOvaleTouched).overlaps(fenetre4Position)) {
            // on remet l'ovale en blanc
            ovaleImageTableau.set(indexOvaleTouched, ovaleImage);
            ovalePositionTableau.get(indexOvaleTouched).setPosition(initTtouchPosX, screenHeight - initTouchPosY);
        }
        else {
            // on remet l'ovale en blanc
             ovaleImageTableau.set(indexOvaleTouched, ovaleImage);
        }

        if (text1Placed==true && text2Placed==true && text3Placed==true && text4Placed== true) {
            gagne = true;
        }


        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}