package cs3773.group11.mymechanic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import cs3773.group11.mymechanic.ui.MapActivity;

public class ProblemsActivity extends AppCompatActivity {

    Button generateSolutionsButton;
    Button findNearbyMechanic;
    Spinner carDropdown;
    Spinner problemDropdown;
    EditText obdCodeUser;
    boolean foundOBD = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String current = user.getUid(); // getting unique user id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_problems);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        generateSolutionsButton = findViewById(R.id.generateSolutionsButton);

        findNearbyMechanic = findViewById(R.id.find_mechanic_btn);

        ImageButton profileIcon = findViewById(R.id.imageButton3);
        // Set an OnClickListener to the profile icon to go to profile page
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProblemsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // problems Dropdown spinner
        problemDropdown = findViewById(R.id.problemsDropdown);
        // get the problems from the google Firestore
        // declare a collection reference to get the collection name called "car-problems"
        CollectionReference carProblemCollection = db.collection("car-problems");
        ArrayList<String> carProbs = new ArrayList<>();
        HashMap<String, String> probNumName = new HashMap<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, carProbs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        problemDropdown.setAdapter(adapter);
        // getting the collection document and reading each document in a for each loop
        // getting the problem name and saving it into an array list which is saved in the array adapter for the dropdown
        carProblemCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String problemName = document.getString("Problem Name");
                        String problemNumber = document.getString("Problem Number");
                        carProbs.add(problemName);
                        probNumName.put(problemName, (problemNumber));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Which Vehicle? Dropdown Menu
        carDropdown = findViewById(R.id.carDropdown);
        // get the registeredVehicles collection from Firestore
        CollectionReference registeredVCollection = db.collection("registeredVehicles");
        ArrayList<String> vehicles = new ArrayList<>();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, vehicles);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carDropdown.setAdapter(adapter2);
        // getting all the collection document and then read each document
        // for each document read compare the uID in the document with the current uID
        registeredVCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        // check if the document uID is the same as current uID
                        String documentUID = document.getString("uID");
                        if(documentUID.equals(current)){
                            String year = document.getString("Year");
                            String make = document.getString("Make");
                            String model = document.getString("Model");
                            String fullVName = year + " " + make + " " + model;
                            vehicles.add(fullVName);
                        }
                    }
                    adapter2.notifyDataSetChanged();
                }
            }
        });


        // Set an OnClickListener to the ImageButton
        findNearbyMechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the MechanicActivity
                Intent intent = new Intent(ProblemsActivity.this, MapActivity.class);
                startActivity(intent); // Start the MechanicActivity
            }
        });

        //Profile Button
        ImageButton profileBtn = findViewById(R.id.imageButton3);
        profileBtn.setOnClickListener(v -> {
            // Create an Intent to navigate to the ProfileActivity
            Intent intent = new Intent(ProblemsActivity.this, ProfileActivity.class);
            startActivity(intent); // Start the ProfileActivity
        });
        navView.setSelectedItemId(R.id.navigation_problems);
        navView.setOnItemSelectedListener(item ->  {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), GarageActivity.class));
                finish();
                return true;
            } else if(itemId == R.id.navigation_maintenance) {
                startActivity(new Intent(getApplicationContext(), MaintenanceActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_problems) {
                startActivity(new Intent(getApplicationContext(), ProblemsActivity.class));
                finish();
                return true;
            }
            return false;
        });

        obdCodeUser = findViewById(R.id.OBDIICode);

        generateSolutionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when the user clicks the generate solutions button and the user entered an OBD code
                // need to check if the OBD code exists in the db
                if(obdCodeUser.getText().length()==0){
                    // this means that user did not enter an OBD code so we can display the commmon problem choosen from the
                    // drop down menu
                    // TODO: need to pass the problem name and number to the next activity
                    Intent intent = new Intent(v.getContext(), SolutionsActivity.class);
                    intent.putExtra("Problem Name", problemDropdown.getSelectedItem().toString());
                    intent.putExtra("Problem Number", probNumName.get(problemDropdown.getSelectedItem().toString()));
                    Log.d("PROBLEM NAME PASSED:", problemDropdown.getSelectedItem().toString());
                    Log.d("PROBLEM NUMBER PASSED: ", String.valueOf(probNumName.get(problemDropdown.getSelectedItem().toString())));
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(ProblemsActivity.this, "Invalid OBD Code", Toast.LENGTH_SHORT).show();
//                    // need to check if the obd code exists in obd-error-codes collection
//                    CollectionReference obdCollection = db.collection("obd-error-codes");
//                    obdCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if(task.isSuccessful()){
//                                for(QueryDocumentSnapshot document : task.getResult()){
//                                    String OBDcodeDoc = document.getString("P0100");
//                                    Log.d("OBD CODE:", String.valueOf(OBDcodeDoc));
//                                    if(obdCodeUser.equals(OBDcodeDoc)){
//                                        foundOBD = true;
//                                        break;
//                                    }
//                                }
//                                if(foundOBD) {
//                                    Log.d("FOUND OBD!:", String.valueOf(foundOBD));
//                                    Intent intent = new Intent(getApplicationContext(), SolutionsActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                                else{
//                                    Log.d("NOT FOUND OBD!:", String.valueOf(foundOBD));
//                                    Toast.makeText(ProblemsActivity.this, "Invalid OBD Code", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                    });

                }

            }
        });
    }
}