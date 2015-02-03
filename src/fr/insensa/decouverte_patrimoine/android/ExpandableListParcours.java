package fr.insensa.decouverte_patrimoine.android;

import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExpandableListParcours extends Fragment {

//Initialize variables
private static final String STR_CHECKED = " has Checked!";
private static final String STR_UNCHECKED = " has unChecked!";
private int ParentClickStatus=-1;
private int ChildClickStatus=-1;
private ArrayList<ParentListParcours> parentListParcoursArray;
private ArrayList<String> titreArray;
private ArrayList<String> descriptionArray;
private ArrayList<String> numeroParcoursArray;
private ArrayList<String> keyIdArray;
private ArrayList<String> uriArray;
private ArrayList<String> zipNameArray;
private ArrayList<String> departementArray;
private View fragmentView;
private ExpandableListView expandableListView;
private Drawable devider;
private Resources ressources;

private ProgressDialog mainProgressDialog;

// objet Json à lire contenant les données propres à chaque parcours
private HandleJsonParcours handleJsonParcours;

// constructeur par défaut
public ExpandableListParcours() {
    }

// utilisé dans procédure de téléchargement (telechargerZip)
private static BroadcastReceiver receiverZip;
// pour savoir si le receiverZip a été enregistré (dans telechargerZip)
private boolean receiverZipRegistrered;
private BroadcastReceiver.PendingResult resultBroadcastReceiverPending;


@Override
public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    Log.i("info", "oncreate ExpandableView");
    Bundle args = getArguments();
    uriArray = args.getStringArrayList("_URI");
    titreArray = args.getStringArrayList("_TITRE");
    descriptionArray = args.getStringArrayList("_DESCRIPTION");
    numeroParcoursArray = args.getStringArrayList("_NUMERO_PARCOURS");
    keyIdArray = args.getStringArrayList("_KEY_ID");
    zipNameArray = args.getStringArrayList("_ZIP_NAME");
    departementArray = args.getStringArrayList("_DEPARTEMENT");

    // aucun receiver (BroadcastReceiver) n'est enregistré au démarrage, il le sera dans
    // (dans telechargeAndDisplayParcours)
    receiverZipRegistrered=false;
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("info", "oncreateView ExpandableView");
        fragmentView = inflater.inflate(R.layout.fragment_list_parcours_main, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i("info", "onViewcreated ExpandableView");
        super.onViewCreated(view, savedInstanceState);
     //   Toast.makeText(getActivity().getApplicationContext(), titreArray.get(0), Toast.LENGTH_LONG).show();

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListViewParcours);

        ressources = this.getResources();
        devider = ressources.getDrawable(R.drawable.line);

        // Set ExpandableListView values
        expandableListView.setGroupIndicator(null);
        expandableListView.setDivider(devider);
        expandableListView.setChildDivider(devider);
        expandableListView.setDividerHeight(1);
        registerForContextMenu(expandableListView);

        //Creating static data in arraylist
        final ArrayList<ParentListParcours> parcoursParDepartement = tableauParcoursParDepartement();

        // Adding ArrayList data to ExpandableListView values
       loadHosts(parcoursParDepartement);
    }
@Override
    public void onPause() {
    super.onPause();
    Log.i("info", "onPause ExpandableView");
    // desenregistrement du BroadcatReceiver (obligatoire)
    // si il a été crée
    if (receiverZip!=null) {
        Log.i("ExpandableView onPause", "receiverZip not null");
        // si il a été enregistré et est actif (à vérifier !!!)
        if (receiverZipRegistrered) {
            Log.i("ExpandableView onPause", "unregisterReceiverZip");
            getActivity().unregisterReceiver(receiverZip);
            receiverZipRegistrered=false;
        }}
    }


// tableau contenant la liste des parcours organisés sous forme de Parent(département) et enfants (liste parcours)

