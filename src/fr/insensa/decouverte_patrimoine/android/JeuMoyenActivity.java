package fr.insensa.decouverte_patrimoine.android;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;

public class JeuMoyenActivity extends AndroidApplication {

    // la map
    protected static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    // the map Fragment
    protected com.google.android.gms.maps.MapFragment mMapFragment;
    // les options par défaut de création de la map
    protected GoogleMapOptions options = new GoogleMapOptions();

    // Le numéro de parcours en cours
    private String numero;
    // permet de lancer des actions UI Android depuis Libgdx
    private ActionResolverAndroid actionResolverAndroid;
    // les différents jeux à lancer en fonction du numéro de parcours sélectionné
    private Jeu04 game1;
    private PlaneGame game2;
    private DropGame game3;
    private View gameView;
    private  RelativeLayout jeuLayout;

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
            //cfg.useGL20 = false;

            // récupérartion du numéro de parcours défini dans la classe MainActivity
            numero = MainActivity.getNumero_parcours_main();
            // actionResolver qui permettra de lancer depuis LibGdx des éléments natifs android (comme des Toast...)
            //This way sending Toast message, opening Alert Box, presenting a ListView and raising an Intent worked from renderThread to uiThread.
            // All of these are implemented using Android native Java methods.
            actionResolverAndroid = new ActionResolverAndroid(this);
            // désérialisation de la vue contenante principale
            setContentView(R.layout.jeu_layout);
            // récupération du layout
            jeuLayout = (RelativeLayout) findViewById(R.id.jeu_layout);

            // initialize(new PlaneGame(), cfg);
       //     initialize(new PetitTrain(), cfg);

            // en fonction du numéro de parcours choisi, ajout du jeu libgdx dans le layout
            switch (numero) {
                case "parcours1" :
                   // initialize(new Jeu01(actionResolverAndroid), cfg);
                    game1=new Jeu04(actionResolverAndroid);
                    gameView=initializeForView(game1,cfg);
                    // ajout de la vue du jeu au layout contenant
                    jeuLayout.addView(gameView);
                    break;
                case "parcours2" :
                  //  initialize(new Jeu02(actionResolverAndroid), cfg);
                    game2=new PlaneGame(actionResolverAndroid);
                    gameView=initializeForView(game2,cfg);
                    jeuLayout.addView(gameView);
                    break;
                case "parcours3" :
                   // initialize(new Jeu03(actionResolverAndroid), cfg);
                    game3=new DropGame(actionResolverAndroid);
                    gameView=initializeForView(game3,cfg);
                    jeuLayout.addView(gameView);
                    break;
                case "parcours4" :
                   // initialize(new Jeu04(actionResolverAndroid), cfg);
                    break;
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // définition du menu à utiliser en action bar pour les jeux
        getMenuInflater().inflate(R.menu.action_bar_jeu_activiy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // actions à réaliser en fonction des click sur actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // rafraichir les consignes de jeu
        if (item.getItemId() == R.id.refresh_jeu_activity) {
            switch (numero) {
                case "parcours1" :
                    Toast.makeText(this, "Envoie la mouche dans la bonne case", Toast.LENGTH_LONG).show();
                    break;
                case "parcours2" :
                    Toast.makeText(this, "Trouves le bon animal et replace le au centre du panneau", Toast.LENGTH_LONG).show();
                    break;
                case "parcours3" :
                    Toast.makeText(this, "Retrouve la couleur de la robe de la statue.\n" +
                            "Trempe ton pinceau dans le pot de la couleur correspondante puis va peindre la salopette de l'Inspecteur Rando", Toast.LENGTH_LONG).show();
                    break;
                case "parcours4" :
                    Toast.makeText(this, "A toi de jouer", Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        }

        // access à Map
        else if (item.getItemId() == R.id.retour_circuit_activity) {
            // verification préalable de la disponiblité du réseau
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                Gdx.app.exit();
                Intent retourCircuit = new Intent(getApplicationContext(), CircuitActivity.class);
                startActivity(retourCircuit);
                return true;
        }

        // accès aux jeux
        else if (item.getItemId() == R.id.jouer_jeu_activity) {
            // penser à mettre dans le manifest les nouvelles activités
            Intent startGame = new Intent(getApplicationContext(), JeuMoyenActivity.class);
            startActivity(startGame);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
