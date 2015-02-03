package fr.insensa.decouverte_patrimoine.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jérôme on 13/01/2015.
 */
public class CircuitActivity extends Activity implements TextesCircuitsFragment.TextCircuitFragmentCallBack {

    // la map
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    // the map Fragment
    private MapFragment mMapFragment;
    // les options par défaut de création de la map
    private GoogleMapOptions options = new GoogleMapOptions();
    // les tableaux contenant la localisation des étapes
    private ArrayList<String> localisationX;
    private ArrayList<String> localisationY;
    private ArrayList<LatLng> etapesArray;

    // le layout de la vue principale de l'activité
    private LinearLayout contentViewLayout;

    // le numéro de parcours en cours d'utilisation
    private String numero_parcours;

    // les différents textes à mettre à jour
    private String textIntroText;

    // le tableau stockant les textes des différents arrêts
    private static ArrayList<String> texteArretArray;

    // le tableau stockant les consignes de jeu des différents arrêts
    private ArrayList<String> consigneJeuArray;

    // le layout inflater
    private LayoutInflater inflater;

    // pour savoir si la map est en cours d'affichage
    private Boolean mMapFragmentDisplayed;

    // le path où sont stockés les fichiers du circuit en question
    private static String pathFile;

    // le texte principal de l'étape en cours
    private TextView textEtapeView;

    // l'image principale de l'étape en cours
    private ImageView imageEtapeView;

    // l'étape en cours
    private static int etape;
    // l'étape en cours, utilisé par onSaveInstanceStare
    // le tableau dezs étapes, selon leur statut, réalisées, en cours, pas réalisées
    private ArrayList<String> etapesArrayIsFinished;
    private static String _ETAPE;

    // le fragment des textes relatifs au circuit
    private TextesCircuitsFragment textesCircuitsFragment;

    // pour récupérer le menu
    private Menu myMenu;


    // pour géolocalisation
    private MapGestion mapGestion;
    private String serviceString = Context.LOCATION_SERVICE;
    private LocationManager locationManager;
    // la liste de tous les fournisseurs de localisation
    private List<String> providers ;
    // le fournisseur de localisation trouvé en fonction des critères
    private String bestLocationProvider;
    // la localisation de l'utilisateur
    private static Location location;
    // Actualisation de la position
    private int tempsActualisationLocation; // temps entre deux actualisation, en milliseconde
    private int distanceActualisationLocation; // distance requise pour une actualisation, en m
    // l'écouteur de localisation qui permet la mise à jour
    private LocationListener locationListener = new LocationListener() {

        @Override
        // exécute le code suivant à chaque mise à jour de la localisation
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // à faire si l'état du matériel change
        }

        @Override
        public void onProviderEnabled(String provider) {
            // à faire si le fournisseur est activé
        }