private ArrayList<ParentListParcours> tableauParcoursParDepartement()    {
    // tabelau de la liste des départements contenant la liste des parcours
    final ArrayList<ParentListParcours> listParentListParcourses = new ArrayList<ParentListParcours>();

    // pour le premier parcours, création d'office d'un nouveau parent (liste départementale)
    final ParentListParcours parentListParcoursInit = new ParentListParcours();
    parentListParcoursInit.setName(departementArray.get(0));
    parentListParcoursInit.setChildListParcours(new ArrayList<ChildListParcours>());

    // création d'office du premier parcours
    final ChildListParcours childListParcoursInit = new ChildListParcours();
    childListParcoursInit.setTitre(titreArray.get(0));
    childListParcoursInit.setDescription(descriptionArray.get(0));
    childListParcoursInit.setDepartement(departementArray.get(0));
    childListParcoursInit.setKeyId(keyIdArray.get(0));
    childListParcoursInit.setNumeroParcours(numeroParcoursArray.get(0));
    childListParcoursInit.setUri(uriArray.get(0));
    childListParcoursInit.setZipName(zipNameArray.get(0));
    // ajout du parcours à la liste départementale
    parentListParcoursInit.getChildListParcours().add(childListParcoursInit);

    // Ajout du parent créé à la liste des parents
    listParentListParcourses.add(parentListParcoursInit);

    // pour le second parcours et les suivants
    // on parcourt la liste des parcours renvoyés par l'intent
    for (int i = 1; i < keyIdArray.size(); i++) {
       // parcours de la nouvelle liste des parcours ordonnés par département
       for (int ii=0; ii< listParentListParcourses.size(); ii++) {
            // si ce nom de département est déjà présent dans la liste des parents :
            if (departementArray.get(i).equals(listParentListParcourses.get(ii).getName())) {
                // => créer l'enfant, attribuer nom, etc... et l'affecter au parent
                    final ChildListParcours childListParcours = new ChildListParcours();
                    childListParcours.setTitre(titreArray.get(i));
                    childListParcours.setDescription(descriptionArray.get(i));
                    childListParcours.setDepartement(departementArray.get(i));
                    childListParcours.setKeyId(keyIdArray.get(i));
                    childListParcours.setNumeroParcours(numeroParcoursArray.get(i));
                    childListParcours.setUri(uriArray.get(i));
                    childListParcours.setZipName(zipNameArray.get(i));
                    listParentListParcourses.get(ii).getChildListParcours().add(childListParcours);
                    break;
                }

                // si ce nom n'est pas présent dans la liste des parents :
                // => créer un nouveau parent, puis un nouveau enfant, etc... et l'affecter à la liste des parents
                // ajouter le nouveau parent à la liste des parents
                else if (!departementArray.get(i).equals(listParentListParcourses.get(ii).getName()) && ii== listParentListParcourses.size()-1) {
                    //Create parent class object
                    final ParentListParcours parentListParcours = new ParentListParcours();

                    // Set values in parent class object

                    parentListParcours.setName(departementArray.get(i));
                    parentListParcours.setChildListParcours(new ArrayList<ChildListParcours>());

                    final ChildListParcours childListParcours = new ChildListParcours();
                    childListParcours.setTitre(titreArray.get(i));
                    childListParcours.setDescription(descriptionArray.get(i));
                    childListParcours.setDepartement(departementArray.get(i));
                    childListParcours.setKeyId(keyIdArray.get(i));
                    childListParcours.setNumeroParcours(numeroParcoursArray.get(i));
                    childListParcours.setUri(uriArray.get(i));
                    childListParcours.setZipName(zipNameArray.get(i));
                    parentListParcours.getChildListParcours().add(childListParcours);

                    //Ajout de la liste départementale à la liste par départements
                    listParentListParcourses.add(parentListParcours);
                    break;
                }
        }
    }
    return listParentListParcourses;
}

// Adding ArrayList data to ExpandableListView values
private void loadHosts(final ArrayList<ParentListParcours> newParentListParcourses)
{
    if (newParentListParcourses == null)
        return;

    parentListParcoursArray = newParentListParcourses;
    expandableListView.setAdapter(new MyExpandableListAdapter());
    expandableListView.setGroupIndicator(null);

   /* // Check for ExpandableListAdapter object
    if (getActivity().getExpandableListAdapter() == null)
    {
        //Create ExpandableListAdapter Object
        final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();

        // Set Adapter to ExpandableList Adapter
        this.setListAdapter(mAdapter);
    }
    else
    {
        // Refresh ExpandableListView data
        ((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
    }*/
}


