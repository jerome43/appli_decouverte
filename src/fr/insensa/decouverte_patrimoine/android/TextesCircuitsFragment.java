package fr.insensa.decouverte_patrimoine.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Jérôme on 20/01/2015.
 */
public class TextesCircuitsFragment extends Fragment {
    private ScrollView layout;
    private TextView textEtapeView;
    private ImageView imageEtapeView;

    public TextesCircuitsFragment() {
}
// interface qui permettra d'accéder aux infos du fragment depuis l'activité contenant le fragment
    // cette activité doit donc implémenter l'interface TextCircuitFragmentCallBack

    public interface TextCircuitFragmentCallBack {
        // une information est produite dans le Fragment
        // En tant que CallBack du fragment, on souhaite la récupérer dans l'activité
        // vous devriez faire quelque chose avec cette information
        public void getTextView(TextView textView);
        public void getImageView(ImageView imageView);
    }

    // La ou les activité(s) parente(s) du callback (en fait toute activité implémentant TextCircuitFragmentCallBack
    private TextCircuitFragmentCallBack parent;

    // Appelé en premier
    @Override
    public void onAttach(Activity activity) {
        Log.w("MainFragmentHC", "onAttach called");
        super.onAttach(activity);
        //Utiliser cette méthode pour lier votre fragment avec son callback
        parent = (TextCircuitFragmentCallBack) activity;
    }

    // méthodes qui vont notifier le changement à l'activité
    // que si on les appelle
    // ici on va les appeler dont onCreatView

