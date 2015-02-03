package fr.insensa.decouverte_patrimoine.android;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class SearchActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";
    private SimpleCursorAdapter searchAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("jérôme : search activity onCreate");

        // le layout que l'on va utiliser pour afficher les résultats
        setContentView(R.layout.list_search_parcours);

        // affichage du bouton de retour à l'activité principale (Main Activity)
         getActionBar().setDisplayHomeAsUpEnabled(true);


        // crée un adaptateur lié à la view
        // SimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags)
        searchAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, null,
                new String[] {MyContentProvider.KEY_TITRE_PARCOURS, MyContentProvider.KEY_DESCRIPTION_PARCOURS},
                new int [] {android.R.id.text1, android.R.id.text2 }, 0) ;

        // mets à jour la ListView avec les éléments de l'adaptateur
        setListAdapter(searchAdapter);

        // initialise le chargeur de curseur
        getLoaderManager().initLoader(0, null, this);

        // Récupérer les intents contenant requête de recherche
        parseIntent(getIntent());

        System.out.println("jérôme : search Activity end onCreate");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        System.out.println("jerome : onCreate Loader");
        String query="0";

        // extrait la requête de recherche des paramètres
        if (args != null) {
            query = args.getString(QUERY_EXTRA_KEY);
        }

        // construit la nouvelle requête sous forme de chargeur de curseur
        // c'est ici qu'on indique les colonnes à récupérer
        String[] projection = {
                MyContentProvider.KEY_ID,
                MyContentProvider.KEY_TITRE_PARCOURS,
                MyContentProvider.KEY_DESCRIPTION_PARCOURS,
                MyContentProvider.KEY_ZIP_NAME,
                MyContentProvider.KEY_NUMERO_PARCOURS
        };
        // ici on indique les restrictions
        String where = MyContentProvider.KEY_TITRE_PARCOURS + " LIKE \"%" + query + "%\"";
        String[] whereArgs = null;
        String sortOrder = MyContentProvider.KEY_TITRE_PARCOURS + " COLLATE LOCALIZED ASC";
        // crée le chargeur de curseur
        return new CursorLoader(this, MyContentProvider.CONTENT_URI, projection, where, whereArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // remplace le curseur affiché par l'adaptateur de curseur par le nouvel ensemble résultat

        searchAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Supprime de l'adaptateur le curseur existant

        searchAdapter.swapCursor(null);

    }


// ************************************ INTENT ******************************************************************

    // ici on récupère la valeur de la requête de recherche envoyée en Intent
    private void parseIntent(Intent intent) {

        if(Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            System.out.println("jerome : search intent");
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("jérôme : new intent");
        parseIntent(getIntent());
    }

    // recharge le LoaderManager avec les éléments demandés en query

    public void performSearch(String query) {
        System.out.println("jérôme : perform search");
        // passe la requête de recherche en paramètre au chargeur de curseur
        Bundle args = new Bundle();
        args.putString(QUERY_EXTRA_KEY, query);
        // Relance le chargeur de curseur pour éxécuter la nouvelle requête
        getLoaderManager().restartLoader(0, args, this);
    }



// ************************************ CLICKS UTILISATEURS *******************************************************

    // permet de réagir au click sur un des éléments de la liste
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // crée une Uri vers l'élément sélectionné qui pourra être récupéré dans un Cursor pour y lire les données
        // utilisé notament dans MainActivity => onCreate => getIntent

        Uri selectedUri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, id);

        // définition d'une intention à récupérer dans MainActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // affectation des données de l'Uri à l'Intent
        intent.setData(selectedUri);

        // lancement de l'activité lié à l'Intent : on retourne dans MainActivity
        startActivity(intent);
    }
}
