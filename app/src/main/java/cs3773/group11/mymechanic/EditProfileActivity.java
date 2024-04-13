package cs3773.group11.mymechanic;

import static cs3773.group11.mymechanic.R.*;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class EditProfileActivity extends AppCompatActivity{
    EditText editUsername, editEmail;
    Button saveButton;
    String usernameUser, emailUser;
    DatabaseReference reference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        // hides top bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        Intent data = getIntent();
        String currUsername = data.getStringExtra("currUsername");
        String currEmail = data.getStringExtra("currEmail");


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("users");

        editUsername = findViewById(id.edit_username);
        //editEmail = findViewById(id.edit_email);
        saveButton = findViewById(id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editUsername.getText().toString().isEmpty()){
                    Toast.makeText(EditProfileActivity.this, "One or Many fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //final String email = editEmail.getText().toString();
                //user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    //@Override
                    //public void onSuccess(Void unused) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        //edited.put("email", email);
                        edited.put("username", editUsername.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), GarageActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("tag", e.getMessage());
                            }
                        });
                        //Toast.makeText(EditProfileActivity.this, "Email is changed", Toast.LENGTH_SHORT).show();
                    }
            });

        editUsername.setText(currUsername);
       // editEmail.setText(currEmail);

    }

}

