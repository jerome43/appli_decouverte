package fr.insensa.decouverte_patrimoine.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {



// ****** VARIABLES GLOBALES ET CONSTANTES *****************************************************************************

    // le numéro de parcours en cours, utilisé pour chargement des bonnes images dans les jeux, par défaut mis à 1
    private static String numero_parcours_main = "parcours1" ;
    // the map Fragment
    private MapFragment mMapFragment;
    // pour indiquer que la mapfragmen est affiché
    private boolean mapFragmentIsDisplayed;
    // les options par défaut de création de la map
    private GoogleMapOptions options = new GoogleMapOptions();
    // menu de navigation gauche
    private NavigationDrawerFragment menuGaucheFragment;
    //  Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mTitle;
    // numéro de page à accéder à partir du menu gauche
    //private static int positionPage;
    private static int positionPage;
    // le Context de l'appli
    private Context context;

    // utilisé dans procédure de téléchargement (telechargeAndDisplayParcours)
    private static BroadcastReceiver receiver;
    // pour savoir si le receiver a été enregistré (dans telechargeAndDisplayParcours)
    // utile pour le désenregistrer dans OnPause/onResume/onDestroy/onStop (sinon fuite non trouvée)
    private static boolean receiverRegistrered;
    private static ProgressDialog mainProgressDialog;
    // utilisé dans procédure de téléchargement (telechargerZip)
    private static BroadcastReceiver receiverZip;
    // pour savoir si le receiverZip a été enregistré (dans telechargerZip)
    private boolean receiverZipRegistrered;
    // pour récupérer le menu
    private Menu myMenu;
    // pour indiquer qu'on affiche les fragments liées au NavifgationDrawerFragment
    private boolean navigationDrawerFragmentIsDisplayed;
    private static String NAVIGATION_DRAWER_FRAGMENT_IS_DISPLAYED = "NAVIGATION_DRAWER_FRAGMENT_IS_DISPLAYED";

    private LocationManager locationManager;
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
        Log.i("info", "onCreate Main Activity");

        // désérialisation du layout principal conteneur des fragments
        setContentView(R.layout.main_activity);

        // récupération du context, pour être utilisé plus tard dans des Toast...
        context = this;

        // chargement du menu_gauche principal gauche
        mTitle = getTitle();
        menuGaucheFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.menu_gauche);
        // Set up the drawer.
        menuGaucheFragment.setUp(R.id.menu_gauche, (DrawerLayout) findViewById(R.id.drawer_layout));

        // pour récupérer intent du circuit sélectionné dans SearchActivity onListItemClick
        Intent intent = getIntent();

        // si le contenu n'est pas vide, on traite l'intent : téléchargement et lancement du parcours sélectionné
        if (intent.getData()!=null) {
            parseIntentSearchActivityOnListItemClick(intent);
        }
        intent.setData(null);

        // aucun receiver (BroadcastReceiver) n'est enregistré au démarrage, il le sera dans
        // (dans telechargeAndDisplayParcours)
        receiverRegistrered=false;

    // -------------------- pour géolocalisation -----------------------------------

        // au démarrage, le mapFragment n'est pas affiché
        mapFragmentIsDisplayed = false;

        // accès au gestionnaire de localisation
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceString);

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

        // récupération de la dernière position connue
        location = locationManager.getLastKnownLocation(bestLocationProvider);
        Log.i("info", location.toString());

        // définition des variables d'actualisation du fournisseur de Localisation
        tempsActualisationLocation = 5000; // temps entre deux actualisation, en milliseconde
        distanceActualisationLocation = 5; // distance requise pour une actualisation, en m


    // -------------- on lance par défaut la liste des parcours en affichage

        // permettra de savoir si on était dans l'affichage des navigationDrawerFragment avant de quiter l'appli
        // utile lors du chargement du menu de l'action bae, pour savoir si on doit l'afficher ou non
        if (savedInstanceState != null) {
            navigationDrawerFragmentIsDisplayed = savedInstanceState.getBoolean(NAVIGATION_DRAWER_FRAGMENT_IS_DISPLAYED);}
        else {navigationDrawerFragmentIsDisplayed = false;}
        // to display ListParcours only if navigationDrawerFragmentIsDiplayed, else display savedInstnceBundle
        displayListeParcours();
        // on remet le titre de l'appli dans la barre d'action
        mTitle=getTitle();
        restoreActionBar();
    }


    // Dans onStart, on lance les traitements
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("info", "onStart Main Activity");
    }

    // appelée après OnStart lorsque l'activité a été détruite
    // cela se produit notamment quand changement d'orientation de l'écran
    // // récupère les infos de nSaveInstanceState
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("info", "onRestoreInstanceState Main Activity");
        navigationDrawerFragmentIsDisplayed=savedInstanceState.getBoolean(NAVIGATION_DRAWER_FRAGMENT_IS_DISPLAYED);
    }

    // Arrive juste après onStart. Dans onResume, on s'abonne et on remet le contexte utilisateur
    @Override
    protected  void onResume() {
        super.onResume();
        Log.i("info", "onResume Main Activity");
    }

    // Dans onPause, on se désabonne et on enregistre le contexte utilisateur
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("info", "onPause Main Activity");
        // desenregistrement du BroadcatReceiver (obligatoire)
        // si il a été crée
        if (receiver!=null) {
            Log.i("info", "Main Activity onPause receiver not null");
            // si il a été enregistré et est actif (à vérifier !!!)
            if (receiverRegistrered) {
                Log.i("info", "Main Activity onPause receiverRegistrered true");
                unregisterReceiver(receiver);
                receiverRegistrered=false;
            }}
        if (receiverZip!=null) {
            Log.i("ExpandableView onPause", "receiverZip not null");
            // si il a été enregistré et est actif (à vérifier !!!)
            if (receiverZipRegistrered) {
                Log.i("ExpandableView onPause", "unregisterReceiverZip");
                unregisterReceiver(receiverZip);
                receiverZipRegistrered=false;
            }}
    }


    // Dans onStop on arrête les traitements et on désalloue les objets
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("info", "onStop Main Activity");
    }


    // Dans onDestroy on ne fait rien (elle n'est pas appelée systématiquement),
    // on préfère utiliser les méthodes de type onTrimMemory pour connaître l'état de l'application dans le LRU cache.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("info", "onDestroy Main Activity");
     }

    // si on veut sauvegarder des valeurs précises à restaurer après arrêts, mise en arrière plan...
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (navigationDrawerFragmentIsDisplayed) {
            savedInstanceState.putBoolean(NAVIGATION_DRAWER_FRAGMENT_IS_DISPLAYED, true);
        }
        else {
            savedInstanceState.putBoolean(NAVIGATION_DRAWER_FRAGMENT_IS_DISPLAYED, false);
        }
    }



