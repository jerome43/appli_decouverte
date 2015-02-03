package fr.insensa.decouverte_patrimoine.android;


import android.util.Log;

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

public class Jeu04 extends MainActivity implements ApplicationListener, GestureDetector.GestureListener {

    ActionResolver actionResolver;
    Jeu04(ActionResolver actionResolver)    {
        this.actionResolver = actionResolver;
    }

    private Texture mouche;
 //   private Texture oeil;
    private Texture fondJeu04;
    private SpriteBatch batch;

    private OrthographicCamera camera;

    private Circle grenouille1;
    private Circle grenouille2;
    private Circle grenouille3;
    private Circle grenouille5;
    private Circle grenouille10;
    private Circle mouchePosition;

    private float vitesseX;
    private float vitesseY;
    private int compteur;

  //  private boolean goodGrenouille;
    private boolean moucheTouche;
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


    // pour voir si c'est la première fois que l'appli est lançée
    private boolean firstStart;
 //   private ShapeRenderer shapeRenderer;


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

        // on indique que c'est la première fois que l'appli est lançée
        firstStart = true;

        // on a pas la bonne grenouille;
     //   goodGrenouille =false;
        moucheTouche=false;
        compteur=0;

        // récupérartion du numéro de parcours défini dans la classe MainActivity
        numero = MainActivity.getNumero_parcours_main();
        // associer les textures aux images

        String path = "Android/data/fr.insensa.decouverte_patrimoine.android/files/" + numero + "/";
        fondJeu04 = new Texture(Gdx.files.internal("jeu04.png"));
        mouche = new Texture(Gdx.files.internal("mouche.png"));
     //   oeil = new Texture(Gdx.files.internal("oeil.png"));

    //    BitmapFont texteFont = new BitmapFont(Gdx.files.internal("calibri.fnt"));

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
        actionResolver.showLongToast(screenWidth + " / " + screenHeight);
        System.out.println( screenWidth + " / " + screenHeight);

        batch = new SpriteBatch();

        // position des bouches de grenouille
        // les coordonnées sont le centre du cercle
        grenouille1 = new Circle(origineX + 250, origineY + 1035, 70);
        grenouille2 = new Circle(origineX + 675, origineY + 1035, 70);
        grenouille3 = new Circle(origineX+475, origineY+810, 70);
        grenouille5 = new Circle(origineX+250, origineY + 580, 70);
        grenouille10 = new Circle(origineX + 690, origineY + 580, 70);
        mouchePosition=new Circle(screenWidth/2, origineY+350, 50);
    //    shapeRenderer = new  ShapeRenderer();
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
                actionResolver.showLongToast("Envoie la mouche dans la bonne case");
                firstStart=false;
        }
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(fondJeu04, origineX, origineY);


        if (moucheTouche) {
            if (compteur<400) {
                mouchePosition.x += (vitesseX/10+compteur/10) * Gdx.graphics.getDeltaTime();
                mouchePosition.y -= (vitesseY/10+compteur/5) * Gdx.graphics.getDeltaTime();
                compteur++;
                if (mouchePosition.overlaps(grenouille1) || mouchePosition.overlaps(grenouille2) || mouchePosition.overlaps(grenouille3)
                        || mouchePosition.overlaps(grenouille5) || mouchePosition.overlaps(grenouille10)) {
                    moucheTouche = false;
                    gagne = true;
                }
            }
            else {
                moucheTouche=false;
                mouchePosition.setPosition(screenWidth/2, origineY+350);
            }

        }

        else if (gagne == true)  {
            actionResolver.showLongToast("Bravo, bien tiré");
            gagne=false;
            mouchePosition.setPosition(screenWidth/2, origineY+350);
        }
        else {
             // affichage et centrage de l'image du jeu (de dimension 960*1381)
             // les coordonnées sont le coin inférieur gauche


         }
        // décalage de -50 pour centrer l'image sur le centre du cercle de rayon 50
            batch.draw(mouche, mouchePosition.x-50, mouchePosition.y-50);
            batch.end();
      /*  shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.circle( origineX + 250, origineY + 1035, 70);
        shapeRenderer.circle(origineX + 675, origineY + 1035, 70);
        shapeRenderer.circle( origineX+475, origineY+810, 70);
        shapeRenderer.circle(origineX+250, origineY + 580, 70);
        shapeRenderer.circle(origineX + 690, origineY + 580, 70);
        shapeRenderer.circle(screenWidth/2, origineY +350, 50);
        shapeRenderer.circle(mouchePosition.x, mouchePosition.y, 50);
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
        Log.i("tap : ", x + " / " + y);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    // pour jeter un élément
    public boolean fling(float velocityX, float velocityY, int button) {
        System.out.println("fling"+velocityX + " / " + velocityY);
        vitesseX=velocityX;
        vitesseY=velocityY;
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        System.out.println("pan");
        touchPosX =x;
        // on décale touchPosY de 73 car ses coordonnées débutent à 0 au bas de l'écran du device alors que les coordonnées du batch débutent à 0 à la zone d'affichag du jeu,
        // cad au dessus de la barre d'action du bas, d'une hauteur de 73px environ
        touchPosY =y+73;
        if (mouchePosition.contains(touchPosX, screenHeight-touchPosY)) {
            System.out.println("mouche touchée");
            moucheTouche=true;
            compteur=0;
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        System.out.println("panStop" + x + " / " + y);



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