package fr.insensa.decouverte_patrimoine.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;

// Utilisé dans les Jeux Libgdx, permet d'y inclure des éléments natifs android (Toast, dialog..)
// voir commentaires dans l'interface ActionResolver


public class ActionResolverAndroid implements ActionResolver{

    private Handler uiThread;
    private AndroidApplication appContext;
    private View testView;

    public ActionResolverAndroid(AndroidApplication appContext) {
        uiThread = new Handler();
        this.appContext = appContext;
    }

    @Override
    public void showShortToast(final CharSequence toastMessage) {
        uiThread.post(new Runnable() {
            public void run() {
                Toast.makeText(appContext, toastMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void showLongToast(final CharSequence toastMessage) {
        uiThread.post(new Runnable() {
            public void run() {
                Toast.makeText(appContext, toastMessage, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void showAlertBox(final String alertBoxTitle,
                             final String alertBoxMessage, final String alertBoxButtonText) {
        uiThread.post(new Runnable() {
            public void run() {
                new AlertDialog.Builder(appContext)
                        .setTitle(alertBoxTitle)
                        .setMessage(alertBoxMessage)
                        .setNeutralButton(alertBoxButtonText,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                }).create().show();
            }
        });
    }

    @Override
    public void openUri(String uri) {
        Uri myUri = Uri.parse(uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, myUri);
        appContext.startActivity(intent);
    }

    public void showView(final int view)
    {
        switch (view)
        {
            case VIEW_TEST:
                appContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (testView==null)
                        {
                            LayoutInflater inflater = appContext.getLayoutInflater();
                            testView = inflater.inflate(R.layout.main_activity, null);
                            appContext.addContentView(testView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                    ViewGroup.LayoutParams.FILL_PARENT));
                           // appContext.initOnClickView(testView);
                        }
                        else
                            testView.setVisibility(View.VISIBLE);
                    }
                });
        }
    }

    public void hideView(final int view)
    {
        switch(view)
        {
            case VIEW_TEST:
                testView.setVisibility(View.GONE);
        }
    }
}
