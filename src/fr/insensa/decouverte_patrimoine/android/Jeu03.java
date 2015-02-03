package fr.insensa.decouverte_patrimoine.android;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;


// extends MainActivity nécessaire pour récupérer variables globales de l'appli

public class Jeu03 extends MainActivity implements ApplicationListener, GestureDetector.GestureListener {

    ActionResolver actionResolver;
    Jeu03(ActionResolver actionResolver)    {
        this.actionResolver = actionResolver;
    }

    private Texture pinceauGris;
    private Texture pinceauBleu;
    private Texture pinceauRouge;
    private Texture pinceauVert;
    private Texture pinceauOrange;
    private Texture pinceauJaune;
    private Texture pinceauBlanc;
    private Texture pinceauAffiche;
    private Texture fondJeu03blanc;
    private Texture fondJeu03bleu;
    private SpriteBatch batch;

    private OrthographicCamera camera;

    private Circle salopettePosition;
    private Circle pinceauPosition;
    private Circle potRougePosition;
    private Circle potBleuPosition;
    private Circle potVertPosition;
    private Circle potBlancPosition;
    private Circle potOrangePosition;
    private Circle potJaunePosition;


    private boolean pinceauTrempe;
    private boolean goodColor;
    private boolean gagne;
    private boolean firstWin;
    private int screenWidth;
    private int screenHeight;
    private int origineX;
    private int origineY;

    // Le numéro de parcours en cours
    private String numero;

   // ShapeRenderer shapeRenderer;

    // les positions du toucher utilisateur à l'écran
    private float touchPosX;
    private float touchPosY;
    // position initiale du touché utilisateur
   // private float initTtouchPosX;
   // private float initTouchPosY;

    // pour voir si c'est la première fois que l'appli est lançée
    private boolean firstStart;


    @Override
    public void create() {
     //   shapeRenderer = new  ShapeRenderer();

        // pour initialiser la détection des touchers écran
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);

        // mise à zéro des position utilisateur
        touchPosX =-1;
        touchPosY =-1;

        // personne n'a encore gagné
        gagne = false;
        firstWin=true;

        // on indique que c'est la première fois que l'appli est lançée
        firstStart = true;
        // le pinceau n'a jamais été trempé
        pinceauTrempe = false;
        // on a pas la bonne couleur;
        goodColor=false;

        // récupérartion du numéro de parcours défini dans la classe MainActivity
        numero = MainActivity.getNumero_parcours_main();
        // associer les textures aux images

        String path = "Android/data/fr.insensa.decouverte_patrimoine.android/files/" + numero + "/";
        fondJeu03blanc = new Texture(Gdx.files.internal("jeu03blanc.png"));
        fondJeu03bleu = new Texture(Gdx.files.internal("jeu03bleu.png"));
        pinceauBlanc = new Texture(Gdx.files.internal("pinceau_blanc.png"));
        pinceauBleu = new Texture(Gdx.files.internal("pinceau_bleu.png"));
        pinceauGris = new Texture(Gdx.files.internal("pinceau_gris.png"));
        pinceauVert = new Texture(Gdx.files.internal("pinceau_vert.png"));
        pinceauRouge = new Texture(Gdx.files.internal("pinceau_rouge.png"));
        pinceauOrange = new Texture(Gdx.files.internal("pinceau_orange.png"));
        pinceauJaune = new Texture(Gdx.files.internal("pinceau_jaune.png"));
        pinceauAffiche =pinceauGris;

       // BitmapFont texteFont = new BitmapFont(Gdx.files.internal("calibri.fnt"));

        camera = new OrthographicCamera();

        // camera
        // Sets this camera to an orthographic projection using a viewport fitting the screen resolution,
        // centered at (Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2), with the y-axis pointing up or down (boolean yDown)
        camera.setToOrtho(false);

        // récupération de la hauteur et de la largeur de la zone d'affichage en fonction du device
        // cad taille écran moins les barres d'actions du haut et du bas
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        // L'origine de l'image de fond (coin inférieur gauche de l'image et non de son affichage à l'écran) à partir duquel on positionne nos objets
        origineX= screenWidth/2 - 960/2;
        origineY= screenHeight/2-1380/2;

        System.out.println( screenWidth + " / " + screenHeight);

        batch = new SpriteBatch();

        // position de la salopette
        // les coordonnées sont le coin inférieur gauche
        salopettePosition = new Circle(origineX + 460, origineY + 800, 130);

