package fr.insensa.decouverte_patrimoine.android;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;


// extends MainActivity nécessaire pour récupérer variables globales de l'appli
public class AttrapeNuage extends MainActivity implements ApplicationListener {

    // Textures
    private Texture nuageImg1;
    private Texture nuageImg2;
    private Texture nuageImg3;
    private Texture nuageOrageImg;
    private Texture soleilImg;
    private Texture filetImg;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private com.badlogic.gdx.math.Rectangle nuageRec1;
    private com.badlogic.gdx.math.Rectangle nuageRec2;
    private com.badlogic.gdx.math.Rectangle nuageRec3;
    private com.badlogic.gdx.math.Rectangle nuageOrageRec;
    private com.badlogic.gdx.math.Rectangle soleilRec;
    private com.badlogic.gdx.math.Rectangle filetRec;
    private Array<com.badlogic.gdx.math.Rectangle> tableauRec;
    private Array<Texture> tableauImg;
    private  boolean initialisation;
    private Array<Integer> tableauRandX;
    private Array<Integer> tableauRandY;
    private boolean gagne;


    @Override
    public void create() {

        gagne = false;
        // sert juste à lancer une fois le placement des images à attraper
        initialisation=false;

        // Association aux images correspondantes
        nuageImg1 = new Texture(Gdx.files.internal("nuage1.png"));
        nuageImg2 = new Texture(Gdx.files.internal("nuage2.png"));
        nuageImg3 = new Texture(Gdx.files.internal("nuage3.png"));
        nuageOrageImg = new Texture(Gdx.files.internal("nuageOrage.png"));
        soleilImg = new Texture(Gdx.files.internal("soleil.png"));
        filetImg = new Texture(Gdx.files.internal("filet.png"));
        batch = new SpriteBatch();

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);

        // Rectangles des images
        filetRec = new com.badlogic.gdx.math.Rectangle();
        nuageRec1 = new com.badlogic.gdx.math.Rectangle();
        nuageRec2 = new com.badlogic.gdx.math.Rectangle();
        nuageRec3 = new com.badlogic.gdx.math.Rectangle();
        nuageOrageRec = new com.badlogic.gdx.math.Rectangle();
        soleilRec = new com.badlogic.gdx.math.Rectangle();
        nuageRec1.width = 152;
        nuageRec2.width = 152;
        nuageRec3.width = 152;
        nuageOrageRec.width = 152;
        soleilRec.width = 152;
        filetRec.width = 141;
        nuageRec1.height = 96;
        nuageRec2.height = 96;
        nuageRec3.height = 96;
        nuageOrageRec.height = 96;
        soleilRec.height = 96;
        filetRec.height = 170;
        filetRec.x = 0;
        filetRec.y = 0;

        // Generer position images
        tableauRandX = new Array<Integer>();
        tableauRandY = new Array<Integer>();
        tableauRec = new Array<com.badlogic.gdx.math.Rectangle>();
        tableauImg = new Array<Texture>();
        spawnImg();

        for(int i =0; i< 5; i++) {
            spawnRec();
            randNumberImg();
        }

    }


    private void randNumberImg() {
        Integer x = MathUtils.random(-200, 200 );
        tableauRandX.add(x);
        Integer y = MathUtils.random(-200, 200);
        tableauRandY.add(y);


    }

    private void randNumberImgAfterCollison(int i) {
        Integer x = MathUtils.random(-200, 200 );
        tableauRandX.insert(i, x);
        Integer y = MathUtils.random(-200, 200);
        tableauRandY.insert(i, y);


    }

    private void spawnRec() {
        com.badlogic.gdx.math.Rectangle image = new com.badlogic.gdx.math.Rectangle();
        image.setCenter(MathUtils.random(0, 480 - 152), MathUtils.random(0, 800 - 96));
        tableauRec.add(image);
    }

    private void spawnImg() {
        tableauImg.add(nuageImg1);
        tableauImg.add(nuageImg2);
        tableauImg.add(nuageImg3);
        tableauImg.add(nuageOrageImg);
        tableauImg.add(soleilImg);
    }
    @Override
    public void render() {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        if (!initialisation)  {
            int i=0;
            for(com.badlogic.gdx.math.Rectangle imageParcoursTableau: tableauRec) {

                batch.draw(tableauImg.get(i), imageParcoursTableau.x, imageParcoursTableau.y);
                i++;
            }
            initialisation=true;
        }


        int i=0;
        for(com.badlogic.gdx.math.Rectangle imageParcoursTableau: tableauRec) {

            batch.draw(tableauImg.get(i), imageParcoursTableau.x = imageParcoursTableau.x - tableauRandX.get(i) * Gdx.graphics.getDeltaTime(),
                    imageParcoursTableau.y = imageParcoursTableau.y - tableauRandY.get(i) * Gdx.graphics.getDeltaTime());

            // Etre sur que les images restent a l'interieur de l'ecran
            if(imageParcoursTableau.x < 0) {
                randNumberImgAfterCollison(i);
                imageParcoursTableau.x = 0;    }
            if(imageParcoursTableau.x > 480 - 152) {
                randNumberImgAfterCollison(i);
                imageParcoursTableau.x = 480 - 152;   }
            if(imageParcoursTableau.y < 0)  {
                randNumberImgAfterCollison(i);
                imageParcoursTableau.y = 0;}
            if(imageParcoursTableau.y > 800 - 96)   {
                randNumberImgAfterCollison(i);
                imageParcoursTableau.y = 800 - 96; }

            //toucher de l'utilisateur



            for (int ii = 0; ii < tableauRec.size; ii++) {
                if (!gagne) {

                    if(Gdx.input.isTouched()) {
                        int x=  Gdx.input.getX();
                        int y = Gdx.input.getY();
                        if(filetRec.contains(x ,800 - y)) {
                            filetRec.setCenter(x, 800 - y);
                            if(filetRec.contains(soleilRec.getX(), soleilRec.getY())) {
                                tableauRec.removeIndex(4);
                                gagne = true;
                            }
                        }
                    }
                    else {
                        if (imageParcoursTableau.overlaps(tableauRec.get(ii))) {
                            randNumberImgAfterCollison(i);
                            randNumberImgAfterCollison(ii);
                        }
                    }
                }
            }

              /*
            if (imageParcoursTableau.overlaps(tableauRec.get(1))) {
                randNumberImgAfterCollison(i);
                randNumberImgAfterCollison(1);
            }
            if (imageParcoursTableau.overlaps(tableauRec.get(2))) {
                randNumberImgAfterCollison(i);
                randNumberImgAfterCollison(2);
            }
            if (imageParcoursTableau.overlaps(tableauRec.get(3))) {
                randNumberImgAfterCollison(i);
                randNumberImgAfterCollison(3);
            }
            if (imageParcoursTableau.overlaps(tableauRec.get(4))) {
                randNumberImgAfterCollison(i);
                randNumberImgAfterCollison(4);
            }      */

            batch.draw(filetImg, filetRec.x, filetRec.y);

            i++;
        }

        //  (MathUtils.random(0, 480 - 152)
        //  (MathUtils.random(0, 800 - 96)

        batch.end();

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