package cs3773.group11.mymechanic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class GarageActivity extends AppCompatActivity{
    FirebaseFirestore db;
    LinearLayout carContainer;
    FirebaseAuth mAuth;
    String userUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_garage);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // Get the currently signed-in user's UID
        userUID = mAuth.getCurrentUser().getUid();
        Log.d("TAG", "curr uID: " + userUID);

        ImageButton profileIcon = findViewById(R.id.imageButton3);
        ImageButton imageButton = findViewById(R.id.addButton);
        carContainer = findViewById(R.id.garageAddView);

        // Set an OnClickListener to the profile icon to go to profile page
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GarageActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        // Set an OnClickListener to the ImageButton
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //temporary sign out button for troubleshooting
                //FirebaseAuth.getInstance().signOut();
                //Intent intent = new Intent(GarageActivity.this, LoginActivity.class);
                //startActivity(intent);


                // Define the intent to start the new activity
                Intent intent = new Intent(GarageActivity.this, AddCarActivity.class);
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.garageScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnItemSelectedListener(item ->  {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_dashboard) {
                startActivity(new Intent(getApplicationContext(), ProblemsActivity.class));
                finish();
                return true;
            } else if(itemId == R.id.navigation_maintenance){
                startActivity(new Intent(getApplicationContext(), MaintenanceActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Clear button that was deleted on resume
        clearButtons();
        populateGarageWithVehicles();
    }

    private void clearButtons() {
        carContainer.removeAllViews();
    }
    private void populateGarageWithVehicles() {
        db.collection("registeredVehicles")
                .whereEqualTo("uID", userUID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // Create buttons dynamically for each car
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Extract car data from the document
                            String make = document.getString("Make");
                            String model = document.getString("Model");
                            String year = document.getString("Year");
                            String miles = document.getString("Miles");
                            String carName = year + " " + make + " " + model;
                            String totalMiles = "Total Miles: " + miles;
                            String documentId = document.getId();

                            // Create a new Button
                            Button carButton = new Button(GarageActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    925, 400
                            );
                            params.setMargins(75, 50, 75, 30); // Left, Top, Right, Bottom margins
                            carButton.setLayoutParams(params);
                            carButton.setPadding(0, 80, 0, 0);
                            carButton.setText(carName + "\n" + totalMiles);
                            carButton.setTextSize(23); // Text size in SP
                            carButton.setTextColor(Color.WHITE);
                            carButton.setBackground(ContextCompat.getDrawable(GarageActivity.this, R.drawable.button_background));
                            carButton.setTypeface(ResourcesCompat.getFont(GarageActivity.this, R.font.racing_sans_one));
                            carButton.setGravity(Gravity.TOP | Gravity.CENTER);
                            carButton.setClickable(true);

                            // Add click listener to the button (if needed)
                            carButton.setOnClickListener(view -> {
                                // Handle button click event
                                // Open a new activity and pass data to it
                                Intent intent = new Intent(GarageActivity.this, GarageEdit.class);
                                intent.putExtra("documentId", documentId);
                                intent.putExtra("carMake", make);
                                intent.putExtra("carModel", model);
                                intent.putExtra("carYear", year);
                                intent.putExtra("carMiles", miles);
                                startActivity(intent);
                            });

                            // Add the Button to the LinearLayout
                            carContainer.addView(carButton);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occurred while fetching the documents
                        Log.e("GarageActivity", "Error getting documents: " + e.getMessage());
                    }
                });
    }
}