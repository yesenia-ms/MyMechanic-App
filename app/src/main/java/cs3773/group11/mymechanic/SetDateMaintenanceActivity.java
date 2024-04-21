package cs3773.group11.mymechanic;

import static cs3773.group11.mymechanic.R.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class SetDateMaintenanceActivity extends AppCompatActivity {

    TextView lastTimeQuestion;
    DatePicker datePicker;
    Button saveButton;
    Button backButton;
    int day, month, year;
    int nextDay, nextMonth, nextYear;
    int nextDayOil, nextMonthOil, nextYearOil;
    TimeZone currentTime;
    List<Object> listOil;

    List<Object> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date_maintenance);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        long now = System.currentTimeMillis() - 1000;
        Log.d("CURRENT DATE:", String.valueOf(now));

        lastTimeQuestion = findViewById(R.id.last_time_question);
        datePicker = findViewById(id.date_picker);
        datePicker.setMaxDate(now);

        saveButton = findViewById(id.save_button);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent intent = new Intent(SetDateMaintenanceActivity.this, MaintenanceActivity.class);
                                              startActivity(intent);
                                          }
                                      });




        Intent data = getIntent();
        String lastTimeQ = data.getStringExtra("LastQ");

        lastTimeQuestion.setText(lastTimeQ);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = datePicker.getDayOfMonth();
                month = datePicker.getMonth() + 1; // reading month you have to add 1, when setting month subtract 1
                year = datePicker.getYear();
                Log.d("LAST tag", String.valueOf(day));
                Log.d("LAST tag", String.valueOf(month));
                Log.d("LAST tag", String.valueOf(year));
                Toast.makeText(SetDateMaintenanceActivity.this, "" + month + "/" + day + "/" + year + "", Toast.LENGTH_SHORT).show();
                updateData(month, day, year);
                /** Calculating the next maintenance checks*/
                String question = (String)lastTimeQuestion.getText();
                if(question.contains("oil change")){
                    // just add 3 to the month - oil change
                    listOil = calculateNewDateOil(day, month, year);
                    nextDay = (int) listOil.get(0);
                    nextMonth = (int) listOil.get(1);
                    nextYear = (int) listOil.get(2);
                    Log.d("CALCULATED NEXT OIL DATES", String.valueOf(nextDay));
                    Log.d("CALCULATED NEXT OIL DATES", String.valueOf(nextMonth));
                    Log.d("CALCULATED NEXT OIL DATES", String.valueOf(nextYear));
                }
                else if(question.contains("tire pressures") || (question.contains("cars lights"))) {
                    // just add 1 to the month - tire pressure
                    list = calculateNewDate(day, month, year);
                    nextDay = (int) list.get(0);
                    nextMonth = (int) list.get(1);
                    nextYear = (int) list.get(2);
                    Log.d("CALCULATED NEXT OTHER DATES", String.valueOf(nextDay));
                    Log.d("CALCULATED NEXT OTHER DATES", String.valueOf(nextMonth));
                    Log.d("CALCULATED NEXT OTHER DATES", String.valueOf(nextYear));
                }

                Log.d("NEXT DAY tag", String.valueOf(nextDay));
                Log.d("NEXT DAY tag", String.valueOf(nextMonth));
                Log.d("NEXT DAY tag", String.valueOf(nextYear));
                // TODO: update the entered data into Firestore for NEXT TIME
                updateNextData(nextDay, nextMonth, nextYear);

            }
        });


    }

    public List<Object> calculateNewDateOil(int day, int month, int year) {
        int nextDay = 0, nextMonth = 0, nextYear = 0;
        if(month == 10){
            nextMonth = 1;
            nextDay = day;
            nextYear = year + 1;
        }
        else if(month == 11){
            nextMonth = 2;
            nextDay = 29;
            nextYear = year + 1;
        }
        else if(month == 12){
            nextMonth = 3;
            nextDay = day;
            nextYear = year + 1;
        }
        else if(((month == 1) || (month == 3) || (month == 8)) && (day == 31) ){
            /** handling the problem where the 3rd month for oil change does not have 31 days */
            nextMonth = month + 3;
            nextDay = 30;
            nextYear = year;
        }
        else{
            nextMonth = month + 3;
            nextDay = day;
            nextYear = year;
        }
       return Arrays.asList(nextDay, nextMonth, nextYear);
    }

    public List<Object> calculateNewDate(int day, int month, int year) {
        int nextDay = 0, nextMonth = 0, nextYear = 0;
        if(month == 12){
            nextMonth = 1;
            nextDay = day;
            nextYear = year + 1;
        }
        else if((month == 1) && (day == 31)){
            nextMonth = 2;
            nextDay = 29;
            nextYear = year;
        }
        else if(((month == 3) || (month == 5) || (month == 8) || (month == 10)) && (day == 31) ){
            /** handling the problem where the 3rd month for oil change does not have 31 days */
            nextMonth = month + 1;
            nextDay = 30;
            nextYear = year;
        }
        else{
            nextMonth = month + 1;
            nextDay = day;
            nextYear = year;
        }
        return Arrays.asList(nextDay, nextMonth, nextYear);
    }

    public void updateData(int month, int day, int year) {
        String lastCheckString = String.valueOf(month) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
        Log.d("LAST CHECK STRING!!", lastCheckString);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String current = user.getUid(); // getting unique user id

        Map<String, Object> lastCheck = new HashMap<>();

        String question = (String)lastTimeQuestion.getText();
        if(question.contains("oil change")){
            // then update last_ol_change
            lastCheck.put("last_ol_change", lastCheckString);
        }
        else if(question.contains("tire pressures")){
            // then update last_tp_check
            lastCheck.put("last_tp_check", lastCheckString);
        }
        else if(question.contains("cars lights")){
            // then update last_cl_check
            lastCheck.put("last_cl_check", lastCheckString);
        }
        // get the collection in Firestore
        db.collection("users")
                .whereEqualTo("uID", current)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            // getting the first document
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            db.collection("users")
                                    .document(documentID)
                                    .update(lastCheck)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(SetDateMaintenanceActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SetDateMaintenanceActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(SetDateMaintenanceActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateNextData(int nextDay, int nextMonth, int nextYear) {
        String nextCheckString = String.valueOf(nextMonth) + "/" + String.valueOf(nextDay) + "/" + String.valueOf(nextYear);
        Log.d("tag", nextCheckString);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String current = user.getUid(); // getting unique user id

        Map<String, Object> nextCheck = new HashMap<>();

        String question = (String)lastTimeQuestion.getText();
        if(question.contains("oil change")){
            // then update last_ol_change
            nextCheck.put("next_ol_change", nextCheckString);
        }
        else if(question.contains("tire pressures")){
            // then update last_tp_check
            nextCheck.put("next_tp_check", nextCheckString);
        }
        else if(question.contains("cars lights")){
            // then update last_cl_check
            nextCheck.put("next_cl_check", nextCheckString);
        }
        // get the collection in Firestore
        db.collection("users")
                .whereEqualTo("uID", current)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            // getting the first document
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            db.collection("users")
                                    .document(documentID)
                                    .update(nextCheck)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(SetDateMaintenanceActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SetDateMaintenanceActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(SetDateMaintenanceActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
