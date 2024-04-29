package cs3773.group11.mymechanic;

import static cs3773.group11.mymechanic.R.id;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    TextView loginText;
    EditText signUpUsername, signUpEmail, signUpPassword, signUpConfirmPassword;
    Button signUpButton;
    //FirebaseDatabase database;
    //DatabaseReference reference;
    FirebaseAuth mAuth;
    //FirebaseFirestore fStore;
    String userID;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), GarageActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        loginText = findViewById(R.id.loginText);
        signUpUsername = findViewById(R.id.signUpUsername);
        signUpEmail = findViewById(id.signUpEmail);
        signUpPassword = findViewById(id.signUpPassword);
        signUpConfirmPassword = findViewById(id.signUpConfirmPassword);
        signUpButton = findViewById(id.signUpButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //changes screen to login screen
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //checks for errors in account creation steps. if no errors, creates account in firebase.
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //database = FirebaseDatabase.getInstance();
                //reference = database.getReference();

                String username = signUpUsername.getText().toString();
                String email = signUpEmail.getText().toString();
                String password = signUpPassword.getText().toString();
                String confirmPassword = signUpConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(SignUpActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUpActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignUpActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(SignUpActivity.this, "Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(confirmPassword)){
                    Toast.makeText(SignUpActivity.this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
                                    userID = user.getUid();

//                                    Map<String, Object> newUser = new HashMap<>();
//                                    newUser.put("username", username);
//                                    newUser.put("email", email);
//                                    newUser.put("uID", userId);
//





                                    /* SAVING DATA IN CLOUD FIRESTORE CODE */
                                    userID = mAuth.getCurrentUser().getUid();
                                    // creating collection of users - if not created it automatically creates it
                                    //DocumentReference documentReference = fStore.collection("users").document(userID);
                                    // use a hash map to save data
                                    Map<String, Object> user1 = new HashMap<>();
                                    user1.put("username", username);
                                    user1.put("email", email);
                                    user1.put("uID", userID);
                                    user1.put("last_ol_change", "");
                                    user1.put("last_tp_check", "");
                                    user1.put("last_cl_check", "");
                                    user1.put("next_ol_change", "");
                                    user1.put("next_tp_check", "");
                                    user1.put("next_cl_check", "");
                                    user1.put("role", "user");


                                    // inserting into database
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(userID).set(user1);


                                    /*documentReference.set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG", "onSuccess: user Profile is created  for " + userID);
                                        }
                                    });*/

                                    //updateUI(user);

                                    Toast.makeText(SignUpActivity.this, "Sign Up Successful!",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class );
                                    startActivity(intent);


                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Sign up failed. Email may already be in use.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });


                /*
                HelperClass helperClass = new HelperClass(username,email,username,password);
                reference.child(username).setValue(helperClass);

                Toast.makeText(SignUpActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class );
                startActivity(intent);
                */

            }
        });
    }
}
