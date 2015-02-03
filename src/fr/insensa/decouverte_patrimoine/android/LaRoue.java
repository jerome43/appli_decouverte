package fr.insensa.decouverte_patrimoine.android;

        import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;


// extends MainActivity nécessaire pour récupérer variables globales de l'appli
public class LaRoue extends MainActivity implements ApplicationListener {

    // Textures
    private Texture roueImg;
    private Texture losangeImg;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle losangeRec;
    private Sprite sprite1;
    private Sprite sprite2;
    private boolean touche1;
    private boolean touche2;
    private int toursBoucle;
    private int compteurRotation;
    private int toursBoucle2;
    private int compteurRotation2;

    @Override
    public void create() {
        compteurRotation = 0;
        compteurRotation2 = 0;

        touche1 = false;
        touche2 = false;

        // Associer aux fichiers
        roueImg = new Texture(Gdx.files.internal("roue.png"));
        losangeImg = new Texture(Gdx.files.internal("losange.png"));
        batch = new SpriteBatch();
        sprite1 = new Sprite(roueImg);
        sprite2 = new Sprite(roueImg);

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);

        //  Associer les rectangles
        losangeRec = new Rectangle();
        losangeRec.width = 60;
        losangeRec.height = 60;
        losangeRec.x = 480 / 2 - 60 / 2;
        losangeRec.y = 800 / 2 - 60 / 2;
        sprite1.setPosition(90, 80);
        sprite2.setPosition(90,420);

    }
    @Override
    public void render() {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(losangeImg, losangeRec.x, losangeRec.y);
        sprite1.draw(batch);
        sprite2.draw(batch);
        if (touche1 == true || compteurRotation <toursBoucle) {
            sprite1.rotate(1);
            compteurRotation++;
            touche1 =true;
        }
        if (touche2 == true || compteurRotation2 <toursBoucle2) {
            sprite2.rotate(1);
            compteurRotation2++;
            touche2 =true;
        }
        touche1 =false;
        touche2 = false;

        batch.end();
        if (Gdx.input.isTouched()) {
            int x=  Gdx.input.getX();
            int y = Gdx.input.getY();
            if(sprite1.getBoundingRectangle().contains(x, 800 - y)) {
                touche1 = true;

                toursBoucle = MathUtils.random(500, 5000);

            }

        }
        if (Gdx.input.isTouched()) {
            int x2=  Gdx.input.getX();
            int y2 = Gdx.input.getY();
            if(sprite2.getBoundingRectangle().contains(x2, 800 - y2)) {
                touche2 = true;

                toursBoucle2 = MathUtils.random(500, 5000);
            }
        }
    }
    @Override
    public void dispose() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void pause() {

    }
    @Override
    public void resize(int width, int height) {

    }
}