//  ***************** MENUS DE NAVIGATION *************************************************************


    // mise à jour du menu action bar (menu du haut) si menu gauche fermé
    // la mise à jour si menu gauche ouvert est définie dans la Classe NavigationDrawerFragment
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuGaucheFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.action_bar_menu_gauche_ferme, menu);
            myMenu = menu;
            if (navigationDrawerFragmentIsDisplayed) {
                myMenu.findItem(R.id.parcours).setVisible(true);
            }
            else {
                myMenu.findItem(R.id.parcours).setVisible(false);
            }

            // affichage titre des pages
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    // pour afficher titre des pages appelées par menu gauche
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    // mise à jour des vues selon sélection des items du menu gauche
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mapFragmentIsDisplayed) {
            // on arrête l'actualisation de la position
            if (locationManager!=null && locationListener!=null) {
                locationManager.removeUpdates(locationListener);
            }
            // on enlève le fragment
            transaction.remove(mMapFragment);
        }
        transaction.replace(R.id.container, FragmentVuesMenuGauche.newInstance(position + 1));
        transaction.commit();
        navigationDrawerFragmentIsDisplayed = true;

    }


    // mise à jour des vues selon sélection des items de l'action bar (menu du haut)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // acces à la liste générale des parcours
        if (item.getItemId() == R.id.parcours) {
      if (mapFragmentIsDisplayed) {
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
          // on rend visible le bouton d'accès à la map
          myMenu.findItem(R.id.carte).setVisible(true);
      }
        // on rend invisible le bouton d'affichage de la liste des parcours
        myMenu.findItem(R.id.parcours).setVisible(false);
        displayListeParcours();
        navigationDrawerFragmentIsDisplayed = false;
        // on remet le titre de l'appli dans la barre d'action
        mTitle=getTitle();
        restoreActionBar();
        return true;
        }

        // accès aux recherches
        else if (item.getItemId() == R.id.search) {
            // fonction qui permet l'affichage du champ de recherche
            System.out.println("jerome : search");
            onSearchRequested();
            navigationDrawerFragmentIsDisplayed = false;
            return true;
        }

        // acces à la la mise à jour de la liste générale des parcours
        else if (item.getItemId() == R.id.refresh) {
            // verification préalable de la disponiblité du réseau
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo !=null && networkInfo.isConnected()) {
                if (mapFragmentIsDisplayed) {
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
                    // on rend visible le bouton d'accès à la map
                    myMenu.findItem(R.id.carte).setVisible(true);
                }
                // on rend invisible le bouton d'affichage de la liste des parcours
                myMenu.findItem(R.id.parcours).setVisible(false);
                writeJsonListParcoursInDataBase();
                displayListeParcours();
                navigationDrawerFragmentIsDisplayed = false;
                // on remet le titre de l'appli dans la barre d'action
                mTitle=getTitle();
                restoreActionBar();
                return true;
            }
            else
                Toast.makeText(getApplicationContext(),"Aucun réseau disponible, veuillez activez le wifi ou la connexion de données", Toast.LENGTH_LONG).show();
            return true;
        }

        // access à Map
        else if (item.getItemId() == R.id.carte) {
            // verification préalable de la disponiblité du réseau
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo !=null && networkInfo.isConnected()) {
                // on rend visible le bouton d'affichage de la liste des parcours
                myMenu.findItem(R.id.parcours).setVisible(true);
                // et on rend invisible le bouton d'accès à la map
                myMenu.findItem(R.id.carte).setVisible(false);
               displayMapFragment();
                navigationDrawerFragmentIsDisplayed = false;
                // on remet le titre de l'appli dans la barre d'action
                mTitle=getTitle();
                restoreActionBar();
                return true;
            }
            else
                Toast.makeText(getApplicationContext(),"Aucun réseau disponible, veuillez activez le wifi ou la connexion de données", Toast.LENGTH_LONG).show();
            return true;
        }

        // accès aux jeux
        else if (item.getItemId() == R.id.jouer) {
            if (mapFragmentIsDisplayed) {
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
            }
            navigationDrawerFragmentIsDisplayed = false;
            // penser à mettre dans le manifest les nouvelles activités
            Intent startCircuit = new Intent(getApplicationContext(), CircuitActivity.class);
            startActivity(startCircuit);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


// ****************** MISE A JOUR DES CONTENUS DES VUES *********************************************************


    // lancement du chargement et affichage de map-fragment dans un nouveau Thread
    // nécessaire pour avoir affichahe du ProgressDialog
    protected void displayMapFragment() {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "Informations", "chargement de la map en cours", true, true);
        Thread newThread = new Thread(new Runnable() {
               public void run() {
                   try {
                       //necessaire pour que la progress dialog ait le temps de s'afficher
                       Thread.sleep(100);
                       // necessaire pour la bonne exécution de fragmentManager.executePendingTransactions();
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                              displayMapFragmentTask(progressDialog);
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


    // chargement et affichage de map-fragment
    protected void displayMapFragmentTask(ProgressDialog progressDialog) {
        mMapFragment = MapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, mMapFragment);
        fragmentTransaction.commit();
        FragmentManager fragmentManager = getFragmentManager();
        // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
        fragmentManager.executePendingTransactions();
        // on récupère la carte pour pouvoir travailler dessus
        GoogleMap mMap = mMapFragment.getMap();
        Log.i("mMap =", mMap.toString());
        // Actualisation de la position
        if (locationManager!=null) {
            locationManager.requestLocationUpdates(bestLocationProvider, tempsActualisationLocation, distanceActualisationLocation, locationListener);
        }
        MapGestion mapGestion = new MapGestion(mMap);
        if (location!=null) {
            mapGestion.setUpMap(location);
        }
        // on indique que la MapFragment est affiché
        mapFragmentIsDisplayed=true;
        // on efface la boite de dialogue progressDialog
        progressDialog.dismiss();
    }

    // mise à jour du contenu des vues liées au menu gauche
    protected void onSectionAttached(int number) {
        // mise à jour de variables en fonction du choix du menu gauche
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                positionPage = 0;
                break;

            case 2:
                mTitle = getString(R.string.title_section2);
                positionPage = 1;
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                positionPage = 2;
                break;
        }
    }





// ********************************** GESTION DES PARCOURS **********************************


    // appelé par onClick item action bar: affichage du fragment de la liste générale des parcours
    protected void displayListeParcours() {
        // on lance la lecture du json des différents circuits et on inscrit les valeurs dans la BD
        ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
        // si c'est la première fois que l'appli est lancée, on télécharges les parcours et on les écrit dans la BD,
        if (parcoursDataBase.getFirstInstallation().equals("1")) {
            // verification préalable de la disponiblité du réseau
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo !=null && networkInfo.isConnected()) {
                parcoursDataBase.closeDatabase();
                writeJsonListParcoursInDataBase();
            }
            else
                Toast.makeText(getApplicationContext(),"Aucun réseau disponible, veuillez activez le wifi ou la connexion de données", Toast.LENGTH_LONG).show();
                parcoursDataBase.closeDatabase();
        }
        // sinon on lit les valeurs dans la BD et on les affiche  et on prévoit une proposition d'actualisation

        else {
            parcoursDataBase.closeDatabase();
            readDatabaseAndDisplayParcours();
        }
    }

    // pour lire document json de la liste des parcours et inscrire valeurs dans la BD
    protected void writeJsonListParcoursInDataBase(){
        new WriteJsonListParcoursInDataBasTask().execute((Void[]) null);
    }

    // classe utilisée pr writeJsonListParcoursInDatabase
    private class WriteJsonListParcoursInDataBasTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Informations", "actualisation de la liste des parcours", true, true);
        }


        protected Void doInBackground(Void... params) {
            try {
                // teste si le parcours est déjà dans la BD
                boolean success = false;
                String urlJsonListParcours = "http://insensa.Fr/appli_decouverte/circuits.json";
                HandleJsonListParcours handleJsonListParcours = new HandleJsonListParcours(urlJsonListParcours);
                // lecture du Json et création du tableau
                handleJsonListParcours.fetchJSON();
                while (handleJsonListParcours.getParsingComplete()) {
                    System.out.println("parsing not complete" + handleJsonListParcours.getSizeArray());
                    // quand la lecture du Json est terminé (le Json n'est pas vide)
                    if (handleJsonListParcours.getSizeArray() != 0) {
                        //   Toast.makeText(this, sizeArray, Toast.LENGTH_LONG ).show();
                        ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
                        // récupération de toutes les valeurs de la base
                        parcoursDataBase.getCursorValues();
                        // récupération du nombre de parcours dans la BD
                        int countCursorLine = parcoursDataBase.getCountCursorLine();
                        // parcours du tableau du json
                        for (int i = 0; i < handleJsonListParcours.getSizeArray(); i++) {
                            System.out.println("boucle commence");
                            // si la BD n'est pas vide, donc qu'il existe déjà des parcours
                            if (countCursorLine != 0) {
                                // parcours dans la BD
                                for (int ii = 0; ii < countCursorLine; ii++) {
                                    String titre = parcoursDataBase.getTitre(ii);
                                    System.out.println("titre parcours in DB:" + titre);
                                    System.out.println("titre parcours in Json:" + handleJsonListParcours.getTitre(i));
                                    // on compare les titres du tableau avec titres dans la base
                                    if (titre.contentEquals(handleJsonListParcours.getTitre(i))) {
                                        success = true;
                                        System.out.println("parcours dans la base");
                                        break;
                                    } else {
                                        success = false;
                                        System.out.println("parcours pas dans la base");
                                    }
                                }
                                // si les parcours n'étaient pas dans la base, on les ajoute
                                if (!success) {
                                    parcoursDataBase.addNewListParcours(handleJsonListParcours.getTitre(i), handleJsonListParcours.getLocalisation(i),
                                            handleJsonListParcours.getDescription(i), handleJsonListParcours.getUri_picture(i), handleJsonListParcours.getZipName(i),
                                            handleJsonListParcours.getNumeroParcours(i), handleJsonListParcours.getDepartement(i));
                                }
                            }
                            // si la BD est vide, on ajoute tous les parcours
                            else {
                                System.out.println("base de donnée vide, ajout de tous les nouveaux parcours");
                                parcoursDataBase.addNewListParcours(handleJsonListParcours.getTitre(i), handleJsonListParcours.getLocalisation(i),
                                        handleJsonListParcours.getDescription(i), handleJsonListParcours.getUri_picture(i), handleJsonListParcours.getZipName(i),
                                        handleJsonListParcours.getNumeroParcours(i), handleJsonListParcours.getDepartement(i));
                                // on met la valeur first_installation à 0 pour ne plus à avoir systématiquement à télécharger les parcours
                                // l'utilisateur le fera à partir de la fonction refresh
                                parcoursDataBase.updateFirstInstallation();
                            }
                            System.out.println("boucle finish");
                        }
                        parcoursDataBase.closeDatabase();
                        System.out.println("close Database");
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(Void result) {
            if (progressDialog!=null) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Mise à jour des parcours réussie", Toast.LENGTH_SHORT).show();
                displayListeParcours();
            }
        }
    }

    // pour lire document json de la liste des parcours et inscrire valeurs dans la BD
    protected void writeJsonParcoursInDataBase(String numero_parcours){
        new WriteJsonParcoursInDataBasTask(numero_parcours).execute((Void[]) null);
    }

    // classe utilisée par writeJsonParcoursInDatabase
    private class WriteJsonParcoursInDataBasTask extends AsyncTask<Void, Void, Void> {
        private String numeroParcours;

        // constructeur par défaut
        public WriteJsonParcoursInDataBasTask(String numeroParcours) {
            this.numeroParcours = numeroParcours;
        }

        ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Informations", "écriture des données du parcours", true, true);
        }

        protected Void doInBackground(Void... params) {
            try {
                // teste si le parcours est déjà dans la BD
               // boolean success = false;
                String location = Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/" + getPackageName()
                        + "/files/" + this.numeroParcours + "/"+ this.numeroParcours + ".json";
                HandleJsonParcours handleJsonParcours = new HandleJsonParcours(location);
                // lecture du Json et création du tableau
                handleJsonParcours.fetchJSON();
                ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
                while (handleJsonParcours.getParsingComplete()) {
                    System.out.println("parsing not complete" + handleJsonParcours.getSizeArray());
                    // quand la lecture du Json est terminé (le Json n'est pas vide)
                    if (handleJsonParcours.getSizeArray() != 0) {
                        // parcours du tableau du json
                        for (int i = 0; i < handleJsonParcours.getSizeArray(); i++) {
                            parcoursDataBase.updateParcoursValue(this.numeroParcours, handleJsonParcours.getTexteArret1(i), handleJsonParcours.getTexteArret2(i),
                                    handleJsonParcours.getTexteArret3(i), handleJsonParcours.getTexteArret4(i), handleJsonParcours.getConsigneArret1(i),
                                    handleJsonParcours.getConsigneArret2(i), handleJsonParcours.getConsigneArret3(i), handleJsonParcours.getConsigneArret4(i));
                        }
                    }
                }
                parcoursDataBase.closeDatabase();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(Void result) {
            if (progressDialog!=null) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Ecriture des données du parcours réussie", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // pour lire les parcours dans la BD et les afficher
    protected void readDatabaseAndDisplayParcours() {

        System.out.println("read data base and display parcours");
        ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
        // récupération de toutes les valeurs de la base
        parcoursDataBase.getCursorValues();
        int countCursorLine= parcoursDataBase.getCountCursorLine();
        System.out.println("countCursorLine : " + countCursorLine);
        if (countCursorLine != 0) {
            // parcours de toutes les lignes de la base et affectation à des variables
            ArrayList<String> uri = new ArrayList<>();
            ArrayList<String> titre = new ArrayList<>();
            ArrayList<String> description = new ArrayList<>();
            ArrayList<String> localisation = new ArrayList<>();
            ArrayList<String> zipName = new ArrayList<>();
            ArrayList<String> parcoursInList = new ArrayList<>();
            ArrayList<String> key_id = new ArrayList<>();
            ArrayList<String> is_installed = new ArrayList<>();
            ArrayList<String> numero = new ArrayList<>();
            ArrayList<String> departement = new ArrayList<>();
            for (int i = 0; i < countCursorLine; i++) {
                System.out.println("boucle read data base");
                uri.add(parcoursDataBase.getUri_picture(i));
                titre.add(parcoursDataBase.getTitre(i));
                description.add(parcoursDataBase.getDescription(i));
                localisation.add(parcoursDataBase.getLocalisation(i));
                zipName.add(parcoursDataBase.getZipName(i));
                parcoursInList.add(parcoursDataBase.getParcoursInList(i));
                key_id.add(parcoursDataBase.getKeyId(i));
                is_installed.add(parcoursDataBase.getIsInstalled(i));
                numero.add(parcoursDataBase.getNumero_parcours(i));
                departement.add(parcoursDataBase.getDepartement(i));
            }
            // fermeture de la connexion
            parcoursDataBase.closeDatabase();
            // téléchargement des images et affichage des descriptions des parcours à partir des variables
            telechargeAndDisplayParcours(key_id, uri, titre, description, zipName, parcoursInList, numero, departement);
        }
        else System.out.println("erreur, base de donnée vide");
        parcoursDataBase.closeDatabase();
    }


    // téléchargement sous condition des images de la liste des pacours avec le gestionnaire de téléchargement et affichage des descriptions des parcours à partir des paramètres
    protected void telechargeAndDisplayParcours(ArrayList<String> pKeyId, ArrayList<String> pUrlIcone, ArrayList<String> pTitre,
                                             ArrayList<String> pDescription, ArrayList<String> pzipName, ArrayList<String> pParcoursInList,
                                             ArrayList<String> pNumeroParcours, ArrayList<String> pDepartement) {

        System.out.println("télécharger and display Parcours");
        System.out.println("jerome parcoursInList : " + pParcoursInList);

        // affichage de la boite de dialogue si elle n'est pas déjà visible
        if (mainProgressDialog==null) {
            mainProgressDialog = ProgressDialog.show(this, "Informations", "chargement des icones des parcours", true, true);
        }

        else {
            if (!mainProgressDialog.isShowing()) {
                mainProgressDialog = ProgressDialog.show(this, "Informations", "chargement des icones des parcours", true, true);
            }
        }
        // test si parcours déjà présent dans la liste
        // si le parcours n'était pas présent dans la base, on télécharge son icone et on affiche les informations
        for (int i = 0; i<pParcoursInList.size(); i++) {
            if (pParcoursInList.get(i).contentEquals("false")) {
                 new TelechargeAndDisplayParcoursTask(pUrlIcone, pzipName, pKeyId, pNumeroParcours,
                        pTitre, pDescription, pDepartement, pParcoursInList, i).execute((Void[]) null);
                // on casse la boucle pour ne pas qu'elle continue avant que les intents envoyées par TelechargeAndDisplayParcoursTask ne soinet onReceive
                // ce qui généère des bugs et des fuitesde l'objet receiver
                // la boucle sera rebouclée dans onReceive
                break;
            }
            else {
                // au dernier tour de boucle, on affiche les textes et icones
                if (i==(pParcoursInList.size()-1)) {
                    mainProgressDialog.dismiss();
                    displayTextAndIconExpandableParcours(pUrlIcone, pzipName, pKeyId, pNumeroParcours, pTitre, pDescription, pDepartement);
                }
            }
        }
    }


    private class TelechargeAndDisplayParcoursTask extends AsyncTask<Void, Void, Void> {
        final ArrayList<String> keyId;
        final ArrayList<String> urlIcone;
        final ArrayList<String> titre;
        final ArrayList <String> description;
        final ArrayList<String> zipName;
        final ArrayList<String> numeroParcours;
        final ArrayList<String> departement;
        final ArrayList<String> parcoursInList;
        final int finalI;

        public TelechargeAndDisplayParcoursTask(ArrayList<String> pUrlIcone,  ArrayList<String> pzipName, ArrayList<String> pKeyId,
                                                ArrayList<String> pNumeroParcours, ArrayList<String> pTitre,
                                                ArrayList<String> pDescription, ArrayList<String> pDepartement,
                                                ArrayList<String> pParcoursInList, int pfinalI)
        {
            keyId = pKeyId;
            urlIcone = pUrlIcone;
            titre = pTitre;
            description = pDescription;
            zipName = pzipName;
            numeroParcours = pNumeroParcours;
            departement = pDepartement;
            parcoursInList = pParcoursInList;
            finalI = pfinalI;
        }

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("parcoursInList = false => téléchargement");
            String serviceString = Context.DOWNLOAD_SERVICE;
            final DownloadManager downloadManager;
            downloadManager = (DownloadManager) getSystemService(serviceString);
            Uri uri = Uri.parse("http://insensa.fr/appli_decouverte/" + urlIcone.get(finalI));
            final DownloadManager.Request request = new DownloadManager.Request(uri);
            // précise que les fichiers téléchargés seront stockés dans le dossier "icone_parcours" du dossier externe de l'application
            request.setDestinationInExternalFilesDir(getApplicationContext(), "icone_parcours", urlIcone.get(finalI));
            // création d'une référence pour pouvoir interroger l'état, etc... du téléchargement et lancement du téléchargement (enqueue)
            final long downloadReference = downloadManager.enqueue(request);
            // System.out.println("jerome : id download reference zip : " + downloadReference);
            // surveiller la fin du téléchargement
            final IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            // création du BroadCastReceiver
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // création d'un PendingResult qui permet d'empêcher le ramasse-miette
                    // d'effacer les résulats du BroadCastReceiver avant l'appel à resulat.finish()
                    final PendingResult result = goAsync();
                    final long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    //   System.out.println("jerome : id reference zip : " + reference);
                    if (downloadReference == reference) {
                        // pour interroger l'état, etc.. du téléchargement avec la reference
                        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
                        myDownloadQuery.setFilterById(reference);
                        Cursor myDonwload = downloadManager.query(myDownloadQuery);
                        if (myDonwload.moveToFirst()) {
                            Log.i("jerome", parcoursInList.get(finalI));
                            // récupération du nom et de l'URI du fichier
                            //int fileNameIdx = myDonwload.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                            // on met à jour la BD pour indiquer que l'icone du parcours a été téléchargé et qu'il n'y aura plus besoin de le faire à l'avenir
                            ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
                            parcoursDataBase.updateParcoursInList(numeroParcours.get(finalI), "true");
                            parcoursDataBase.closeDatabase();
                            // permet le recyclage du receiver
                            result.finish();

                            // desenregistrement du BroadcatReceiver (obligatoire)
                            // si il a été crée
                            if (receiver!=null) {
                                Log.i("info", "Main Activity onPause receiver not null");
                                // si il a été enregistré et est actif (à vérifier !!!)
                                if (receiverRegistrered) {
                                    Log.i("info", "Main Activity onPause receiverRegistrered true");
                                    unregisterReceiver(receiver);
                                    receiverRegistrered=false;
                                }}
                            // au dernier tour de boucle, on affiche les textes et icones
                            if (finalI==(parcoursInList.size()-1)) {
                                Log.i("info", "lancement DisplayTextAndIcone");
                              mainProgressDialog.dismiss();
                              Toast.makeText(getApplicationContext(), "Téléchargement des icones des parcours réussie", Toast.LENGTH_LONG).show();
                              displayTextAndIconExpandableParcours(urlIcone, zipName, keyId, numeroParcours,titre, description, departement);
                            }
                            else {
                                // avant de renvoyer le tableau à telechargerAndDisplay, on indique que le parcours a ét téléchargé
                                parcoursInList.set(finalI, "true");
                                // on relance téléchargerAndDisplay pour finir de boucler
                                telechargeAndDisplayParcours(keyId, urlIcone, titre, description, zipName, parcoursInList, numeroParcours, departement);
                            }
                        }
                        System.out.println("jerome " + " mydownloadclosed");
                        myDonwload.close();
                    }
                }
            };
            receiverRegistrered = true;
            registerReceiver(receiver, filter);
            Log.i("info", "registerReceiver Main Activity telechargeAndDisplayParcours");
            return null;
        }

        protected void onPostExecute(Void result) {

        }
    }


    protected void displayTextAndIconExpandableParcours(ArrayList<String> pUri, ArrayList<String> pzipName, ArrayList<String> pKeyId,
                                                     ArrayList<String> pNumeroParcours, ArrayList<String> ptitre,
                                                     ArrayList<String> pdescription, ArrayList<String> pDepartement ) {

        System.out.println("débutDisplayIconeParcours");

        Bundle args = new Bundle();
        args.putStringArrayList("_KEY_ID", pKeyId);
        args.putStringArrayList("_URI", pUri);
        args.putStringArrayList("_ZIP_NAME", pzipName);
        args.putStringArrayList("_NUMERO_PARCOURS", pNumeroParcours);
        args.putStringArrayList("_TITRE", ptitre);
        args.putStringArrayList("_DESCRIPTION", pdescription);
        args.putStringArrayList("_DEPARTEMENT", pDepartement);

       FragmentManager fragmentManager = getFragmentManager();
        ExpandableListParcours fragment = new ExpandableListParcours();
        fragment.setArguments(args);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
        // permet d'attendre que le commit soit terminé avant de continuer à exécuter le code
        fragmentManager.executePendingTransactions();
    }


    // télécharge le fichier zip du parcours, le dézippe (lance unzip), puis lance le parcours quand téléchargement terminé
    protected void telechargerZip(String zipName, final String pNumeroParcours) {
        Uri uri = Uri.parse("http://insensa.fr/appli_decouverte/" + zipName);
        String serviceString = Context.DOWNLOAD_SERVICE;
        final DownloadManager downloadManagerZip;
        downloadManagerZip = (DownloadManager)getSystemService(serviceString);
        final DownloadManager.Request request = new DownloadManager.Request(uri);
        // précise que les fichiers téléchargés seront stockés dans le dossier numeroParcours du dossier externe de l'application avec le nom zipName
        request.setDestinationInExternalFilesDir(this, pNumeroParcours, zipName);

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Informations", "chargement du parcours", true, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // ci-aprtès ne marche pas marche pas car le téléchargement doit se faire sur un externalStorage
                    // Uri uriDestination = Uri.parse(getFilesDir().toURI().toString()+"assets/");
                    //request.setDestinationUri(uriDestination);
                    // création d'une référence pour pouvoir interroger l'état, etc... du téléchargement et lancement du téléchargement (enqueue)
                    final long downloadReferenceZip = downloadManagerZip.enqueue(request);
                    // System.out.println("jerome : id downloadreference zip : " + downloadReferenceZip);

                    // surveiller la fin du téléchargement
                    IntentFilter filterZip = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                  //  BroadcastReceiver receiverZip = new BroadcastReceiver() {
                        receiverZip = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intentZip) {
                            //EXTRA_DOWNLOAD_ID : included with ACTION_DOWNLOAD_COMPLETE intents, indicating the ID (as a long) of the download that just completed
                            final long referenceZip = intentZip.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                            //System.out.println("jerome : id reference zip : " + referenceZip);
                            if (downloadReferenceZip == referenceZip) {
                                // pour interroger l'état, etc.. du téléchargement avec la referenceZip
                                DownloadManager.Query myDownloadQueryZip = new DownloadManager.Query();
                                myDownloadQueryZip.setFilterById(referenceZip);
                                Cursor myDonwloadZip = downloadManagerZip.query(myDownloadQueryZip);

                                myDonwloadZip.moveToFirst();
                                if (myDonwloadZip.moveToFirst()) {
                                    // System.out.println("jerome " + " : téléchargement zip terminé : lignes : " + count);
                                    // récupération du nom et de l'URI du fichier
                                    int fileNameIdx = myDonwloadZip.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                                    //System.out.println("jerome " + fileNameIdx);
                                    int fileUriIdx = myDonwloadZip.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                                    //System.out.println("jerome " + fileUriIdx);
                                    String fileName = myDonwloadZip.getString(fileNameIdx);
                                    System.out.println("jerome" + fileName);
                                    String fileUri = myDonwloadZip.getString(fileUriIdx);
                                    // Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();
                                    System.out.println("jerome" + fileUri);
                                    // dézippe le fichier telechargé
                                    try {
                                        unzip(fileName, pNumeroParcours);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    // desenregistrement du BroadcatReceiver (obligatoire)
                                    // si il a été crée
                                    if (receiverZip!=null) {
                                        // si il a été enregistré et est actif (à vérifier !!!)
                                        if (receiverZipRegistrered) {
                                            unregisterReceiver(receiverZip);
                                            receiverZipRegistrered=false;
                                        }}
                                }
                                // fermeture du Cursor
                                myDonwloadZip.close();
                                // changement dans la BD pour indiquer que le parcours est installé
                                ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
                                parcoursDataBase.updateParcoursIsInstalled(pNumeroParcours, "true");
                                parcoursDataBase.closeDatabase();
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Téléchargement du parcours réussi", Toast.LENGTH_SHORT).show();
                                numero_parcours_main = pNumeroParcours;
                            }
                        }
                    };
                    registerReceiver(receiverZip, filterZip);
                    receiverZipRegistrered=true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    // dezippage d'un parcours
    protected void unzip(String pzipFile, String pNumeroParcours) throws IOException {
        // à faire, rajouter un paramètre de numéro de parcours concerné, le path changera avec à la place de parcours, parcours1... parcours 2... etc

        //  String location = Environment.getExternalStorageDirectory() +"/Android/data/fr.insensa.decouverte_patrimoine.android/files/parcours/";
        String location = Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/" + getPackageName() + "/files/" + pNumeroParcours + "/";
        int size;
        byte[] buffer = new byte[1024];
        System.out.println("jerome" + "début unzip");

        System.out.println("jerome" + pzipFile);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream((new FileInputStream(pzipFile))));
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                String path = location + ze.getName();
                File unzipFile = new File(path);
                // unzip the file
                FileOutputStream out = new FileOutputStream(unzipFile, false);
                BufferedOutputStream fout = new BufferedOutputStream(out, 1024);
                try {
                    while ((size = zis.read(buffer, 0, 1024)) != -1) {
                        fout.write(buffer, 0, size);
                    }
                    zis.closeEntry();
                } finally {
                    fout.flush();
                    fout.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            zis.close();
            System.out.println("jerome " + " dézippage terminé, lancement du jeu");
            // récupérer son Json et inscrire les valeurs dans la BD
            writeJsonParcoursInDataBase(pNumeroParcours);
            // lancer le jeu
            Toast.makeText(getApplicationContext(), "lancement du jeu", Toast.LENGTH_LONG).show();
            Intent startCircuit = new Intent(getApplicationContext(), CircuitActivity.class);
            startActivity(startCircuit);
        }
    }

// *************************************** TRAITEMENT DES INTENTS ************************************************

    // traitement de l'intent envoyé par SearchActivity => onListItemClich : télécharge et lance le parcours sélectionné
    protected void parseIntentSearchActivityOnListItemClick(Intent intent) {
        // on récupère l'Uri du Cursor sélectionné
        Uri dataIntent = intent.getData();
        // les données que l'on va récupérer dans le Cursor
        String[] projection = {
                MyContentProvider.KEY_ID,
                MyContentProvider.KEY_NUMERO_PARCOURS,
                MyContentProvider.KEY_ZIP_NAME
        };
        // récupération des contenus du cursor à l'Uri en question, selon projection (données souhaitées), sans aucunes conditions
        Cursor cursor = getContentResolver().query(dataIntent, projection, null, null, null);
        // on se positionne sur la première ligne du cursor (ici il n'y en a qu'une de toute façon
        cursor.moveToFirst();
        // on récupère l'index des colonnes
        int keyIdIdx = cursor.getColumnIndexOrThrow(MyContentProvider.KEY_ID);
        int numeroIdx = cursor.getColumnIndexOrThrow(MyContentProvider.KEY_NUMERO_PARCOURS);
        int zipNameIdx = cursor.getColumnIndexOrThrow(MyContentProvider.KEY_ZIP_NAME);
        // on récupère le contenu des colonnes à l'index en question
        String keyId = cursor.getString(keyIdIdx);
        String numeroParcours = cursor.getString(numeroIdx);
        String zipName = cursor.getString(zipNameIdx);
        System.out.println(keyId + " / " + numeroParcours + " / " + zipName);

        // test si ZIP parcours déjà installé, auquel cas on lance le parcours direcetement
        final String is_installed;
        ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getApplicationContext());
        parcoursDataBase.getOneCursorValues(numeroParcours);
        is_installed = parcoursDataBase.getOneIsInstalled();
        parcoursDataBase.closeDatabase();
        if (is_installed.contentEquals("true")) {
            numero_parcours_main = numeroParcours;
            // lancer le parcours
            System.out.println("parcours déjà installé : lancement du parcours !");
            Toast.makeText(getApplicationContext(), "lancement du jeu", Toast.LENGTH_LONG).show();
            Intent startCircuit = new Intent(getApplicationContext(), CircuitActivity.class);
            startActivity(startCircuit);
        }

        else {
            // télécharger les fichiers liés au circuit, les installer et lancer le jeu
            telechargerZip(zipName, numeroParcours);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("jérôme : new intent");
        // pour récupérer intent du circuit sélectionné dans SearchActivity onListItemClick
        // si le contenu n'est pas vide, on traite l'intent
        if (intent.getData()!=null) {
            parseIntentSearchActivityOnListItemClick(intent);
        }
        intent.setData(null);
    }

    // à faire lorsque la position de l'utilsateur a changé
    // envoyé par onLocationChanged du LocationListener
    private void updateLocation(Location location) {
        MainActivity.location = location;
    }

    public static String getNumero_parcours_main() {
        return numero_parcours_main;
    }

    public static void setNumero_parcours_main(String numero_parcours) {
       numero_parcours_main = numero_parcours;
    }

    public static int getPositionPage() {
        return positionPage;
    }
}
