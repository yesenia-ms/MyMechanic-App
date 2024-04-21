package cs3773.group11.mymechanic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EditVehicle extends AppCompatActivity {

    EditText makeEditText, modelEditText, yearEditText, milesEditText;
    FirebaseFirestore db;
    String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_vehicle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        makeEditText = findViewById(R.id.vehicleMake);
        modelEditText = findViewById(R.id.vehicleModel);
        yearEditText = findViewById(R.id.vehicleYear);
        milesEditText = findViewById(R.id.vehicleMiles);

        // Get the data passed from GarageEdit activity
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            documentId = intent.getStringExtra("documentId");
            String make = intent.getStringExtra("carMake");
            String model = intent.getStringExtra("carModel");
            String year = intent.getStringExtra("carYear");
            String miles = intent.getStringExtra("carMiles");

            // Set the initial text of EditText fields
            makeEditText.setText(make);
            modelEditText.setText(model);
            yearEditText.setText(year);
            milesEditText.setText(miles);
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        Button saveButton = findViewById(R.id.saveChangesButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When Save Changes button is clicked, update the vehicle information
                updateVehicleInfo();
            }
        });
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the previous screen (GarageEdit activity)
                finish();
            }
        });
    }
    private void updateVehicleInfo() {
        // Get the new values from the EditText fields
        String newMake = makeEditText.getText().toString();
        String newModel = modelEditText.getText().toString();
        String newYear = yearEditText.getText().toString();
        String newMiles = milesEditText.getText().toString();

        // Update the vehicle document in Firestore
        db.collection("registeredVehicles")
                .document(documentId)
                .update(
                        "Make", newMake,
                        "Model", newModel,
                        "Year", newYear,
                        "Miles", newMiles
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Vehicle information updated successfully
                        Toast.makeText(EditVehicle.this, "Changes saved", Toast.LENGTH_SHORT).show();
                        // Finish the activity and go back to Garage
                        Intent garage = new Intent(EditVehicle.this, GarageActivity.class);
                        startActivity(garage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error updating vehicle information
                        Toast.makeText(EditVehicle.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}