package fr.insensa.decouverte_patrimoine.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class ParcoursDataBase {
    // ArrayListe qui contiendront les valeurs de l'ensemble de la base (requête sur toutes les lignes)
    private ArrayList<String> key_idArray = new ArrayList();
    private ArrayList<String> titreArray = new ArrayList();
    private ArrayList<String> descriptionArray = new ArrayList();
    private ArrayList<String> localisationArray = new ArrayList();
    private ArrayList<String> uri_pictureArray = new ArrayList();
    private ArrayList<String> zip_nameArray = new ArrayList();
    private ArrayList<String> parcours_In_ListArray = new ArrayList();
    private ArrayList<String> is_InstalledArray = new ArrayList();
    private ArrayList<String> numero_parcoursArray = new ArrayList();
    private ArrayList<String> departementArray = new ArrayList<>();
    private ArrayList<String> texte_arret_1_Array = new ArrayList<>();
    private ArrayList<String> texte_arret_2_Array = new ArrayList<>();
    private ArrayList<String> texte_arret_3_Array = new ArrayList<>();
    private ArrayList<String> texte_arret_4_Array = new ArrayList<>();
    private ArrayList<String> consigne_arret_1_Array = new ArrayList<>();
    private ArrayList<String> consigne_arret_2_Array= new ArrayList<>();
    private ArrayList<String> consigne_arret_3_Array = new ArrayList<>();
    private ArrayList<String> consigne_arret_4_Array = new ArrayList<>();

    private int countCursorLine=0;

    // utilisé quand requête porte sur une seule ligne de la base
    private String key_id;
    private String titre;
    private String description;
    private String localisation;
    private String uri_picture;
    private String zip_name;
    private String parcours_in_list;
    private String is_installed;
    private String numero_parcours;
    private String first_installation;
    private String departement;
    private String texte_arret_1;
    private String texte_arret_2;
    private String texte_arret_3;
    private String texte_arret_4;
    private String consigne_arret_1;
    private String consigne_arret_2;
    private String consigne_arret_3;
    private String consigne_arret_4;



    //The index (key) column name for use in where clauses.
    // Table parcours
  private static final String KEY_ID = "_id";
  //The name and column index of each column in your database.
  //These should be descriptive.
  private static final String KEY_TITRE_PARCOURS =
   "TITRE_PARCOURS";
  private static final String KEY_DESCRIPTION_PARCOURS =
   "DESCRIPTION_PARCOURS";
  private static final String KEY_LOCALISATION =
   "LOCALISATION";
  private static final String KEY_URI_PICTURE =
    "URI_PICTURE";
  private static final String KEY_ZIP_NAME =
            "ZIP_NAME";
  private static final String KEY_PARCOURS_IN_LIST =
            "PARCOURS_IN_LIST";
  private static final String KEY_IS_INSTALLED =
            "IS_INSTALLED";
  private static final String KEY_NUMERO_PARCOURS =
            "NUMERO_PARCOURS";
  private static final String KEY_DEPARTEMENT =
            "DEPARTEMENT";
    private static final String KEY_TEXTE_ARRET_1 =
            "TEXTE_ARRET_1";
    private static final String KEY_TEXTE_ARRET_2 =
            "TEXTE_ARRET_2";
    private static final String KEY_TEXTE_ARRET_3 =
            "TEXTE_ARRET_3";
    private static final String KEY_TEXTE_ARRET_4 =
            "TEXTE_ARRET_4";
    private static final String KEY_CONSIGNE_ARRET_1 =
            "CONSIGNE_ARRET_1";
    private static final String KEY_CONSIGNE_ARRET_2 =
            "CONSIGNE_ARRET_2";
    private static final String KEY_CONSIGNE_ARRET_3 =
            "CONSIGNE_ARRET_3";
    private static final String KEY_CONSIGNE_ARRET_4 =
            "CONSIGNE_ARRET_4";

  // table Main
  private   static  final String KEY_FIRST_INSTALLATION = "KEY_FIRST_INSTALLATION";
  private static final String KEY_ID_MAIN = "_id";

  // Database open/upgrade helper
  private static ParcoursDBOpenHelper parcoursDBOpenHelper;

  public ParcoursDataBase(Context context) {
    parcoursDBOpenHelper = new ParcoursDBOpenHelper(context, ParcoursDBOpenHelper.DATABASE_NAME, null,
                                              ParcoursDBOpenHelper.DATABASE_VERSION);
      System.out.println("parcoursDataBase initialized");
  }

     // Called when you no longer need access to the database.
  public void closeDatabase() {
    parcoursDBOpenHelper.close();
  }


    // Pour obtenir le Cursor de l'ensembles des parcours (soit toutes les lignes de la base
    private Cursor getAllParcours() {

        // Les colonnes que l'on souhaite extraire

        String[] result_columns = new String[] {
                KEY_ID, KEY_TITRE_PARCOURS, KEY_LOCALISATION, KEY_DESCRIPTION_PARCOURS,
                KEY_URI_PICTURE, KEY_ZIP_NAME, KEY_PARCOURS_IN_LIST, KEY_IS_INSTALLED, KEY_NUMERO_PARCOURS, KEY_DEPARTEMENT,
                KEY_TEXTE_ARRET_1, KEY_TEXTE_ARRET_2, KEY_TEXTE_ARRET_3, KEY_TEXTE_ARRET_4,
                KEY_CONSIGNE_ARRET_1, KEY_CONSIGNE_ARRET_2, KEY_CONSIGNE_ARRET_3, KEY_CONSIGNE_ARRET_4};
        // les clauses sql pour limiter ou formater le rendu
        String where = null ;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ParcoursDBOpenHelper.DATABASE_TABLE,
                result_columns, where,
                whereArgs, groupBy, having, order);
        //
        return cursor;
    }

    // exemple pour lire le cursor de tous les parcours
  public void getCursorValues() {
    //  ArrayList<String> listeParcours = new ArrayList<String>();
    // récupération du cursor de tous les parcours
      Cursor cursor = getAllParcours();
    // on se positionne sur la première ligne
    cursor.moveToFirst();
      // on vérfie qu'on a pas déjà atteint la dernière ligne
      while (!cursor.isAfterLast()) {
          // récupération des valeurs de la ligne sur laquelle on est positionné et à la colonne indiquée en index
          String key_idCursor = cursor.getString(0);
          String titreCursor = cursor.getString(1);
          String localisationCursor= cursor.getString(2);
          String descriptionCursor = cursor.getString(3);
          String uri_pictureCursor = cursor.getString(4);
          String zip_nameCursor = cursor.getString(5);
          String parcours_in_listCursor = cursor.getString(6);
          String is_installed_cursor = cursor.getString(7);
          String numero_parcours_cursor = cursor.getString(8);
          String departementCursor = cursor.getString(9);
          String texteArret1Cursor = cursor.getString(10);
          String texteArret2Cursor = cursor.getString(11);
          String texteArret3Cursor = cursor.getString(12);
          String texteArret4Cursor = cursor.getString(13);
          String consigneArret1Cursor = cursor.getString(14);
          String consigneArret2Cursor = cursor.getString(15);
          String consigneArret3Cursor = cursor.getString(16);
          String consigneArret4Cursor = cursor.getString(17);

          key_idArray.add(key_idCursor);
          titreArray.add(titreCursor);
          localisationArray.add(localisationCursor);
          descriptionArray.add(descriptionCursor);
          uri_pictureArray.add(uri_pictureCursor);
          zip_nameArray.add(zip_nameCursor);
          parcours_In_ListArray.add(parcours_in_listCursor);
          is_InstalledArray.add(is_installed_cursor);
          numero_parcoursArray.add(numero_parcours_cursor);
          departementArray.add(departementCursor);
          texte_arret_1_Array.add(texteArret1Cursor);
          texte_arret_2_Array.add(texteArret2Cursor);
          texte_arret_3_Array.add(texteArret3Cursor);
          texte_arret_4_Array.add(texteArret4Cursor);
          consigne_arret_1_Array.add(consigneArret1Cursor);
          consigne_arret_2_Array.add(consigneArret2Cursor);
          consigne_arret_3_Array.add(consigneArret3Cursor);
          consigne_arret_4_Array.add(consigneArret4Cursor);

          // aller à la prochaine ligne
          cursor.moveToNext();
      }
      // pour compter le nombre de ligne, utile dans méthode main pour créer des vues de rendu en fonction
      countCursorLine = cursor.getCount();
      // libération des ressources occupées par le curseur.
      cursor.close();
  }


    // Pour obtenir le Cursor des éléments liés à la liste des parcours d'un parcours
    public void getOneCursorValues(String pNumeroParcours) {
        final String numeroParcours = pNumeroParcours;

        // récupération du cursor d'un parcours
        Cursor cursor = getParticularParcours(numeroParcours);
        // on se positionne sur la première ligne
        cursor.moveToFirst();
        // on vérfie qu'on a pas déjà atteint la dernière ligne

            // récupération des valeurs de la ligne sur laquelle on est positionné et à la colonne indiquée en index
            String key_idCursor = cursor.getString(0);
            String titreCursor = cursor.getString(1);
            String localisationCursor= cursor.getString(2);
            String descriptionCursor = cursor.getString(3);
            String uri_pictureCursor = cursor.getString(4);
            String zip_nameCursor = cursor.getString(5);
            String parcours_in_listCursor = cursor.getString(6);
            String is_installed_cursor = cursor.getString(7);
            String numero_parcours_cursor = cursor.getString(8);
            String departementCursor = cursor.getString(9);
            String texteArret1Cursor = cursor.getString(10);
            String texteArret2Cursor = cursor.getString(11);
            String texteArret3Cursor = cursor.getString(12);
            String texteArret4Cursor = cursor.getString(13);
            String consigneArret1Cursor = cursor.getString(14);
            String consigneArret2Cursor = cursor.getString(15);
            String consigneArret3Cursor = cursor.getString(16);
            String consigneArret4Cursor = cursor.getString(17);
            // to do

            key_id = key_idCursor;
            titre = titreCursor;
            localisation = localisationCursor;
            description = descriptionCursor;
            uri_picture = uri_pictureCursor;
            zip_name = zip_nameCursor;
            parcours_in_list = parcours_in_listCursor;
            is_installed = is_installed_cursor;
            numero_parcours = numero_parcours_cursor;
            departement = departementCursor;
            texte_arret_1 = texteArret1Cursor;
            texte_arret_2 = texteArret2Cursor;
            texte_arret_3 = texteArret3Cursor;
            texte_arret_4 = texteArret4Cursor;
            consigne_arret_1 = consigneArret1Cursor;
            consigne_arret_2 = consigneArret2Cursor;
            consigne_arret_3 = consigneArret3Cursor;
            consigne_arret_4 = consigneArret4Cursor;

        // pour compter le nombre de ligne, utile dans méthode main pour créer des vues de rendu en fonction
        countCursorLine = cursor.getCount();

        // libération des ressources occupées par le curseur.
        cursor.close();
}



    private Cursor getParticularParcours(String pNumeroParcours) {
        final String numeroParcours = pNumeroParcours;

        // Les colonnes que l'on souhaite extraire

        String[] result_columns = new String[] {
                KEY_ID, KEY_TITRE_PARCOURS, KEY_LOCALISATION, KEY_DESCRIPTION_PARCOURS,
                KEY_URI_PICTURE, KEY_ZIP_NAME, KEY_PARCOURS_IN_LIST, KEY_IS_INSTALLED, KEY_NUMERO_PARCOURS, KEY_DEPARTEMENT,
                KEY_TEXTE_ARRET_1, KEY_TEXTE_ARRET_2, KEY_TEXTE_ARRET_3, KEY_TEXTE_ARRET_4,
                KEY_CONSIGNE_ARRET_1, KEY_CONSIGNE_ARRET_2, KEY_CONSIGNE_ARRET_3, KEY_CONSIGNE_ARRET_4};

        // La clause Where doit être renseignée si l'on veut limiter les lignes
        String where = KEY_NUMERO_PARCOURS  + "=\"" + numeroParcours + "\"" ;

        //remplace éventuellement la clause where = KEY_ID + "=?"
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ParcoursDBOpenHelper.DATABASE_TABLE,
                result_columns, where,
                whereArgs, groupBy, having, order);
        //
        return cursor;
    }

    // récupération variable first_installation
    public String getFirstInstallation() {

        // Les colonnes que l'on souhaite extraire

        String[] result_columns = new String[] {
                KEY_FIRST_INSTALLATION};
        // les clauses sql pour limiter ou formater le rendu
        String where = null ;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        // récupération du cursor de la table Main
        SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ParcoursDBOpenHelper.DATABASE_TABLE_MAIN,
                result_columns, where,
                whereArgs, groupBy, having, order);

        // on se positionne sur la première ligne
        cursor.moveToFirst();

        // récupération des valeurs de la ligne sur laquelle on est positionné et à la colonne indiquée en index

        String key_first_installationCursor = cursor.getString(0);
        first_installation = key_first_installationCursor;

        // libération des ressources occupées par le curseur.
        cursor.close();
        return  first_installation;
    }

    // méthodes de récupération des données du tableau
    // test préalbale que le tableau n'est pas vide
    public String getKeyId(int i){
        if (key_idArray.size()==0)
            return null;
        return key_idArray.get(i);
    }
    public String getTitre(int i){
        if (titreArray.size()==0)
            return null;
        return titreArray.get(i);
    }
    public String getDescription(int i){
        if (descriptionArray.size()==0)
            return null;
        return descriptionArray.get(i);
    }
    public String getLocalisation(int i){
        if (localisationArray.size()==0)
            return null;
        else return localisationArray.get(i);
    }
    public String getUri_picture(int i){
        if (uri_pictureArray.size()==0)
            return null;
        return uri_pictureArray.get(i);
    }
    public String getZipName(int i){
        if (zip_nameArray.size()==0)
            return null;
        return zip_nameArray.get(i);
    }

    public  String getParcoursInList(int i) {
        if (parcours_In_ListArray.size()==0)
            return null;
        return  parcours_In_ListArray.get(i);
    }

    public  String getIsInstalled(int i) {
        if (is_InstalledArray.size()==0)
            return null;
        return  is_InstalledArray.get(i);
    }

    public  String getNumero_parcours(int i) {
        if (numero_parcoursArray.size()==0)
            return null;
        return  numero_parcoursArray.get(i);
    }

    public String getDepartement(int i) {
        if (numero_parcoursArray.size()==0)
            return null;
        return  departementArray.get(i);
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

    public int getCountCursorLine(){
             return countCursorLine;
      }


    // méthodes de récupération des données des variables quand un seul parcours est interrogé
    public String getOneKeyId(){
             return key_id;
    }
    public String getOneTitre(){
        return titre;
    }
    public String getOneDescription(){
        return description;
    }
    public String getOneLocalisation(){
       return localisation;
    }
    public String getOneUri_picture(){
        return uri_picture;
    }
    public String getOneZipName(){
        return zip_name;
    }
    public String getOneParcoursInList() {
        return  parcours_in_list;
    }
    public String getOneIsInstalled() {
        return  is_installed;
    }
    public String getOneNumeroParcours() {
        return  numero_parcours;
    }
    public String getOneDepartement() {
        return  departement;
    }
    public String getOneTexteArret1() {
        return texte_arret_1;
    }
    public String getOneTexteArret2() {
        return texte_arret_2;
    }
    public String getOneTexteArret3() {
        return texte_arret_3;
    }
    public String getOneTexteArret4() {
        return texte_arret_4;
    }
    public String getOneConsigneArret1() {
        return consigne_arret_1;
    }
    public String getOneConsigneArret2() {
        return consigne_arret_2;
    }
    public String getOneConsigneArret3() {
        return consigne_arret_3;
    }
    public String getOneConsigneArret4() {
        return consigne_arret_4;
    }


    // ajouter une entrées dans la base (ici nouveaux parcours)
  public void addNewListParcours(String parcoursTitre, String parcoursLocalisation, String parcoursDescription, String parcours_Uri_picture, String parcours_Zip_Name, String parcoursNumero, String parcoursDepartement) {
    System.out.println("debut addNewParcoursDebut");
    // Create a new row of values to insert.
    ContentValues newValues = new ContentValues();
    // Assign values for each row.
    newValues.put(KEY_TITRE_PARCOURS, parcoursTitre);
    newValues.put(KEY_LOCALISATION, parcoursLocalisation);
    newValues.put(KEY_DESCRIPTION_PARCOURS, parcoursDescription);
    newValues.put(KEY_URI_PICTURE, parcours_Uri_picture);
    newValues.put(KEY_ZIP_NAME, parcours_Zip_Name);
    newValues.put(KEY_PARCOURS_IN_LIST, "false");
    newValues.put(KEY_IS_INSTALLED, "false");
    newValues.put(KEY_NUMERO_PARCOURS, parcoursNumero);
    newValues.put(KEY_DEPARTEMENT, parcoursDepartement);
    // [ ... Repeat for each column / value pair ... ]

    // Insert the row into your table
    SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
    db.insert(ParcoursDBOpenHelper.DATABASE_TABLE, null, newValues);
      System.out.println("addNewParcoursFinish");
  }


    // mettre à jour les données d'un parcours
    public void updateParcoursValue(String pNumeroParcours, String pTexte_arret_1, String pTexte_arret_2, String pTexte_arret_3, String pTexte_arret_4,
                                    String pConsigne_arret_1, String pConsigne_arret_2, String pConsigne_arret_3, String pConsigne_arret_4) {

        // Create the updated row Content Values.
        ContentValues updatedValues = new ContentValues();

        // Assign values for each row.
        updatedValues.put(KEY_TEXTE_ARRET_1, pTexte_arret_1);
        updatedValues.put(KEY_TEXTE_ARRET_2, pTexte_arret_2);
        updatedValues.put(KEY_TEXTE_ARRET_3, pTexte_arret_3);
        updatedValues.put(KEY_TEXTE_ARRET_4, pTexte_arret_4);
        updatedValues.put(KEY_CONSIGNE_ARRET_1, pConsigne_arret_1);
        updatedValues.put(KEY_CONSIGNE_ARRET_2, pConsigne_arret_2);
        updatedValues.put(KEY_CONSIGNE_ARRET_3, pConsigne_arret_3);
        updatedValues.put(KEY_CONSIGNE_ARRET_4, pConsigne_arret_4);

        // [ ... Repeat for each column to update ... ]

        // Specify a where clause the defines which rows should be
        // updated. Specify where arguments as necessary.
        String where = KEY_NUMERO_PARCOURS + "=\"" + pNumeroParcours + "\"" ;
        String whereArgs[] = null;

        // Update the row with the specified index with the new values.
        SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
        db.update(ParcoursDBOpenHelper.DATABASE_TABLE, updatedValues,
                where, whereArgs);
    }


    public void updateParcoursListValue(int parcoursId, String newParcoursTitre, String newParcoursLocalisation, String newParcoursDescription, String newParcours_Uri_picture, String newParcours_Zip_Name, String newNumeroParcours, String newDepartement) {

    // Create the updated row Content Values.
    ContentValues updatedValues = new ContentValues();
  
    // Assign values for each row.
    updatedValues.put(KEY_TITRE_PARCOURS, newParcoursTitre);
    updatedValues.put(KEY_LOCALISATION, newParcoursLocalisation);
    updatedValues.put(KEY_DESCRIPTION_PARCOURS, newParcoursDescription);
    updatedValues.put(KEY_URI_PICTURE, newParcours_Uri_picture);
    updatedValues.put(KEY_ZIP_NAME, newParcours_Zip_Name);
    updatedValues.put(KEY_NUMERO_PARCOURS, newNumeroParcours);
    updatedValues.put(KEY_DEPARTEMENT, newDepartement);
    // [ ... Repeat for each column to update ... ]
  
    // Specify a where clause the defines which rows should be
    // updated. Specify where arguments as necessary.
    String where = KEY_NUMERO_PARCOURS  + "=\"" + newNumeroParcours + "\"";
    String whereArgs[] = null;
  
    // Update the row with the specified index with the new values.
    SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
    db.update(ParcoursDBOpenHelper.DATABASE_TABLE, updatedValues,
              where, whereArgs);
  }

    // utilisé pour mettre à true le champ d'une colonne de la BD
    public void updateParcoursInList(String pNumeroParcours, String value) {
        // Create the updated row Content Values.
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(KEY_PARCOURS_IN_LIST, value);
        // Specify a where clause the defines which rows should be
        // updated. Specify where arguments as necessary.
        String where = KEY_NUMERO_PARCOURS  + "=\"" + pNumeroParcours + "\"" ;
        String whereArgs[] = null;

        // Update the row with the specified index with the new values.
        SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
        db.update(ParcoursDBOpenHelper.DATABASE_TABLE, updatedValues,
                where, whereArgs);
    }

    // utilisé pour mettre à true le champ d'une colonne de la BD
    public void updateParcoursIsInstalled(String pNumeroParcours, String value) {
        // Create the updated row Content Values.
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(KEY_IS_INSTALLED, value);
        // Specify a where clause the defines which rows should be
        // updated. Specify where arguments as necessary.
        String where = KEY_NUMERO_PARCOURS  + "=\"" + pNumeroParcours + "\"";
        String whereArgs[] = null;

        // Update the row with the specified index with the new values.
        SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
        db.update(ParcoursDBOpenHelper.DATABASE_TABLE, updatedValues,
                where, whereArgs);
    }

    // utilisé pour mettre à 0 le champ fist_installation
    // lancé après le premier téléchargement de la liste des parcours après la première installation
    public void updateFirstInstallation() {
        // Create the updated row Content Values.
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(KEY_FIRST_INSTALLATION, "0");
        String where = null;
        String whereArgs[] = null;
        // Update the row with the specified index with the new values.
        SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
        db.update(ParcoursDBOpenHelper.DATABASE_TABLE_MAIN, updatedValues,
                where, whereArgs);
    }


   // pour effacer des lignes d'une table (ici toutes)
  public void deleteParcours() {
    // Specify a where clause that determines which row(s) to delete as necessary
    String where = null;
    String whereArgs[] = null;
  
    // Delete the rows that match the where clause.
    SQLiteDatabase db = parcoursDBOpenHelper.getWritableDatabase();
    db.delete(ParcoursDBOpenHelper.DATABASE_TABLE, where, whereArgs);
  }



    /**
   * Listing 8-2: Implementing an SQLite Open Helper
   */
  private static class ParcoursDBOpenHelper extends SQLiteOpenHelper {

    
    private static final String DATABASE_NAME = "ParcoursDatabase.db";
    private static final String DATABASE_TABLE = "Parcours";
    private static final String DATABASE_TABLE_MAIN = "Main";
    private static final int DATABASE_VERSION = 1;
    
    // SQL Statement to create the new table of parcours in database.
    private static final String DATABASE_CREATE = "create table " +
      DATABASE_TABLE + " (" + KEY_ID +
      " integer primary key autoincrement, " +
            KEY_TITRE_PARCOURS + " varchar(255), " +
            KEY_LOCALISATION + " varchar(255), " +
            KEY_DESCRIPTION_PARCOURS + " varchar(500), " +
            KEY_URI_PICTURE + " varchar(255), " +
            KEY_ZIP_NAME + " varchar(255), " +
            KEY_PARCOURS_IN_LIST + " varchar(255), " +
            KEY_IS_INSTALLED + " varchar(255), " +
            KEY_NUMERO_PARCOURS + " varchar(255), " +
            KEY_DEPARTEMENT + " varchar(255), " +
            KEY_TEXTE_ARRET_1 + " varchar(255), " +
            KEY_TEXTE_ARRET_2 + " varchar(255), " +
            KEY_TEXTE_ARRET_3 + " varchar(500), " +
            KEY_TEXTE_ARRET_4 + " varchar(255), " +
            KEY_CONSIGNE_ARRET_1 + " varchar(255), " +
            KEY_CONSIGNE_ARRET_2 + " varchar(255), " +
            KEY_CONSIGNE_ARRET_3 + " varchar(255), " +
            KEY_CONSIGNE_ARRET_4 + " varchar(255) " + ");";

      // SQL Statement to create the new table Main in database.
      // création de la valeur par défaut fist_installation à 1, on la passera à 0 après téléchargement des parcours
      private static final String DATABASE_CREATE_MAIN = "create table " +
              DATABASE_TABLE_MAIN + " (" + KEY_ID_MAIN +
              " integer primary key autoincrement, " +
              KEY_FIRST_INSTALLATION + " varchar(255) default \"1\"" + ");";


    public ParcoursDBOpenHelper(Context context, String name,
                                CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // création table Main
        db.execSQL(DATABASE_CREATE_MAIN);
        // création table Parcours
        db.execSQL(DATABASE_CREATE);
        // créer une ligne ds main
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put(KEY_FIRST_INSTALLATION, "1");
        db.insert(ParcoursDBOpenHelper.DATABASE_TABLE_MAIN, null, newValues);
    }


      // Called when there is a database version mismatch meaning that
    // the version of the database on disk needs to be upgraded to
    // the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                          int newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " +
        oldVersion + " to " +
        newVersion + ", which will destroy all old data");

      // Upgrade the existing database to conform to the new 
      // version. Multiple previous versions can be handled by 
      // comparing oldVersion and newVersion values.

      // The simplest case is to drop the old table and create a new one.
      db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
      // Create a new one.
      onCreate(db);
    }
  }
}