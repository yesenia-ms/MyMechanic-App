package cs3773.group11.mymechanic;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SolutionsActivity extends AppCompatActivity {

    String problem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_solutions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.solutionsScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        TextView problemTitle = findViewById(R.id.problem_title);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference solutionsRef = db.collection("car-solutions");
        CollectionReference problemRef = db.collection("car-problems");

        Query problemQuery = problemRef.whereEqualTo("Problem Number", 8).limit(1);

        problemQuery.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                problem = documentSnapshot.getString("Problem Name");
                            }
                            problemTitle.setText(problem);
                        });



        List<SolutionItem> solutionItemList = new ArrayList<>();




        //**********************the value below needs to be passed through intent.putExtra from problems page*************************
        solutionsRef.whereEqualTo("Problem Number", 8)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        SolutionItem solutionItem = new SolutionItem();
                        solutionItem.setSolutionTitle(documentSnapshot.getString("Possible Solution"));
                        solutionItem.setSolutionDescription(documentSnapshot.getString("Description"));
                        solutionItemList.add(solutionItem);
                    }
                    MyAdapter adapter = new MyAdapter(solutionItemList);
                    recyclerView.setAdapter(adapter);

                });


    }
}