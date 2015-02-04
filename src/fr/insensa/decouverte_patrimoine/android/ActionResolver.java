package fr.insensa.decouverte_patrimoine.android;

/*
Utilisé dans les Jeux Libgdx, permet d'y inclure des éléments natifs android (Toast, dialog..)
To call native Android elements from Libgdx, you need a callback interface,
 which will be referenced in both the ApplicationListener on the Libgdx side, and the AndroidApplication on the Android side.
Libgdx Side with the ApplicationListener
   * Create your interface (Called "ActionResolver" in this Wiki's code example). Give it methods that you will call within the Libgdx ApplicationListener, that will be implemented in the AndroidApplication.
   * Create a constructor in your ApplicationListener that takes the interface as a parameter. Save a reference to this parameter in your ApplicationListener, so you can use it to call its methods (which run on the Android side). Make it public or pass it to your screens (or whatever will need to call native android elements)

Android side with the AndroidApplication
   * Implement the interface. Either have the AndroidApplication implement it or create a new class that does, such as the ActionResolverAndroid class in this wiki's code example.
  *  Pass the interface to the ApplicationListener when initializing it, using the constructor you made that takes the interface as a parameter. This part happens in the AndroidApplication's onCreate(..) method, before the initialize(..) call.
*/

public interface ActionResolver {
    public static final int VIEW_TEST = 1;
    public void showShortToast(CharSequence toastMessage);
    public void showLongToast(CharSequence toastMessage);
    public void showAlertBox(String alertBoxTitle, String alertBoxMessage, String alertBoxButtonText);
    public void openUri(String uri);
    public void showView(final int view);
    public void hideView(final int view);
    public void essai();
}