/**
 * A Custom adapter to create Parent view (Used parent_list_parcours_grouprow.xmlparcours_grouprow.xml) and Child View((Used child_list_parcours_row.xmlarcours_row.xml).
 */
private class MyExpandableListAdapter extends BaseExpandableListAdapter {

    // récupération de différentes variables qui permettront de construire le chemin d'accès aux fichiers externes privés de l'application
    File pathData = Environment.getDataDirectory();
    File pathExtStor = Environment.getExternalStorageDirectory();
    String packageName = getActivity().getPackageName();

    private LayoutInflater inflater;


    public MyExpandableListAdapter()
    {
        // Create Layout Inflator
        inflater = LayoutInflater.from(getActivity());

    }


    // This Function used to inflate parent rows view

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView)
    {
        final ParentListParcours parentListParcours = ExpandableListParcours.this.parentListParcoursArray.get(groupPosition);

        // Inflate parent_list_parcours_grouprowist_parcours_grouprow.xml file for parent rows

        convertView = inflater.inflate(R.layout.parent_list_parcours_grouprow, parentView, false);

        // Get parent_list_parcours_grouprow.xmlparcours_grouprow.xml file elements and set values
        ((TextView) convertView.findViewById(R.id.textName)).setText(parentListParcours.getName());

        // à utiliserpour mettre éventuellement une image propre à chaque département
        //ImageView image=(ImageView)convertView.findViewById(R.id.image);

       // ImageView rightcheck=(ImageView)convertView.findViewById(R.id.rightcheck);

        //Log.i("onCheckedChanged", "isChecked: "+parent.isChecked());

        // Change right check image on parent at runtime
      /*  if(parentListParcoursArray.isChecked()==true){
            rightcheck.setImageResource(
                    getResources().getIdentifier(
                            "com.androidexample.customexpandablelist:drawable/rightcheck",null,null));
        }
        else{
            rightcheck.setImageResource(
                    getResources().getIdentifier(
                            "com.androidexample.customexpandablelist:drawable/button_check",null,null));
        }*/

        // Get parent_list_parcours_grouprowist_parcours_grouprow.xml file checkbox elements
       // CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
       // checkbox.setChecked(parentListParcoursArray.isChecked());

        // Set CheckUpdateListener for CheckBox (see below CheckUpdateListener class)
        //checkbox.setOnCheckedChangeListener(new CheckUpdateListener(parentListParcoursArray));
        return convertView;
    }


    // This Function used to inflate child rows view
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parentView)
    {
        final ParentListParcours parentListParcours = ExpandableListParcours.this.parentListParcoursArray.get(groupPosition);
        final ChildListParcours childListParcours = parentListParcours.getChildListParcours().get(childPosition);

        // Inflate child_list_parcours_row.xmlarcours_row.xml file for child rows
        convertView = inflater.inflate(R.layout.child_list_parcours_row, parentView, false);

        // Get child_list_parcours_rowst_parcours_row.xml file elements and set values
        TextView description = (TextView) convertView.findViewById(R.id.description_parcours);
        description.setText(childListParcours.getDescription());
        TextView titre = (TextView) convertView.findViewById(R.id.name_parcours);
        titre.setText(childListParcours.getTitre());
        ImageView image=(ImageView)convertView.findViewById(R.id.image);

         Uri uri = Uri.parse(pathExtStor.getAbsolutePath() + "/Android" + pathData.getAbsolutePath() + "/"
                + packageName + "/files/icone_parcours/" + childListParcours.getUri());
        image.setImageURI(uri);

        // lancement téléchargement zip si on clique sur l'image
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String is_installed;
                ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getActivity().getApplicationContext());
                parcoursDataBase.getOneCursorValues(childListParcours.getNumeroParcours());
                is_installed = parcoursDataBase.getOneIsInstalled();
                parcoursDataBase.closeDatabase();

                // test si ZIP parcours déjà installé, auquel cas on lance le parcours direcetement
                if (is_installed.contentEquals("true")) {
                    MainActivity.setNumero_parcours_main(childListParcours.getNumeroParcours());
                    // lancer le parcours
                    System.out.println("parcours déjà installé : lancement du parcours !");
                    Toast.makeText(getActivity().getApplicationContext(), "lancement du jeu", Toast.LENGTH_LONG).show();
                    Intent startCircuit = new Intent(getActivity().getApplicationContext(), CircuitActivity.class);
                    startActivity(startCircuit);
                } else {
                    // télécharger et lancer le jeu quand téléchargement terminé
                  //  Toast.makeText(getActivity().getApplicationContext(), "téléchargemenr ZIP", Toast.LENGTH_LONG).show();
                    MainActivity.setNumero_parcours_main(childListParcours.getNumeroParcours());
                    telechargerZip(childListParcours.getZipName(), childListParcours.getKeyId(), childListParcours.getNumeroParcours());
                }
            }
        });
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String is_installed;
                ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getActivity().getApplicationContext());
                parcoursDataBase.getOneCursorValues(childListParcours.getNumeroParcours());
                is_installed = parcoursDataBase.getOneIsInstalled();
                parcoursDataBase.closeDatabase();

                // test si ZIP parcours déjà installé, auquel cas on lance le parcours direcetement
                if (is_installed.contentEquals("true")) {
                    MainActivity.setNumero_parcours_main(childListParcours.getNumeroParcours());
                    // lancer le parcours
                    System.out.println("parcours déjà installé : lancement du parcours !");
                    Toast.makeText(getActivity().getApplicationContext(), "lancement du jeu", Toast.LENGTH_LONG).show();
                    Intent startCircuit = new Intent(getActivity().getApplicationContext(), CircuitActivity.class);
                    startActivity(startCircuit);
                } else {
                    // télécharger et lancer le jeu quand téléchargement terminé
                   // Toast.makeText(getActivity().getApplicationContext(), "téléchargemenr ZIP", Toast.LENGTH_LONG).show();
                    MainActivity.setNumero_parcours_main(childListParcours.getNumeroParcours());
                    telechargerZip(childListParcours.getZipName(), childListParcours.getKeyId(), childListParcours.getNumeroParcours());
                }
            }
        });
        titre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String is_installed;
                ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getActivity().getApplicationContext());
                parcoursDataBase.getOneCursorValues(childListParcours.getNumeroParcours());
                is_installed = parcoursDataBase.getOneIsInstalled();
                parcoursDataBase.closeDatabase();

                // test si ZIP parcours déjà installé, auquel cas on lance le parcours direcetement
                if (is_installed.contentEquals("true")) {
                    MainActivity.setNumero_parcours_main(childListParcours.getNumeroParcours());
                    // lancer le parcours
                    System.out.println("parcours déjà installé : lancement du parcours !");
                    Toast.makeText(getActivity().getApplicationContext(), "lancement du jeu", Toast.LENGTH_LONG).show();
                    Intent startCircuit = new Intent(getActivity().getApplicationContext(), CircuitActivity.class);
                    startActivity(startCircuit);
                } else {
                    // télécharger et lancer le jeu quand téléchargement terminé
                //    Toast.makeText(getActivity().getApplicationContext(), "téléchargemenr ZIP", Toast.LENGTH_LONG).show();
                    MainActivity.setNumero_parcours_main(childListParcours.getNumeroParcours());
                    telechargerZip(childListParcours.getZipName(), childListParcours.getKeyId(), childListParcours.getNumeroParcours());
                }
            }
        });

        return convertView;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
        return parentListParcoursArray.get(groupPosition).getChildListParcours().get(childPosition);
    }

    //Call when child row clicked
    @Override
    public long getChildId(int groupPosition, final int childPosition)
    {
        /****** When Child row clicked then this function call *******/

        //Log.i("Noise", "parent == "+groupPosition+"=  child : =="+childPosition);
        if( ChildClickStatus!=childPosition)
        {
            ChildClickStatus = childPosition;

        //    Toast.makeText(getActivity().getApplicationContext(), "Parent :"+groupPosition + " Child :"+childPosition ,
        //            Toast.LENGTH_LONG).show();

        }

        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        int size=0;
        if(parentListParcoursArray.get(groupPosition).getChildListParcours()!=null)
            size = parentListParcoursArray.get(groupPosition).getChildListParcours().size();
        return size;
    }


    @Override
    public Object getGroup(int groupPosition)
    {
        Log.i("Parent", groupPosition+"=  getGroup ");

        return parentListParcoursArray.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return parentListParcoursArray.size();
    }

    //Call when parent row clicked
    @Override
    public long getGroupId(int groupPosition)
    {
        Log.i("Parent", groupPosition+"=  getGroupId "+ParentClickStatus);

        if(groupPosition==2 && ParentClickStatus!=groupPosition){

            //Alert to user
          //  Toast.makeText(getActivity().getApplicationContext(), "Parent :"+groupPosition ,
          //          Toast.LENGTH_LONG).show();
        }

        ParentClickStatus=groupPosition;
        if(ParentClickStatus==0)
            ParentClickStatus=-1;

        return groupPosition;
    }

    @Override
    public void notifyDataSetChanged()
    {
        // Refresh List rows
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty()
    {
        return ((parentListParcoursArray == null) || parentListParcoursArray.isEmpty());
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }



    /******************* Checkbox Checked Change Listener ********************/

    private final class CheckUpdateListener implements OnCheckedChangeListener
    {
        private final ParentListParcours parentListParcours;

        private CheckUpdateListener(ParentListParcours parentListParcours)
        {
            this.parentListParcours = parentListParcours;
        }
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            Log.i("onCheckedChanged", "isChecked: "+isChecked);
            parentListParcours.setChecked(isChecked);

          //  ((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();

            final Boolean checked = parentListParcours.isChecked();
            Toast.makeText(getActivity().getApplicationContext(),
                    "Parent : "+ parentListParcours.getName() + " " + (checked ? STR_CHECKED : STR_UNCHECKED),
                    Toast.LENGTH_LONG).show();
        }
    }
    /***********************************************************************/

    }
    // télécharge le fichier zip du parcours, le dézippe (lance unzip), puis lance le parcours quand téléchargement terminé
    private void telechargerZip(String zipName, String pKeyId, String pNumeroParcours) {
        new TelechargerZipTask(zipName,pKeyId, pNumeroParcours).execute((Void[]) null);
    }


    private class TelechargerZipTask extends AsyncTask<Void, Void, Void> {
        final String zipName;
        final  String keyId ;
        final String numeroParcours;
        final Uri uri;
        final String serviceString;
        final DownloadManager downloadManagerZip;
        final DownloadManager.Request request;


        public TelechargerZipTask(String pZipName, String pKeyId, String pNumeroParcours) {
            zipName = pZipName;
            keyId = pKeyId;
            numeroParcours = pNumeroParcours;
            uri = Uri.parse("http://insensa.fr/appli_decouverte/" + zipName);
            serviceString = Context.DOWNLOAD_SERVICE;
            downloadManagerZip = (DownloadManager)getActivity().getSystemService(serviceString);
            request = new DownloadManager.Request(uri);

            // précise que les fichiers téléchargés seront stockés dans le dossier numeroParcoursArray du dossier externe de l'application avec le nom zipNameArray
            request.setDestinationInExternalFilesDir(getActivity().getApplicationContext(), numeroParcours, zipName);
        }

        @Override
        protected void onPreExecute() {
            mainProgressDialog = ProgressDialog.show(getActivity().getCurrentFocus().getContext(), "Informations", "chargement du parcours", true, true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // création d'une référence pour pouvoir interroger l'état, etc... du téléchargement et lancement du téléchargement (enqueue)
            final long downloadReferenceZip = downloadManagerZip.enqueue(request);
            // surveiller la fin du téléchargement
            IntentFilter filterZip = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            // final BroadcastReceiver receiverZip = new BroadcastReceiver() {
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
                            int count = myDonwloadZip.getCount();
                            // System.out.println("jerome " + " : téléchargement zip terminé : lignes : " + count);
                            // récupération du nom et de l'URI du fichier
                            int fileNameIdx = myDonwloadZip.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                            String fileName = myDonwloadZip.getString(fileNameIdx);
                            // changement dans la BD pour indiquer que le parcours est installé
                            ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getActivity().getApplicationContext());
                            parcoursDataBase.updateParcoursIsInstalled(numeroParcours, "true");
                            parcoursDataBase.closeDatabase();
                            mainProgressDialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), "Téléchargement du parcours réussi", Toast.LENGTH_SHORT).show();
                            // desenregistrement du BroadcatReceiver (obligatoire)
                            // si il a été crée
                            if (receiverZip!=null) {
                                // si il a été enregistré et est actif (à vérifier !!!)
                                if (receiverZipRegistrered) {
                                    getActivity().unregisterReceiver(receiverZip);
                                    receiverZipRegistrered=false;
                                }}
                                // dézippe le fichier telechargé
                            try {
                                unzip(fileName, numeroParcours);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // fermeture du Cursor
                        myDonwloadZip.close();
                    }
                }
            };
            getActivity().registerReceiver(receiverZip, filterZip);
            Log.i("info", "ExpandableView telechargerZip register receiverZip");
            receiverZipRegistrered=true;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }



    // dezippage d'un parcours
    private void unzip(String pzipFile, String pNumeroParcours) throws IOException{
        String numeroParcours = pNumeroParcours;
        String location = Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/" + getActivity().getPackageName() + "/files/" + numeroParcours + "/";
        int size;
        byte[] buffer = new byte[1024];
        File f = new File(location);
        System.out.println("jerome" + "début unzip");
        String zipFile = pzipFile;

        System.out.println("jerome" + zipFile);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream((new FileInputStream(zipFile))));
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
        }
        finally {
            zis.close();
            System.out.println("jerome " + " dézippage terminé, lancement du jeu");
            // récupérer son Json et inscrire les valeurs dans la BD
            writeJsonParcoursInDataBase(numeroParcours);
            // lancer le jeu
            Toast.makeText(getActivity().getApplicationContext(), "lancement du jeu", Toast.LENGTH_LONG).show();
            Intent startCircuit = new Intent(getActivity().getApplicationContext(), CircuitActivity.class);
            startActivity(startCircuit);
        }
    }

    // pour lire document json des données propres au parcours et inscrire valeurs dans la BD
    private void writeJsonParcoursInDataBase(String pNumeroParcours){
      //  final String numeroParcours = pNumeroParcours;
        // teste si le parcours est déjà dans la BD
        // boolean success = false;
        String location = Environment.getExternalStorageDirectory() + "/Android" + Environment.getDataDirectory() + "/" + getActivity().getPackageName()
                + "/files/" + pNumeroParcours + "/"+ pNumeroParcours + ".json";
        handleJsonParcours = new HandleJsonParcours(location);
        // lecture du Json et création du tableau
        handleJsonParcours.fetchJSON();
        ParcoursDataBase parcoursDataBase = new ParcoursDataBase(getActivity().getApplicationContext());
        while (handleJsonParcours.getParsingComplete()) {
            //   System.out.println("parsing not complete" + handleJsonParcours.getSizeArray());
            // quand la lecture du Json est terminé (le Json n'est pas vide)
            if (handleJsonParcours.getSizeArray() != 0) {
                // parcours du tableau du json
                for (int i = 0; i < handleJsonParcours.getSizeArray(); i++) {
                    parcoursDataBase.updateParcoursValue(pNumeroParcours, handleJsonParcours.getTexteArret1(i), handleJsonParcours.getTexteArret2(i),
                            handleJsonParcours.getTexteArret3(i), handleJsonParcours.getTexteArret4(i), handleJsonParcours.getConsigneArret1(i),
                            handleJsonParcours.getConsigneArret2(i), handleJsonParcours.getConsigneArret3(i), handleJsonParcours.getConsigneArret4(i));
                }
            }
        }
        parcoursDataBase.closeDatabase();
    }
}