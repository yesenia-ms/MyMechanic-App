package cs3773.group11.mymechanic.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import cs3773.group11.mymechanic.ProblemsActivity;
import cs3773.group11.mymechanic.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap map; // Declare the GoogleMap variable
    private static final float DEFAULT_ZOOM = 13f; // Default zoom level for the map

    private final int FINE_PERMISSION_CODE = 1; //Permission code for fine location permission
    Location currentLocation; //Current location of the user
    FusedLocationProviderClient fusedLocationProviderClient; //Fused location provider client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button by its ID
        Button findMechanicButton = findViewById(R.id.back_btn);

        // Set onClickListener for the button
        findMechanicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the MechanicActivity
                Intent intent = new Intent(MapActivity.this, ProblemsActivity.class);
                startActivity(intent); // Start the MechanicActivity
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void getLastLocation() {
        //Permissions check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        //Create a new task to build map if the location is not null
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }


    //onMapReady used to create the map
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setZoomControlsEnabled(true);

        //Add a marker and set camera to the users current location
        LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("My Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));

        //Add markers
        addMechanicMarkers();
    }


    //Add map markers to mark nearby mechanics
    private void addMechanicMarkers() {
        LatLng mechanicLocation1 = new LatLng(37.415870502130495, -122.08713324503431);
        LatLng mechanicLocation2 = new LatLng(37.42132327614966, -122.09989974534655);
        LatLng mechanicLocation3 = new LatLng(37.42190392944933, -122.10360833297297);

        map.addMarker(new MarkerOptions()
                .position(mechanicLocation1)
                .title("Kevin's Auto Repair")
                .snippet("Rating: 4.9/5.0")
                );
        map.addMarker(new MarkerOptions()
                .position(mechanicLocation2)
                .title("Silicon Valley Performance Truck and Auto Repair")
                .snippet("Rating: 5.0/5.0"));
        map.addMarker(new MarkerOptions()
                .position(mechanicLocation3)
                .title("Advanced Motor Works")
                .snippet("Rating: 4.7/5.0"));
    }


    //onRequestPermissionsResult used to check if the location permission is granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}