    public void getTextView(TextView textView) {
        //Notifiez le parent qu'un item a été sélectionné
        parent.getTextView(textView);
        //Faîtes d'autres traitements ici au besoin
    }
    public void getImageView(ImageView imageView) {
        //Notifiez le parent qu'un item a été sélectionné
        parent.getImageView(imageView);
        //Faîtes d'autres traitements ici au besoin
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the Fragment.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (ScrollView) inflater.inflate(R.layout.fragment_circuit_layout_texte, container, false);
        textEtapeView = (TextView) layout.findViewById(R.id.textIntroCircuitView);
        // on met à jour les infos dans l'activité principale
        getTextView(textEtapeView);
        imageEtapeView = (ImageView)layout.findViewById(R.id.imageAccueilCircuitView);
        getImageView(imageEtapeView);
       // textEtapeView.setText(CircuitActivity.getTextesEtape());
        textEtapeView.setText("Commençons par la considération des choses les plus communes, et que nous croyons comprendre le plus distinctement, à savoir les corps que nous touchons et que nous voyons. Je n’entends pas parler des corps en général, car ces notions générales sont d’ordinaire plus confuses, mais de quelqu’un en particulier. Prenons pour exemple ce morceau de cire qui vient d’être tiré de la ruche : il n’a pas encore perdu la douceur du miel qu’il contenait, il retient encore quelque chose de l’odeur des fleurs dont il a été recueilli; sa couleur, sa figure, sa grandeur, sont apparentes; il est dur, il est froid, on le touche, et si vous le frappez, il rendra quelque son.\n" +
                "Qu’est-ce maintenant que cette extension ? N’est-elle pas aussi inconnue, puisque dans la cire qui se fond elle augmente, et se trouve encore plus grande quand elle est entièrement fondue, et beaucoup plus encore quand la chaleur augmente davantage ? Et je ne concevrais pas clairement et selon la vérité ce que c’est que la cire, si je ne pensais qu’elle est capable de recevoir plus de variétés selon l’extension, que je n’en ai jamais imaginé. Il faut donc que je tombe d’accord, que je ne saurais pas même concevoir par l’imagination ce que c’est que cette cire, et qu’il n’y a que mon entendement seul qui le conçoive; je dis ce morceau de cire en particulier, car pour la cire en général, il est encore plus évident.\n" +
                "Commençons par la considération des choses les plus communes, et que nous croyons comprendre le plus distinctement, à savoir les corps que nous touchons et que nous voyons. Je n’entends pas parler des corps en général, car ces notions générales sont d’ordinaire plus confuses, mais de quelqu’un en particulier. Prenons pour exemple ce morceau de cire qui vient d’être tiré de la ruche : il n’a pas encore perdu la douceur du miel qu’il contenait, il retient encore quelque chose de l’odeur des fleurs dont il a été recueilli; sa couleur, sa figure, sa grandeur, sont apparentes; il est dur, il est froid, on le touche, et si vous le frappez, il rendra quelque son.\n" +
                "Commençons par la considération des choses les plus communes, et que nous croyons comprendre le plus distinctement, à savoir les corps que nous touchons et que nous voyons. Je n’entends pas parler des corps en général, car ces notions générales sont d’ordinaire plus confuses, mais de quelqu’un en particulier. Prenons pour exemple ce morceau de cire qui vient d’être tiré de la ruche : il n’a pas encore perdu la douceur du miel qu’il contenait, il retient encore quelque chose de l’odeur des fleurs dont il a été recueilli; sa couleur, sa figure, sa grandeur, sont apparentes; il est dur, il est froid, on le touche, et si vous le frappez, il rendra quelque son.\n" +
                "Qu’est-ce maintenant que cette extension ? N’est-elle pas aussi inconnue, puisque dans la cire qui se fond elle augmente, et se trouve encore plus grande quand elle est entièrement fondue, et beaucoup plus encore quand la chaleur augmente davantage ? Et je ne concevrais pas clairement et selon la vérité ce que c’est que la cire, si je ne pensais qu’elle est capable de recevoir plus de variétés selon l’extension, que je n’en ai jamais imaginé. Il faut donc que je tombe d’accord, que je ne saurais pas même concevoir par l’imagination ce que c’est que cette cire, et qu’il n’y a que mon entendement seul qui le conçoive; je dis ce morceau de cire en particulier, car pour la cire en général, il est encore plus évident.\n" +
                "Certes ce ne peut être rien de tout ce que j’y ai remarqué par l’entremise des sens, puisque toutes les choses qui tombaient sous le goût, ou l’odorat, ou la vue, ou l’attouchement, ou l’ouïe, se trouvent changées, et cependant la même cire demeure. Peut-être était-ce ce que je pense maintenant, à savoir que la cire n’était pas ni cette douceur du miel, ni cette agréable odeur des fleurs, ni cette blancheur, ni cette figure, ni ce son, mais seulement un corps qui un peu auparavant me paraissait sous ces formes, et qui maintenant se fait remarquer sous d’autres. Mais qu’est-ce, précisément parlant, que j’imagine, lorsque je la conçois en cette sorte ?\n" +
                "Or quelle est cette cire, qui ne peut être conçue que par l’entendement ou l’esprit ? Certes c’est la même que je vois, que je touche, que j’imagine, et la même que je connaissais dès le commencement. Mais ce qui est à remarquer, sa perception, ou bien l’action par laquelle on l’aperçoit, n’est point une vision, ni un attouchement, ni une imagination, et ne l’a jamais été, quoiqu’il le semblât ainsi auparavant, mais seulement une inspection de l’esprit, laquelle peut être imparfaite et confuse, comme elle était auparavant, ou bien claire et distincte, comme elle est à présent, selon que mon attention se porte plus ou moins aux choses qui sont en elle, et dont elle est composée.\n" +
                "Or quelle est cette cire, qui ne peut être conçue que par l’entendement ou l’esprit ? Certes c’est la même que je vois, que je touche, que j’imagine, et la même que je connaissais dès le commencement. Mais ce qui est à remarquer, sa perception, ou bien l’action par laquelle on l’aperçoit, n’est point une vision, ni un attouchement, ni une imagination, et ne l’a jamais été, quoiqu’il le semblât ainsi auparavant, mais seulement une inspection de l’esprit, laquelle peut être imparfaite et confuse, comme elle était auparavant, ou bien claire et distincte, comme elle est à présent, selon que mon attention se porte plus ou moins aux choses qui sont en elle, et dont elle est composée.\n" +
                "Enfin toutes les choses qui peuvent distinctement faire connaître un corps, se rencontrent en celui-ci. Mais voici que, cependant que je parle, on l’approche du feu : ce qui y restait de saveur s’exhale, l’odeur s’évanouit, sa couleur se change, sa figure se perd, sa grandeur augmente, il devient liquide, il s’échauffe, à peine le peut-on toucher, et quoiqu’on le frappe, il ne rendra plus aucun son. La même cire demeure-t-elle après ce changement ? Il faut avouer qu’elle demeure; et personne ne le peut nier. Qu’est-ce donc que l’on connaissait en ce morceau de cire avec tant de distinction ?\n" +
                "Considérons-le attentivement, et éloignant toutes les choses qui n’appartiennent point à la cire, voyons ce qui reste. Certes il ne demeure rien que quelque chose d’étendu, de flexible et de muable. Or qu’est-ce que cela : flexible et muable ? N’est-ce pas que j’imagine que cette cire étant ronde est capable de devenir carrée, et de passer du carré en une figure triangulaire ? Non certes, ce n’est pas cela, puisque je la conçois capable de recevoir une infinité de semblables changements, et je ne saurais néanmoins parcourir cette infinité par mon imagination, et par conséquent cette conception que j’ai de la cire ne s’accomplit pas par la faculté d’imaginer.");
        imageEtapeView.setImageURI(CircuitActivity.getImageEtapeUri());
        return  layout;
    }

}
