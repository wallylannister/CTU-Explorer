package com.ctu.ctu_explorer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnItemSelectedListener {
    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    private MapView mapView;
    private GeoPoint currentLocation;
    private Marker currentMarker;
    private GeoPoint destinationPoint;
    private Marker destinationMarker;
    private Polyline routingOverlay;
    private FusedLocationProviderClient fusedLocationClient;

    final MapEventsReceiver mReceive = new MapEventsReceiver(){
        @Override
        public boolean singleTapConfirmedHelper(GeoPoint p) {
            mapView.getOverlays().remove(destinationMarker);
            mapView.getOverlays().remove(routingOverlay);
            setCurrentLocation(p.getLatitude(),p.getLongitude());
            return false;
        }
        @Override
        public boolean longPressHelper(GeoPoint p) {
            return false;
        }
    };

    public void setCurrentLocation(double latitude, double longitude) {
        mapView.getOverlays().remove(currentMarker);
        currentLocation = new GeoPoint(latitude, longitude);
        currentMarker = new Marker(mapView);
        currentMarker.setPosition(currentLocation);
        currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        currentMarker.setIcon(getResources().getDrawable(R.drawable.ic_person_pin_circle_black_24dp, getTheme()));
        mapView.getOverlays().add(currentMarker);
        mapView.getController().animateTo(currentLocation);
        mapView.invalidate();
    }

    public void setDestination(double latitude, double longitude) {
        mapView.getOverlays().remove(destinationMarker);
        destinationPoint = new GeoPoint(latitude, longitude);
        destinationMarker = new Marker(mapView);
        destinationMarker.setPosition(destinationPoint);
        destinationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        destinationMarker.setIcon(getResources().getDrawable(R.drawable.ic_place_black_24dp, getTheme()));
        mapView.getOverlays().add(destinationMarker);
        mapView.getController().animateTo(destinationPoint);
        mapView.invalidate();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (Buildings.locations[pos] != null) {
            setDestination(Buildings.locations[pos].getLatitude(), Buildings.locations[pos].getLongitude());
            getRouting();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // disable strict mode for easy development
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);

        checkPermissionsStateAndSetupMap();
        getCurrentLocation();
        mapView.getOverlays().add(new MapEventsOverlay(mReceive));

        Spinner buildingMenu = findViewById(R.id.building_selection);
        String[] buildings = Buildings.titles;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, buildings);
        buildingMenu.setAdapter(adapter);
        buildingMenu.setOnItemSelectedListener(this);

        ImageButton myLocationBtn = findViewById(R.id.my_location_btn);
        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    private void checkPermissionsStateAndSetupMap() {
        int internetPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int networkStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);

        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE);

        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                wifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED) {

            setupMap();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE},
                    MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean somePermissionWasDenied = false;
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        somePermissionWasDenied = true;
                    }
                }
                if (somePermissionWasDenied) {
                    Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    setupMap();
                }
            } else {
                Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private void setupMap() {
        mapView = findViewById(R.id.map_view);

        mapView.setClickable(true);
        mapView.setMultiTouchControls(true);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.getController().setCenter(new GeoPoint(10.03088, 105.76813));
        mapView.getController().setZoom(18.0);
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            setCurrentLocation(location.getLatitude(), location.getLongitude());
                        }
                    }
                });

        if (currentLocation == null) {
            Toast.makeText(this, "Please enable GPS to get your location", Toast.LENGTH_LONG).show();
        }
    }

    public void getRouting() {
        if (currentLocation != null && destinationPoint != null) {
            ArrayList<GeoPoint> waypoints = new ArrayList<>();

            waypoints.add(currentLocation);
            waypoints.add(destinationPoint);

            RoadManager roadManager = new OSRMRoadManager(this);
            ((OSRMRoadManager) roadManager).setService(getResources().getString(R.string.osrm_server_url));

            Road road = null;
            try {
                road = roadManager.getRoad(waypoints);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (road != null) {
                mapView.getOverlays().remove(routingOverlay);
                routingOverlay = RoadManager.buildRoadOverlay(road);
                mapView.getOverlays().add(routingOverlay);
                mapView.invalidate();
            }

        }
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up

    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
