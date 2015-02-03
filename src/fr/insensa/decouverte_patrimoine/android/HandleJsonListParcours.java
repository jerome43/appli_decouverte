package fr.insensa.decouverte_patrimoine.android;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HandleJsonListParcours {
  // tableau des données propres à chaque circuit
    private ArrayList<String> titre = new ArrayList();
    private ArrayList<String> description = new ArrayList();
    private ArrayList<String> localisation = new ArrayList();
    private ArrayList<String> uri_picture = new ArrayList();
    private ArrayList<String> zip_name = new ArrayList();
    private ArrayList<String> numero_parcours = new ArrayList();
    private ArrayList<String> departement = new ArrayList<>();
  // url de téléchargement du Json
    private String urlString = null;

  // pour indiquer quand la lecture du Json est terminée
    private volatile boolean parsingComplete = true;
  // pour indiquer la taille du tableau, utile pour les bucles for dans classe MainActivity
    private int sizeArray=0;

  // récupération de l'url de téléchargement du Json passée dans méthode MainActivity
    public HandleJsonListParcours(String url){
        this.urlString = url;

    }
    // méthodes de récupération des données du tableau
    // test préalbale que le tableau n'est pas vide
    public String getTitre(int i){
        if (titre.size()==0)
            return null;
        return titre.get(i);
    }
    public String getDescription(int i){
        if (description.size()==0)
            return null;
        return description.get(i);
    }
    public String getLocalisation(int i){
        if (localisation.size()==0)
        return null;
        else return localisation.get(i);
    }
    public String getUri_picture(int i){
        if (uri_picture.size()==0)
            return null;
        return uri_picture.get(i);
    }
    public String getZipName(int i){
        if (zip_name.size()==0)
            return null;
        return zip_name.get(i);
    }

    public String getNumeroParcours(int i){
        if (numero_parcours.size()==0)
            return null;
        return numero_parcours.get(i);
    }

    public String getDepartement(int i){
        if (numero_parcours.size()==0)
            return null;
        return departement.get(i);
    }

    // renvoie la taille du tableau
    public int getSizeArray() {
        return sizeArray;
    }

    @SuppressLint("NewApi")

    // lecture du fichier Json comportant des objects Json agencés dans un tableau
    private void readAndParseJSON(String in) {
        try {
            Log.i("jerome : ", "debut readAndParsejson");
            // lecteur de tableau
            JSONArray readerArray = new JSONArray(in);
            // lecteur d'objet
            JSONObject reader;
            // lecture de chaque objet dans le tableau
            for (int i=0; i<readerArray.length(); i++){
                reader = (JSONObject) readerArray.opt(i);
                System.out.println(reader);
                // affectation dades valeurs dans les ArrayList
                titre.add(i, reader.getString("titre"));
                description.add(i, reader.getString("description"));
                uri_picture.add(i, reader.getString("uri_picture"));
                localisation.add(i, reader.getString("localisation"));
                zip_name.add(i, reader.getString("zip_name"));
                numero_parcours.add(i, reader.getString("numero_parcours"));
                departement.add(i, reader.getString("département"));
            }

            sizeArray=readerArray.length();
            System.out.println("parsing finish, size Array = " + sizeArray);
            parsingComplete = false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // connexion au serveur de fichier Jsopn et récupération du flux de données
    public void fetchJSON(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Log.i("jerome : ", "debut fetchjson");
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    String data = convertStreamToString(stream);
                    // lecture du Json
                    readAndParseJSON(data);
                    Log.i("jerome : ", "enf readAndParsejson2");
                    stream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public boolean getParsingComplete() {
        return parsingComplete;
    }
}