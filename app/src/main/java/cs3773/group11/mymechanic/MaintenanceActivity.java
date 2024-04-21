package cs3773.group11.mymechanic;

import static cs3773.group11.mymechanic.R.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
public class MaintenanceActivity extends AppCompatActivity{

    ImageButton profileIcon;
    Button enterlastOilChange, enterlastTirePressure, enterlastCarLights;
    TextView lastOilChange, lastTirePressure, lastCarLights;
    TextView nextOilChange, nextTirePressure, nextCarLights;
    //ProgressBar progressBar;
    boolean oilASAP, tpASAP, clASAP;
    FirebaseAuth mAuth;
    // Start firebase firestore instance to save the username data
    FirebaseFirestore fStore;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        profileIcon = findViewById(id.imageButton3);
        /** BUTTONS TO ENTER DATA */
        enterlastOilChange = findViewById(id.enter_last_ol_date);
        enterlastTirePressure = findViewById(id.enter_last_tp_date);
        enterlastCarLights = findViewById(id.enter_last_cl_date);
        /** TEXTVIEWS TO CHANGE TEXT FOR LAST TIME */
        lastOilChange = findViewById(id.last_oil_change);
        lastTirePressure = findViewById(id.last_tp_check);
        lastCarLights = findViewById(id.last_cl_check);
        /** TEXTVIEWS TO CHANGE TEXT FOR NEXT TIME */
        nextOilChange = findViewById(id.next_oil_change);
        nextTirePressure = findViewById(id.next_tp_check);
        nextCarLights = findViewById(id.next_cl_check);
        /** FIRESTORE VARIABLES */
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

