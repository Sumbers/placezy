package bh.edu.ahlia.placezy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapsActivity extends FragmentActivity implements IApiCallTask, OnMapReadyCallback, FragmentSelect.SelectFragmentListener, LocationListener {

    private GoogleMap mMap;
    private ConnexionState coState = null;
    private Location location = null;
    private FragmentSelect fragmentSelect;
    private String allPlaces = null;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private Marker myMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allPlaces = getAllPlaces();
        setContentView(R.layout.main_layout);

        LocationManager locationManager = null;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(10000L, (float)50, criteria, this, null);
//        System.out.println("LA LOCATION A ETE ETABLIE");
//        System.out.println("LOCATION = " + location);

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

    public String getAllPlaces(){
        String[] placesSequence = getResources().getStringArray(R.array.places);
        String places  = new String();
        for (int i = 1; i < placesSequence.length;i++){
            places = places.concat(placesSequence[i]);
            if (i < placesSequence.length - 1){
                places = places.concat("|");
            }
        }
        places = places.replaceAll(" ", "_");
        System.out.println("all places = " + places);
        return (places);
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
        myMarker = mMap.addMarker(new MarkerOptions().position(defaultPos).title("Me"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPos));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPos, 10.0F));
        new ApiCallTask(this, ApiCallTask.OBJECT, "displayPlace").execute(
                getString(R.string.google_places_api),
                "en",
                defaultPos.latitude + "," + defaultPos.longitude,
                "1000",
                allPlaces);
    }

    @Override
    public void onPlaceSelected(String placeSelected) {
        if (placeSelected == "all") {
            placeSelected = allPlaces;
        }
        LatLng defaultPos = myMarker.getPosition();
        new ApiCallTask(this, ApiCallTask.OBJECT, "displayPlace").execute(
                getString(R.string.google_places_api),
                "en",
                defaultPos.latitude + "," + defaultPos.longitude,
                "1000",
                placeSelected);
    }

    @Override
    public void onBackgroundTaskCompleted(Places places, int type, String action) throws JSONException {
        if (action == "displayPlace")
            displayPlaces(places);
    }

    private void displayPlaces(Places places) {
        removeMarkers();
        System.out.println("nombre de resultat = " + places.getResults().size());
        for (Result result : places.getResults()){
            System.out.println("nom de la place = " + result.getName());
            bh.edu.ahlia.placezy.Location location = result.getGeometry().getLocation();
            LatLng posMarker = new LatLng(location.getLat(), location.getLng());
            Marker newMarker = mMap.addMarker(new MarkerOptions().position(posMarker)
                    .title(result.getName())
                    .icon(getIcon(result.getTypes()))
                    .snippet(result.getVicinity()));
            markers.add(newMarker);
        }
    }

    private BitmapDescriptor getIcon(List<String> types) {
        for (String type : types)
        {
            switch (type)
            {
                case "hospital" :
                    return (BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital));
                case "bank" :
                    return (BitmapDescriptorFactory.fromResource(R.drawable.ic_bank));
                case "hotel" :
                    return (BitmapDescriptorFactory.fromResource(R.drawable.ic_hotel));
                case "mosque" :
                    return (BitmapDescriptorFactory.fromResource(R.drawable.ic_mosque));
                case "clothing_store" :
                    return (BitmapDescriptorFactory.fromResource(R.drawable.ic_shopping));
                case "pharmacy" :
                    return (BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital));
                default:
                    return (BitmapDescriptorFactory.fromResource(R.drawable.ic_generic_business));
            }
        }
        return (BitmapDescriptorFactory.fromResource(R.drawable.ic_generic_business));
    }

    private void removeMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (myMarker != null)
            myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
