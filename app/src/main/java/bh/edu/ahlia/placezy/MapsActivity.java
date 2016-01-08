package bh.edu.ahlia.placezy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

public class MapsActivity extends FragmentActivity implements IApiCallTask, OnMapReadyCallback, FragmentSelect.SelectFragmentListener {

    private GoogleMap mMap;
    private ConnexionState coState = null;
    private LocationManager locationManager = null;
    private Location location = null;
    private FragmentSelect fragmentSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        System.out.println("LA LOCATION A ETE ETABLIE");
        System.out.println("LOCATION = " + location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragmentSelect = (FragmentSelect) getSupportFragmentManager()
                .findFragmentById(R.id.select);
        mapFragment.getMapAsync(this);

//        coState = new ConnexionState(this);
//        if(!coState.getStatus()) {
//            AlertDialog.Builder dialogue = new AlertDialog.Builder(this);
//            dialogue.setIcon(R.mipmap.ic_launcher);
//            dialogue.setTitle("Vous n'êtes pas connecté à internet");
//            dialogue.setMessage("Sans connexion Placezy ne peut fonctionner");
//                dialogue.setPositiveButton("Se connecter", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent settings_call = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
//                        PackageManager packageManager = getPackageManager();
//                        List<ResolveInfo> activities = packageManager.queryIntentActivities(settings_call, 0);
//                        boolean isIntentSafe = activities.size() > 0;
//                        if (isIntentSafe) {
//                            startActivity(settings_call);
//                        }
//                    }
//                });
//                dialogue.setNegativeButton("Quitter Placezy", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//                dialogue.show();
//        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultPos;
        // Add a marker in my pos and move the camera
        if (location != null) {
            defaultPos = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else {
            defaultPos = new LatLng(26.236440, 50.590090);
        }
        mMap.addMarker(new MarkerOptions().position(defaultPos).title("Me"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPos));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPos, 10.0F));

        new ApiCallTask(this, ApiCallTask.OBJECT, "displayPlace").execute(
                "AIzaSyA4tEUNxP6-k5XZ77BVtAO4IqAmAZlFBNE",
                "en",
                defaultPos.latitude + "," + defaultPos.longitude,
                "3000",
                "hospital%7bank%7library%7mosque%7bus_station%7cafe%7clothing_store%7pharmacy%7parking%7park");
    }

    @Override
    public void onPlaceSelected(String placeSelected) {

    }

    @Override
    public void onBackgroundTaskCompleted(Places places, int type, String action) throws JSONException {
        System.out.println("nombre de resultat = " + places.getResults().size());
        for (Result result : places.getResults()){
            System.out.println("nom de la place = " + result.getName());
        }
//        if (action == "displayPlace")
//            displayPlaces(places);
    }

    private void displayPlaces(Places places) {

    }
}