       // progressBar.setVisibility(View.GONE);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the intent to start the new activity
                Intent intent = new Intent(MaintenanceActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        enterlastOilChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the intent to start the new activity
                Intent intent = new Intent(MaintenanceActivity.this, SetDateMaintenanceActivity.class);
                intent.putExtra("LastQ", "When was your last oil change?");
                startActivity(intent);
            }
        });

        enterlastTirePressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceActivity.this, SetDateMaintenanceActivity.class);
                intent.putExtra("LastQ", "When was the last time you checked the tire pressures for your car tires?");
                startActivity(intent);
            }
        });

        enterlastCarLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceActivity.this, SetDateMaintenanceActivity.class);
                intent.putExtra("LastQ", "When was the last time you checked your cars lights?");
                startActivity(intent);
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnItemSelectedListener(item ->  {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), GarageActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_dashboard) {
                startActivity(new Intent(getApplicationContext(), ProblemsActivity.class));
                finish();
                return true;
            } else if(itemId == R.id.navigation_maintenance) {
                return true;
            }
            return false;
        });

        /**
         * READ DATA FROM FIRESTORE AND SEE THE
         * LAST_OL_CHANGE
         * LAST_TP_CHECK
         * LAST_CL_CHECK
         */
        getData();
    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String current = user.getUid(); // getting unique user id
        DocumentReference documentReference = fStore.collection("users").document(current);

       // progressBar.setVisibility(View.VISIBLE);
        db.collection("users")
                .whereEqualTo("uID", current)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot document : task.getResult()){
                                // check if the document entry "last_ol_change" is equal to null
                                String lastOilChangeText = document.getString("last_ol_change");
                                String lastTirePressureText = document.getString("last_tp_check");
                                String lastCarLightsText = document.getString("last_cl_check");
                                // next check date
                                String nextOilChangeText = document.getString("next_ol_change");
                                String nextTirePressureText = document.getString("next_tp_check");
                                String nextCarLightsText = document.getString("next_cl_check");
                                // before spliting the date check if a DATE even exists if it is null
                                if(lastOilChangeText.equals("")){
                                    lastOilChange.setText("\tNO DATA ENTERED");
                                    nextOilChange.setText("\tNO DATA ENTERED");
                                }
                                else{ // otherwise set the text to date the user entered
                                    lastOilChange.setText(""+lastOilChangeText);
                                    if(checkDates(nextOilChangeText)){
                                        nextOilChange.setTextColor(255);
                                        nextOilChange.setText("ASAP!");
                                    }
                                    else {
                                        nextOilChange.setTextColor(000);
                                        nextOilChange.setText("" + nextOilChangeText);
                                    }
                                }
                                // check if the document entry "last_tp_check" is equal to null
                                if(lastTirePressureText.equals("")){
                                    lastTirePressure.setText("\tNO DATA ENTERED");
                                    nextTirePressure.setText("\tNO DATA ENTERED");
                                }
                                else{ // otherwise set the text to date the user entered
                                    lastTirePressure.setText(""+lastTirePressureText);
                                    if(checkDates(nextTirePressureText)){
                                        nextTirePressure.setTextColor(255);
                                        nextTirePressure.setText("ASAP!");
                                    }else{
                                        nextTirePressure.setTextColor(000);
                                        nextTirePressure.setText(""+nextTirePressureText);
                                    }
                                }
                                // check if the document entry "last_cl_check" is equal to null
                                if(lastCarLightsText.equals("")){
                                    nextCarLights.setTextColor(000);
                                    lastCarLights.setText("\tNO DATA ENTERED");
                                    nextCarLights.setText("\tNO DATA ENTERED");
                                }
                                else{ // otherwise set the text to date the user entered
                                    lastCarLights.setText(""+lastCarLightsText);
                                    if(checkDates(nextCarLightsText)){
                                        nextCarLights.setTextColor(255);
                                        nextCarLights.setText("ASAP!");
                                    }else{
                                        nextCarLights.setTextColor(000);
                                        nextCarLights.setText(""+nextCarLightsText);
                                    }
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MaintenanceActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        //progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private boolean checkNextDateASAP(String[] currentDateFormat, String[] validNextChange) {
        // changing the variable type from String to integer to do calculations
        int month = Integer.parseInt(validNextChange[0]);
        int day = Integer.parseInt(validNextChange[1]);
        int year = Integer.parseInt(validNextChange[2]);
        // Log for debugging
        Log.d("NEXT ASAP?", String.valueOf(month));
        Log.d("NEXT ASAP?", String.valueOf(day));
        Log.d("NEXT ASAP?", String.valueOf(year));

        // changing the varianle type from String to integer to do calculations on current date
        int currDay = Integer.parseInt(currentDateFormat[0]);
        int currMonth = Integer.parseInt(currentDateFormat[1]);
        int currYear = Integer.parseInt(currentDateFormat[2]);
        // Log for debugging
        Log.d("CURR ASAP?", String.valueOf(currMonth));
        Log.d("CURR ASAP?", String.valueOf(currDay));
        Log.d("CURR ASAP?", String.valueOf(currYear));

        // if the next maintenance date is before this current year then ASAP
        if(year < currYear){
            return true;
        }
        // if the next maintenance month date is before this current month AND the maintenance year is < this year then ASAP
        else if(month < currMonth && (year < currYear)){
            return true;
        }
        // if the next maintenance day date is before this current day and the month is before this current month then ASAP
        else if((day < currDay) && (month == currMonth)){
            return true;
        }
        return false;
    }
    private boolean checkDates(String nextText/*, String nextOilChangeText, String nextTirePressureText, String nextCarLightsText*/){
        boolean nextASAP;
        String[] validNext = nextText.split("/");

//        String[] validNextOil = nextOilChangeText.split("/");
//        String[] validNextTP = nextTirePressureText.split("/");
//        String[] validNextCL = nextCarLightsText.split("/");

        Log.d("VALIDATE NEXT OIL MONTH tag", validNext[0]);
        Log.d("VALIDATE NEXT OIL MONTH tag", validNext[1]);
        Log.d("VALIDATE NEXT OIL MONTH tag", validNext[2]);

//        Log.d("VALIDATE NEXT OIL MONTH tag", validNextOil[0]);
//        Log.d("VALIDATE NEXT OIL DAY tag", validNextOil[1]);
//        Log.d("VALIDATE NEXT OIL YEAR tag", validNextOil[2]);

        // getting the current Month, Day, and Year
        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
        DateFormat dateFormatDay = new SimpleDateFormat("DD");
        DateFormat dateFormatYear = new SimpleDateFormat("YYYY");
        Date date = new Date();
        Log.d("Month", (dateFormatMonth.format(date)));
        Log.d("Day", (dateFormatDay.format(date)));
        Log.d("Year", (dateFormatYear.format(date)));
        // formatting todays date to use it to calculate
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        Log.d("FORMATTED DATE", formattedDate);

        // split formatted date
        String[] currentDateFormat = formattedDate.split("-");
        Log.d("CURRENT MM DATE", currentDateFormat[0]);
        Log.d("CURRENT DD DATE", currentDateFormat[1]);
        Log.d("CURRENT YYYY DATE", currentDateFormat[2]);

        nextASAP = checkNextDateASAP(currentDateFormat, validNext);
//
//        oilASAP = checkNextDateASAP(currentDateFormat, validNextOil);
//        tpASAP = checkNextDateASAP(currentDateFormat, validNextTP);
//        clASAP = checkNextDateASAP(currentDateFormat, validNextCL);
        //Log.d("oilASAP", oil)
        return nextASAP;
    }

}
