package cs3773.group11.mymechanic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AddCarActivity extends AppCompatActivity {
    FirebaseFirestore db;
    AutoCompleteTextView makeDropdown;
    AutoCompleteTextView modelDropdown;

    AutoCompleteTextView yearDropdown;
    AutoCompleteTextView miles;
    String selectedMake;
    String selectedModel;
    ImageButton profileIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.garageScreenAdd), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        db = FirebaseFirestore.getInstance();
        makeDropdown = findViewById(R.id.addMakeDropdown);
        modelDropdown = findViewById(R.id.addModelDropdown);
        yearDropdown = findViewById(R.id.addYearDropdown);
        miles = findViewById(R.id.addMiles);
        fetchMakeFromFirestore();
        makeDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed
                selectedMake = s.toString();
                if (selectedMake != null && !selectedMake.isEmpty()) {
                    fetchModelsFromFirestore(selectedMake);
                } else {
                    Log.d("AddCarActivity", "Selected make is null or empty");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text is changed
            }
        });
        modelDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed
                selectedModel = s.toString();
                if (selectedModel != null && !selectedModel.isEmpty() && selectedMake != null && !selectedMake.isEmpty()) {
                    fetchYearsFromFirestore(selectedMake, selectedModel);
                } else {
                    Log.d("AddCarActivity", "Selected model or make is null or empty");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text is changed
            }
        });

        Button button1 = findViewById(R.id.cancelButton);
        Button button2 = findViewById(R.id.addConfirmButton);
        profileIcon = findViewById(R.id.imageButton3);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCarActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the first button
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the intent to start the new activity
                Intent intent = new Intent(AddCarActivity.this, GarageActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the second button
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming mAuth is a reference to Firebase Authentication
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                // Get the currently signed-in user's UID
                String userUID = mAuth.getCurrentUser().getUid();
                String make = makeDropdown.getText().toString();
                String model = modelDropdown.getText().toString();
                String year = yearDropdown.getText().toString();
                String tMiles = miles.getText().toString();

// Create a new vehicle object
                Map<String, Object> vehicle = new HashMap<>();
                vehicle.put("uID", userUID);
                vehicle.put("Make", make);
                vehicle.put("Model", model);
                vehicle.put("Year", year);
                vehicle.put("Miles", tMiles);

// Add the vehicle data to the Firestore database
                db.collection("registeredVehicles")
                        .add(vehicle)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "Vehicle added with ID: " + documentReference.getId());
                                // Handle success (e.g., show a success message)
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding vehicle", e);
                                // Handle failure (e.g., show an error message)
                            }
                        });
                // Define the intent to start the new activity
                Intent intent = new Intent(AddCarActivity.this, GarageActivity.class);
                startActivity(intent);
            }
        });
    }
    private void fetchMakeFromFirestore() {
        db.collection("cars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> makes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get 'Make' field value from the document
                                String make = document.getString("Make");
                                if (make != null && !makes.contains(make)) {
                                    makes.add(make); // Add the 'Make' value to the list if it's not already there
                                }
                            }
                            Collections.sort(makes);
                            // Populate the dropdown menu with the retrieved 'Make' values
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCarActivity.this, android.R.layout.simple_dropdown_item_1line, makes);
                            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            makeDropdown.setAdapter(adapter);
                        } else {
                            // Handle failed data retrieval
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void fetchModelsFromFirestore(String selectedMake) {
        // Query Firestore to fetch models based on the selected make
        // Example:
        db.collection("cars")
                .whereEqualTo("Make", selectedMake)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Set<String> uniqueModels = new HashSet<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve the 'Model' field as an Object
                                Object modelValue = document.get("Model");

                                // Check the data type of the 'Model' field
                                if (modelValue instanceof String) {
                                    String model = (String) modelValue; // If 'Model' is a string, simply cast it to a string
                                    uniqueModels.add(model);
                                } else if (modelValue instanceof Number) {
                                    String model = String.valueOf(modelValue); // If 'Model' is a number, convert it to a string
                                    uniqueModels.add(model);
                                } else {
                                    // Handle other data types for 'Model', if necessary
                                    Log.e("fetchModelsFromFirestore", "Unexpected data type for 'Model' field");
                                }
                            }
                            List<String> models = new ArrayList<>(uniqueModels);
                            // Update the list of models in the AutoCompleteTextView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCarActivity.this, android.R.layout.simple_dropdown_item_1line, models);
                            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            modelDropdown.setAdapter(adapter);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void fetchYearsFromFirestore(String selectedMake, String selectedModel) {
        // Query Firestore to fetch years based on the selected make and model
        db.collection("cars")
                .whereEqualTo("Make", selectedMake)
                .whereEqualTo("Model", selectedModel)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> years = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Object yearValue = document.get("Year");
                                if (yearValue instanceof Number) {
                                    String year = String.valueOf(yearValue);
                                    years.add(year);
                                }
                            }
                            Collections.sort(years);
                            Log.d("TAG", "Selected years: " + years);
                            // Update the list of years in the AutoCompleteTextView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCarActivity.this, android.R.layout.simple_dropdown_item_1line, years);
                            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            yearDropdown.setAdapter(adapter);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}