        // position des pots
        // les coordonnées sont le centre du cercle
        // les cercles doivent être réglés aux mêmes dimensions que les images qu'ils contiennent si l'on veut gérer les intersections adaptées à ce que voit l'utilisateur
        // la taille du rectangle ou du cercle n'a rien à voir avec la taille de l'image car il ne la contient pas, on leur affecte juste les mêmes coordonnées géographiques dans le batch

        potRougePosition = new Circle(origineX+265, origineY+480, 35);
        potOrangePosition = new Circle(origineX+415, origineY+480, 35);
        potJaunePosition = new Circle(origineX+560, origineY+480, 35);
        potBleuPosition = new Circle(origineX+480, origineY + 400, 35);
        potVertPosition = new Circle(origineX + 340, origineY + 400, 35);
        potBlancPosition = new Circle(origineX+630, origineY+400, 35);

        // position du pinceau
        pinceauPosition = new Circle(origineX + 200, origineY + 200, 35);
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
                actionResolver.showLongToast("Retrouve la couleur de la robe de la statue.\n" +
                        "Trempe ton pinceau dans le pot de la couleur correspondante puis va peindre la salopette de l'Inspecteur Rando");
                firstStart=false;
        }
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
         if (gagne == true)  {
             batch.draw(fondJeu03bleu, origineX, origineY);
             batch.draw(pinceauAffiche, origineY+200, origineY+200);
             if (firstWin) {
                 actionResolver.showLongToast("Bravo, c'était la bonne couleur.\n");
             }
             firstWin=false;
        }
        else {
             // affichage et centrage de l'image du jeu (de dimension 960*1381)
             // les coordonnées sont le coin inférieur gauche
             batch.draw(fondJeu03blanc, origineX, origineY);
             batch.draw(pinceauAffiche, pinceauPosition.x-50, pinceauPosition.y-50);
         }

        batch.end();
   /* utile pour visualiser les rectangles des animaux

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
       shapeRenderer.circle(origineX+265, origineY+480, 35);
        shapeRenderer.circle(origineX+415, origineY+480, 35);
        shapeRenderer.circle(origineX+560, origineY+480, 35);
        shapeRenderer.circle(origineX+480, origineY + 400, 35);
        shapeRenderer.circle(origineX + 340, origineY + 400, 35);
        shapeRenderer.circle(origineX+630, origineY+400, 35);
        shapeRenderer.circle(salopettePosition.x, salopettePosition.y, 130);
        shapeRenderer.circle(pinceauPosition.x, pinceauPosition.y, 50);
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
        pinceauPosition.setPosition(touchPosX, screenHeight-touchPosY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        System.out.println("panStop" + x + " / " + y);
        if (pinceauPosition.overlaps(potBleuPosition)) {
            pinceauTrempe=true;
            pinceauAffiche=pinceauBleu;
            goodColor=true;
        }
        else if(pinceauPosition.overlaps(potRougePosition)) {
            pinceauTrempe=true;
            pinceauAffiche=pinceauRouge;
            goodColor=false;
        }
        else if(pinceauPosition.overlaps(potOrangePosition)) {
            pinceauTrempe=true;
            pinceauAffiche=pinceauOrange;
            goodColor=false;
        }
        else if(pinceauPosition.overlaps(potVertPosition)) {
            pinceauTrempe=true;
            pinceauAffiche= pinceauVert;
            goodColor=false;
        }
        else if(pinceauPosition.overlaps(potBlancPosition)) {
            pinceauTrempe=true;
            pinceauAffiche= pinceauBlanc;
            goodColor=false;
        }
        else if(pinceauPosition.overlaps(potJaunePosition)) {
            pinceauTrempe=true;
            pinceauAffiche= pinceauJaune;
            goodColor=false;
        }
        else if (pinceauPosition.overlaps(salopettePosition) && pinceauTrempe==true && goodColor==true) {
            gagne=true;
        }
        else if (pinceauPosition.overlaps(salopettePosition) && pinceauTrempe==true && goodColor==false) {
            actionResolver.showLongToast("Non, ce n'est pas la bonne couleur.\n"+
                    "essaye avec une autre couleur");
        }
        else if (pinceauPosition.overlaps(salopettePosition) && pinceauTrempe==false) {
            actionResolver.showLongToast("Trempe d'abord ton pinceau dans un pot de peinture.\n");
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