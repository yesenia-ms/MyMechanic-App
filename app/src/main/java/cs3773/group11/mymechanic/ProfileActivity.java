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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
public class ProfileActivity extends AppCompatActivity{
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    TextView userUsername;
    TextView userEmail;
    TextView userPassword;
    String userID;


    Button resetPassword, logoutButton, editProfileButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // hides the top bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        userUsername = findViewById(R.id.user_username);
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        logoutButton = findViewById(R.id.logout_button);
        editProfileButton = findViewById(R.id.edit_profile_button);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // getting current Uid
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        /**
         * Code causing error with logout button, doesn't allow complete log out for users
         * documentSnapshot.exists() - error
         */
        /** DISPLAY DATA CODE */
        getData();
//        DocumentReference documentReference = fStore.collection("users").document(userID);
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                if(documentSnapshot.exists()){
//                    userUsername.setText(documentSnapshot.getString("username"));
//                    userEmail.setText(documentSnapshot.getString("email"));
//                } else{
//                    Log.d("tag", "onEvent: Document do not exists");
//                }
//            }
//        });
        /** END DISPLAY CODE */

        /** EDIT PROFILE CODE */
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EditProfileActivity.class);
                i.putExtra("currUsername", userUsername.getText().toString());
                i.putExtra("currEmail", userEmail.getText().toString());
                startActivity(i);
            }
        });
        /** END EDIT PROFILE CODE */


        /** PASSWORD RESET CODE */
        resetPassword = findViewById(R.id.reset_password);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract new password and reset it
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });
                passwordResetDialog.create().show();
            }
        });
        /** END PASSWORD RESET CODE */

        /** LOG OUT BUTTON CODE */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
        /** END LOG OUT BUTTON CODE */

    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String current = user.getUid(); // getting unique user id

        db.collection("users")
                .whereEqualTo("uID", current)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot document : task.getResult()){
                                userUsername.setText((CharSequence) document.get("username"));
                                userEmail.setText((CharSequence) document.get("email"));
                            }
                        }
                    }
                });

        // BottomNavigationView navView = findViewById(R.id.nav_view);
        // navView.setSelectedItemId(R.id.navigation_home);
        // navView.setOnItemSelectedListener(item ->  {
        //     int itemId = item.getItemId();
        //     if (itemId == R.id.navigation_home) {
        //         startActivity(new Intent(getApplicationContext(), GarageActivity.class));
        //         finish();
        //         return true;
        //     } else if (itemId == R.id.navigation_dashboard) {
        //         startActivity(new Intent(getApplicationContext(), ProblemsActivity.class));
        //         finish();
        //         return true;
        //     }
        //     return false;
        // });
    }

}
