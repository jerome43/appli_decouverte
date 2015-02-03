package fr.insensa.decouverte_patrimoine.android;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Jérôme on 13/01/2015.
 */
public class HandleJsonParcours {
    // tableau des données propres à chaque circuit
    private ArrayList<String> titre = new ArrayList();
    private ArrayList<String> description = new ArrayList();
    private ArrayList<String> localisation = new ArrayList();
    private ArrayList<String> uri_picture = new ArrayList();
    private ArrayList<String> zip_name = new ArrayList();
    private ArrayList<String> numero_parcours = new ArrayList();
    private ArrayList<String> departement = new ArrayList<>();
    private ArrayList<String> texte_arret_1_Array = new ArrayList<>();
    private ArrayList<String> texte_arret_2_Array = new ArrayList<>();
    private ArrayList<String> texte_arret_3_Array = new ArrayList<>();
    private ArrayList<String> texte_arret_4_Array = new ArrayList<>();
    private ArrayList<String> consigne_arret_1_Array = new ArrayList<>();
    private ArrayList<String> consigne_arret_2_Array= new ArrayList<>();
    private ArrayList<String> consigne_arret_3_Array = new ArrayList<>();
    private ArrayList<String> consigne_arret_4_Array = new ArrayList<>();
    // url de téléchargement du Json
    private String urlString = null;

    // pour indiquer quand la lecture du Json est terminée
    private volatile boolean parsingComplete = true;
    // pour indiquer la taille du tableau, utile pour les boucles for dans classe MainActivity
    private int sizeArray=0;
    // récupération de l'url de téléchargement du Json passée dans méthode MainActivity

   // constructeur par défaut
    public HandleJsonParcours(String url){
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

    public String getTexteArret1(int i) {
        if (texte_arret_1_Array.size()==0)
            return null;
        return texte_arret_1_Array.get(i);
    }
    public String getTexteArret2(int i) {
        if (texte_arret_2_Array.size()==0)
            return null;
        return texte_arret_2_Array.get(i);
    }

    public String getTexteArret3(int i) {
        if (texte_arret_3_Array.size()==0)
            return null;
        return texte_arret_3_Array.get(i);
    }

    public String getTexteArret4(int i) {
        if (texte_arret_4_Array.size()==0)
            return null;
        return texte_arret_4_Array.get(i);
    }
    public String getConsigneArret1(int i) {
        if (consigne_arret_1_Array.size()==0)
            return null;
        return consigne_arret_1_Array.get(i);
    }
    public String getConsigneArret2(int i) {
        if (consigne_arret_2_Array.size()==0)
            return null;
        return consigne_arret_2_Array.get(i);
    }
    public String getConsigneArret3(int i) {
        if (consigne_arret_3_Array.size()==0)
            return null;
        return consigne_arret_3_Array.get(i);
    }
    public String getConsigneArret4(int i) {
        if (consigne_arret_4_Array.size()==0)
            return null;
        return consigne_arret_4_Array.get(i);
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
                // affectation des valeurs dans les ArrayList
                texte_arret_1_Array.add(i, reader.getString("texte_arret_1"));
                texte_arret_2_Array.add(i, reader.getString("texte_arret_2"));
                texte_arret_3_Array.add(i, reader.getString("texte_arret_3"));
                texte_arret_4_Array.add(i, reader.getString("texte_arret_4"));
                consigne_arret_1_Array.add(i, reader.getString("consigne_arret_1"));
                consigne_arret_2_Array.add(i, reader.getString("consigne_arret_2"));
                consigne_arret_3_Array.add(i, reader.getString("consigne_arret_3"));
                consigne_arret_4_Array.add(i, reader.getString("consigne_arret_4"));
            }

            sizeArray=readerArray.length();
            System.out.println("parsing finish, size Array = " + sizeArray);
            parsingComplete = false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // connexion au fichier Jsopn et récupération du flux de données
    public void fetchJSON(){

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                File yourFile = new File(urlString);
                FileInputStream stream = null;
                String jsonStr = null;
                try {
                    stream = new FileInputStream(yourFile);
                    try {
                        FileChannel fc = stream.getChannel();
                        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                        jsonStr = Charset.defaultCharset().decode(bb).toString();
                        readAndParseJSON(jsonStr);
                    }
                    finally {
                        stream.close();
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public boolean getParsingComplete() {
        return parsingComplete;
    }
}
