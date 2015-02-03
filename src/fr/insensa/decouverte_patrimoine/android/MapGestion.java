package fr.insensa.decouverte_patrimoine.android;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

// différentes méthodes qui peuvent être utilisée par un objet de type GoogleMap à passer en paramètre du constructeur

public class MapGestion extends Activity {

    // la map
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    // the map Fragment
    private com.google.android.gms.maps.MapFragment mMapFragment;
    // les options par défaut de création de la map
    private GoogleMapOptions options = new GoogleMapOptions();
    // le markeur de la position de l'utilisateur
    private Marker markerMaPosition;
    // constructeur par défaut
    public MapGestion(GoogleMap map) {
        mMap = map;
    };


    // mettre à jour la map
    public void setUpMap(Location location) {
        afficherMaPosition(location);
        mMap.setMapType(2);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        Log.i("mMap =", mMap.toString());
    }

    public void afficherMaPosition(Location location) {
        //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        if (markerMaPosition!=null) {
            markerMaPosition.remove();
            markerMaPosition = mMap.addMarker( new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Ma position").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_action_edit)));
        }
        else {
            markerMaPosition = mMap.addMarker( new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Ma position").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_action_locate)));
        }
    }

    public void afficherEtapesSelonStatut(ArrayList<LatLng> etapesArray, ArrayList<String> etapesArrayIsFinished) {
        for (int i=1; i<etapesArray.size(); i++) {
            if (etapesArrayIsFinished.get(i).equals("true")) {
                mMap.addMarker(new MarkerOptions()
                        .position(etapesArray.get(i))
                        .title("etape")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
            else if (etapesArrayIsFinished.get(i).equals("false")) {
                mMap.addMarker(new MarkerOptions()
                        .position(etapesArray.get(i))
                        .title("etape")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else if (etapesArrayIsFinished.get(i).equals("enCours")) {
                mMap.addMarker(new MarkerOptions()
                        .position(etapesArray.get(i))
                        .title("etape")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }
    }
}
