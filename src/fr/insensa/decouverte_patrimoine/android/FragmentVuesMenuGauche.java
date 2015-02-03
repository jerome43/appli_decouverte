package fr.insensa.decouverte_patrimoine.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


// ******************* GESTION DES FRAGMENTS ******************************************************

//fragment qui permet la mise à jour du contenu du layout fragment_main en fonction section sélectionnée dans DrawerMenu

public class FragmentVuesMenuGauche extends Fragment {

    // The fragment argument representing the section number for this fragment
    private static final String ARG_SECTION_NUMBER = "section_number";
    // Returns a new instance of this fragment for the given section number
    public static FragmentVuesMenuGauche newInstance(int sectionNumber) {
        FragmentVuesMenuGauche fragment = new FragmentVuesMenuGauche();
        // Bundle permet de sauvegarder des informations liées à l'activité et de les récupérer avec la méthode savedInstanceState appelée à OncReate
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public FragmentVuesMenuGauche() {
    }
    // Dans onCreate, on instancie les objets non graphiques ;
    // Dans onCreateView, on instancie la vue et les composants graphiques ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // instanciation des objets View
        // conteneur principal (ViewGroup)
        RelativeLayout layout = null;
        // un TextView particulier
        TextView text = null;
        String textesPages[] = {getString(R.string.contenu_page1), getString(R.string.contenu_page2), getString(R.string.contenu_page3)};
        // pour désérialiseer (récupérer en java le xml) l'arbre de vue xml complet du fragment
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);
        // on récupère le TextView en question dans l'arbre de vue
        text = (TextView) layout.findViewById(R.id.section_label);
        // on le met à jour
        text.setText(textesPages[MainActivity.getPositionPage()]);
        // on retourne le ViewGroup qui sera mis à jour par les fonctions parentes
        return layout;
    }

    // Dans onAttach, on récupère un pointeur vers l'activité contenante
    //To allow a Fragment to communicate up to its Activity, you can define an interface in the Fragment class and implement it within the Activity.
    // The Fragment captures the interface implementation during its onAttach() lifecycle method and can then call the Interface methods in order to communicate with the Activity.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    // si on veut sauvegarder des valeurs précises à restaurer après arrêts, mise en arrière plan...
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
