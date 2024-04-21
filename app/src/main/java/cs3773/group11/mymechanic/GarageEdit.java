package cs3773.group11.mymechanic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class GarageEdit extends AppCompatActivity {

    TextView makeTextView, modelTextView, yearTextView, milesTextView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_garage_edit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userUID = mAuth.getCurrentUser().getUid(); db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userUID = mAuth.getCurrentUser().getUid();

        makeTextView = findViewById(R.id.vehicleMake);
        modelTextView = findViewById(R.id.vehicleModel);
        yearTextView = findViewById(R.id.vehicleYear);
        milesTextView = findViewById(R.id.vehicleMiles);

        Intent intent = getIntent();

        // Check if the intent has extras
        if (intent != null && intent.getExtras() != null) {
            // Retrieve the data from the intent extras
            String documentId = intent.getStringExtra("documentId");
            String make = intent.getStringExtra("carMake");
            String model = intent.getStringExtra("carModel");
            String year = intent.getStringExtra("carYear");
            String miles = intent.getStringExtra("carMiles");

            // Set the text of the TextViews with the retrieved data
            makeTextView.setText(make);
            modelTextView.setText(model);
            yearTextView.setText(year);
            milesTextView.setText(miles);
            Button deleteButton = findViewById(R.id.deleteVehicleButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete the vehicle document from Firestore
                    deleteVehicleFromFirestore(documentId);
                }
            });
            Button editButton = findViewById(R.id.editVehicleInfoButton);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implement what should happen when the "Edit Vehicle" button is clicked
                    // For example, navigate to an EditActivity where the user can edit the vehicle info
                    Intent editIntent = new Intent(GarageEdit.this, EditVehicle.class);
                    // Pass any necessary data to the EditActivity using extras
                    editIntent.putExtra("documentId", documentId);
                    editIntent.putExtra("carMake", makeTextView.getText().toString());
                    editIntent.putExtra("carModel", modelTextView.getText().toString());
                    editIntent.putExtra("carYear", yearTextView.getText().toString());
                    editIntent.putExtra("carMiles", milesTextView.getText().toString());
                    startActivity(editIntent);
                }
            });
        }
    }
    private void deleteVehicleFromFirestore(String documentId) {
        db.collection("registeredVehicles")
                .document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Vehicle deleted successfully
                        Toast.makeText(GarageEdit.this, "Vehicle deleted", Toast.LENGTH_SHORT).show();
                        // Navigate back to the GarageActivity or do something else
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error deleting vehicle
                        Toast.makeText(GarageEdit.this, "Error deleting vehicle", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}