        @Override
        public void onProviderDisabled(String provider) {
            // à faire si le fournisseur est désactivé
        }
    };




    // ************ CYCLE DE VIE DE L'ACTIVITE PRINCIPALE**************************************************************


    // Dans onCreate, on instancie les objets ;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info", "onCreate CircuitActivity");

        // récupération du numéro de parcours
        this.numero_parcours = MainActivity.getNumero_parcours_main();

        // l'ArrayList qui contiendra les textes descriptifs de chaque étape
        texteArretArray = new ArrayList<>();
        // l'ArrayList qui contiendra les consignes de jeu de chaque étape
        consigneJeuArray = new ArrayList<>();


        // accès au gestionnaire de localisation
        locationManager = (LocationManager) getSystemService(serviceString);

        // trouver la liste des fournisseurs disponibles
        final boolean enableOnly = true;
        providers = locationManager.getProviders(enableOnly);

        // définition de critères pour récupérer du plus au moins précis
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);

        // récupération du meilleur provider
        bestLocationProvider = locationManager.getBestProvider(criteria, false);
     //   bestLocationProvider = locationManager.GPS_PROVIDER;

        // récupération de la dernière position connue
        location = locationManager.getLastKnownLocation(bestLocationProvider);
        //Log.i("info", location.toString());

        // définition des variables d'actualisation du fournisseur de Localisation
        tempsActualisationLocation = 5000; // temps entre deux actualisation, en milliseconde
        distanceActualisationLocation = 5; // distance requise pour une actualisation, en m


        // les ArrayList qui contiendront les Localisation
        localisationX = new ArrayList<>();
        localisationY = new ArrayList<>();
        etapesArray = new ArrayList<>();
        etapesArrayIsFinished = new ArrayList<>();

        // récupétation du path des fichiers du circuit
        pathFile = Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/" + getPackageName() + "/files/" + numero_parcours + "/";

        // ----------------- Récupération des données textuelles propres au parcours
        // acces à la BD avec l'objet ParcoursDataBase
        ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
        // récupération des valeurs de la base souhaitées définies dans ParcoursDataBase pour le parcours en question
        System.out.println(numero_parcours);
        parcoursDataBase.getOneCursorValues(numero_parcours);
        // pour tester si la BD n'est pas vide
        int countCursorLine = parcoursDataBase.getCountCursorLine();
        if (countCursorLine != 0) {
            System.out.println("entree dans boucle");
            // récupération des données à partir du cursor
            for (int i = 0; i < countCursorLine; i++) {
                texteArretArray.add(0, null);
                System.out.println("texteArret1" + parcoursDataBase.getOneTexteArret1());
                texteArretArray.add(1, parcoursDataBase.getOneTexteArret1());
                texteArretArray.add(2, parcoursDataBase.getOneTexteArret2());
                texteArretArray.add(3, parcoursDataBase.getOneTexteArret3());
                texteArretArray.add(4, parcoursDataBase.getOneTexteArret4());
                consigneJeuArray.add(0, null);
                consigneJeuArray.add(1, parcoursDataBase.getOneConsigneArret1());
                consigneJeuArray.add(2, parcoursDataBase.getOneConsigneArret2());
                consigneJeuArray.add(3, parcoursDataBase.getOneConsigneArret3());
                consigneJeuArray.add(4, parcoursDataBase.getOneConsigneArret4());
                etapesArrayIsFinished.add(0, null);
                etapesArrayIsFinished.add(1, "false");
                etapesArrayIsFinished.add(2, "false");
                etapesArrayIsFinished.add(3, "false");
                etapesArrayIsFinished.add(4, "false");
                localisationX.add(0, null);
                localisationX.add(1, "45.106164");
                localisationX.add(2, "45.109798");
                localisationX.add(3, "45.107254");
                localisationX.add(4, "45.104043");
                localisationY.add(0, null);
                localisationY.add(1, "4.237625");
                localisationY.add(2, "4.241895");
                localisationY.add(3, "4.245264");
                localisationY.add(4, "4.239770");
                etapesArray.add(0, null);
                for (int index=1; index<5; index++) {
                    LatLng latLng = new LatLng(Location.convert(localisationX.get(index)), Location.convert(localisationY.get(index)));
                    etapesArray.add(index, latLng);
                }
            }
        } else {
            System.out.println("countCursorLine=0");
        }
        // fermeture de la connexion à la BD
        parcoursDataBase.closeDatabase();

        // désérialisation du layout de la vue principale de l'activité
        setContentView(R.layout.circuit_layout);

        // chargement du fragment contenant les textes et photos
        displayTextFragment();

        // récupération de la vue conteneur
        contentViewLayout = (LinearLayout) findViewById(R.id.circuit_layout);

        // Create Layout Inflator
        inflater = LayoutInflater.from(getApplicationContext());

        // par défaut la map n'est pas affichée
        mMapFragmentDisplayed = false;

        // l'étape en cours, on commence à la première
        if (savedInstanceState != null) {
            Log.i("savedInstanceState", "not null");
            etape = savedInstanceState.getInt(_ETAPE);
        } else {
            Log.i("savedInstanceState", "null");
            // par défaut on commence à la première étape
            etape = 1;
            // on indique au tableau etapesArrayIsFinished quel est l'étape en cours (pour affichage du marker adéquat dans GoogleMap
            etapesArrayIsFinished.set(etape, "enCours");
        }

    }

    // appelée après OnStart lorsque l'activité a été détruite
    // cela se produit notamment quand changement d'orientation de l'écran
    // // récupère les infos de nSaveInstanceState
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("info", "onRestoreInstanceState Main Activity");
        etape = savedInstanceState.getInt(_ETAPE);
    }

    // si on veut sauvegarder des valeurs précises à restaurer après arrêts, mise en arrière plan...
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(_ETAPE, etape);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //   System.out.println(texteArretArray.get(1));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // définition du menu à utiliser en action bar pour les jeux
        getMenuInflater().inflate(R.menu.action_bar_circuit_activity, menu);
        myMenu = menu;
        myMenu.findItem(R.id.accueil_circuits_activity).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // actions à réaliser en fonction des click sur actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // access à Map
        if (item.getItemId() == R.id.carte_circuit_activity) {
            // verification préalable de la disponiblité du réseau
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo !=null && networkInfo.isConnected()) {
                myMenu.findItem(R.id.carte_circuit_activity).setVisible(false);
                myMenu.findItem(R.id.accueil_circuits_activity).setVisible(true);
                displayMapFragmentCircuitJeuActivity();
                return true;
            }
            else
                Toast.makeText(this,"Aucun réseau disponible, veuillez activez le wifi ou la connexion de données", Toast.LENGTH_LONG).show();
            return true;
        }

        else if (item.getItemId() == R.id.accueil_circuits_activity) {
             if (mMapFragmentDisplayed) {
                 // on arrête l'actualisation de la position
                 if (locationManager!=null && locationListener!=null) {
                     locationManager.removeUpdates(locationListener);
                 }
                 // on enlève le fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.remove(mMapFragment);
                fragmentTransaction.commit();
                FragmentManager fragmentManager = getFragmentManager();
                // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
                fragmentManager.executePendingTransactions();
                 myMenu.findItem(R.id.carte_circuit_activity).setVisible(true);
                 myMenu.findItem(R.id.accueil_circuits_activity).setVisible(false);
                 mMapFragmentDisplayed= false;
                 displayTextFragment();
            }

            return true;
        }
        else if (item.getItemId() == R.id.next_etape) {

            if (mMapFragmentDisplayed) {
                // on arrête l'actualisation de la position
                if (locationManager!=null && locationListener!=null) {
                    locationManager.removeUpdates(locationListener);
                }
                // on enlève le fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.remove(mMapFragment);
                fragmentTransaction.commit();
                FragmentManager fragmentManager = getFragmentManager();
                // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
                fragmentManager.executePendingTransactions();
                myMenu.findItem(R.id.carte_circuit_activity).setVisible(true);
                myMenu.findItem(R.id.accueil_circuits_activity).setVisible(false);
                mMapFragmentDisplayed= false;
                displayTextFragment();
            }

            if (etape<texteArretArray.size()-1) {
                // on indique au tableau etapesArrayIsFinished que l'étape a été terminée, (pour affichage du marker adéquat dans GoogleMap)
                etapesArrayIsFinished.set(etape, "true");
                etape ++;
                // on indique au tableau etapesArrayIsFinished quel est l'étape en cours (pour affichage du marker adéquat dans GoogleMap
                etapesArrayIsFinished.set(etape, "enCours");
                if (textEtapeView!=null) {
                    textEtapeView.setText(texteArretArray.get(etape));
                }
                if (imageEtapeView!=null) {
                    imageEtapeView.setImageURI(Uri.parse(pathFile + "image_etape_" + etape + ".png"));
                }
            }
           else {
                Toast.makeText(getApplicationContext(), "Vous êtes déjà à la dernière étape", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if (item.getItemId() == R.id.previous_etape) {
            if (mMapFragmentDisplayed) {
                // on arrête l'actualisation de la position
                if (locationManager!=null && locationListener!=null) {
                    locationManager.removeUpdates(locationListener);
                }
                // on enlève le fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.remove(mMapFragment);
                fragmentTransaction.commit();
                FragmentManager fragmentManager = getFragmentManager();
                // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
                fragmentManager.executePendingTransactions();
                myMenu.findItem(R.id.carte_circuit_activity).setVisible(true);
                myMenu.findItem(R.id.accueil_circuits_activity).setVisible(false);
                mMapFragmentDisplayed= false;
                displayTextFragment();
            }
            if (etape>1) {
                // on indique au tableau etapesArrayIsFinished que l'étape n'a pas été terminée, (pour affichage du marker adéquat dans GoogleMap)
                etapesArrayIsFinished.set(etape, "false");
                etape --;
                // on indique au tableau etapesArrayIsFinished quel est l'étape en cours (pour affichage du marker adéquat dans GoogleMap
                etapesArrayIsFinished.set(etape, "enCours");
                if (textEtapeView!=null) {
                    textEtapeView.setText(texteArretArray.get(etape));
                }
                if (imageEtapeView!=null) {
                    imageEtapeView.setImageURI(Uri.parse(pathFile + "image_etape_" + etape + ".png"));
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Vous êtes déjà à la première étape", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // lancement du chargement et affichage de map-fragment dans un nouveau Thread
    // nécessaire pour avoir affichahe du ProgressDialog
    private void displayMapFragmentCircuitJeuActivity() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Informations", "chargement de la map en cours", true, true);
        Thread newThread = new Thread(new Runnable() {
            public void run() {
                try {
                    //necessaire pour que la progress dialog ait le temps de s'afficher
                    Thread.sleep(100);
                    // necessaire pour la bonne exécution de fragmentManager.executePendingTransactions();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayMapFragmentTaskCircuitActivity(progressDialog);
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        newThread.start();
    }


    private void displayMapFragmentTaskCircuitActivity(ProgressDialog progressDialog) {
        mMapFragment = MapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
       // fragmentTransaction.add(R.id.container_fragment, mMapFragment);
        fragmentTransaction.replace(R.id.container_fragment, mMapFragment);
        fragmentTransaction.commit();
        FragmentManager fragmentManager = getFragmentManager();
        // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
        fragmentManager.executePendingTransactions();
        // on indique que la map est affichée
        mMapFragmentDisplayed = true;
        // on récupère la carte pour pouvoir travailler dessus
        mMap = mMapFragment.getMap();
        Log.i("mMap =", mMap.toString());
        mapGestion = new MapGestion(mMap);
        // Actualisation de la position
        if (locationManager!=null) {
            locationManager.requestLocationUpdates(bestLocationProvider, tempsActualisationLocation, distanceActualisationLocation, locationListener);
        }
        if (location!=null) {
            mapGestion.setUpMap(location);
        }
        progressDialog.dismiss();
        mapGestion.afficherEtapesSelonStatut(etapesArray, etapesArrayIsFinished);
    }


    // pour charger le fragment contenant les textes et photos
public void displayTextFragment() {
    // chargement du fragment des textes
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction ft = getFragmentManager().beginTransaction();

    Fragment testNatureFragment = fragmentManager.findFragmentById(R.id.container_fragment);
    if (testNatureFragment instanceof MapFragment) {
        if (textesCircuitsFragment == null) {
            Log.i("textCircuitFragment", "null");
            ft.replace(R.id.container_fragment, new TextesCircuitsFragment());
            ft.commit();
            // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
            fragmentManager.executePendingTransactions();
            // pour récupérer le fragment par son id, c'est l'id du container auquel on l'a rajouté qu'il faut indiquer
            textesCircuitsFragment = (TextesCircuitsFragment) fragmentManager.findFragmentById(R.id.container_fragment);
        }
        else {
            Log.i("textCircuitFragment", "not null");
            ft.replace(R.id.container_fragment, textesCircuitsFragment);
            ft.commit();
            // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
            fragmentManager.executePendingTransactions();
        }
    }
    else if (testNatureFragment instanceof TextesCircuitsFragment) {
        textesCircuitsFragment = (TextesCircuitsFragment) fragmentManager.findFragmentById(R.id.container_fragment);
            ft.replace(R.id.container_fragment, textesCircuitsFragment);
            ft.commit();
            // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
            fragmentManager.executePendingTransactions();
    }
    else {
        Log.i("textCircuitFragment", "null");
        ft.replace(R.id.container_fragment, new TextesCircuitsFragment());
        ft.commit();
        // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
        fragmentManager.executePendingTransactions();
        // pour récupérer le fragment par son id, c'est l'id du container auquel on l'a rajouté qu'il faut indiquer
        textesCircuitsFragment = (TextesCircuitsFragment) fragmentManager.findFragmentById(R.id.container_fragment);
    }

}

    // à faire lorsque la position de l'utilsateur a changé
    // envoyé par onLocationChanged du LocationListener
    private void updateLocation(Location plocation) {
        location = plocation;
        mapGestion.afficherMaPosition(location);
    }


// pour récupérer les textes et images à afficher
    public static String getTextesEtape() {
       return texteArretArray.get(etape);
    }

    public static Uri getImageEtapeUri() {
     return Uri.parse(pathFile + "image_etape_" + etape + ".png");
    }

    // pour lancer les jeux
    public void lancerJeuPetits(View view) {
        Intent startGame = new Intent(getApplicationContext(), JeuActivity.class);
        startActivity(startGame);
    }

    public void lancerJeuMoyens(View view) {
        Intent startGame = new Intent(getApplicationContext(), JeuMoyenActivity.class);
        startActivity(startGame);
    }

    public void lancerJeuGrands(View view) {
        Intent startGame = new Intent(getApplicationContext(), JeuActivity.class);
        startActivity(startGame);
    }

// surcharge des méthodes de callback de TextesCircuitsFragment.TextCircuitFragmentCallBack
    // nous permet de récupérer les textView et ImagesView du fragment
    @Override
    public void getTextView(TextView textView) {
        textEtapeView=textView;
    }

    @Override
    public void getImageView(ImageView imageView) {
        imageEtapeView=imageView;
    }
}
