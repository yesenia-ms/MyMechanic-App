package cs3773.group11.mymechanic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    EditText addComment;
    TextView post;

    String solutionID;
    //String authorId;

    FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment);

        //Toolbar toolbar;
        //toolbar = findViewById(R.id.comment_toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Comments");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        finish();
        //    }
        //});
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        solutionID = intent.getStringExtra("solutionID");

        if (solutionID == null) {
            Toast.makeText(this, "Solution ID is not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        addComment = findViewById(R.id.add_comment);
        post = findViewById(R.id.post);
        RecyclerView recyclerView = findViewById(R.id.comment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference solutionsRef = db.collection("car-solutions").document(solutionID);

        CollectionReference commentsRef = solutionsRef.collection("comments");





        fUser = FirebaseAuth.getInstance().getCurrentUser();


        commentsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CommentItem> commentItemList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        CommentItem commentItem = new CommentItem();

                        commentItem.setName(documentSnapshot.getString("name"));
                        commentItem.setRole(documentSnapshot.getString("role"));
                        commentItem.setComment(documentSnapshot.getString("comment"));

                        commentItemList.add(commentItem);
                    }

                    CommentsAdapter adapter = new CommentsAdapter(this, commentItemList);
                    recyclerView.setAdapter(adapter);
                });




/*
        List<CommentItem> commentItemList = new ArrayList<>();

        solutionsRef.whereEqualTo("Document ID", solutionID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        CommentItem commentItem = new CommentItem();
                        commentItem.setName(documentSnapshot.getString("user"));
                        commentItem.setRole(documentSnapshot.getString("role"));
                        commentItem.setComment(documentSnapshot.getString("text"));

                        //String documentId = documentSnapshot.getId();
                        //commentItem.setSolutionID(documentId);

                        commentItemList.add(commentItem);
                    }
                    CommentsAdapter adapter = new CommentsAdapter(this, commentItemList);
                    recyclerView.setAdapter(adapter);

                });
*/
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(CommentActivity.this, "No comment added!", Toast.LENGTH_SHORT).show();
                }else{
                    putComment();
                }
            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
    }

    private void putComment() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference solutionDocRef = db.collection("car-solutions").document(solutionID);
        CollectionReference commentsRef = solutionDocRef.collection("comments");

        CommentItem commentItem = new CommentItem();
        commentItem.setName(fUser.getEmail());
        commentItem.setComment(addComment.getText().toString());

        commentsRef.add(commentItem)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Comment added successfully
                        Toast.makeText(CommentActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                        // Clear the comment input field
                        addComment.setText("");
                    } else {
                        // Failed to add comment
                        Toast.makeText(CommentActivity.this, "Failed to add comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });








        //map.put("comment", addComment.getText().toString());
       // map.put("publisher", fUser.getUid());

        //FirebaseDatabase.getInstance().getReference().child("Comments").child(solutionID)
                //.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>(){

                    //@Override
                    //public void onComplete(@NonNull Task<Void> task) {
                        //if(task.isSuccessful()){
                            //Toast.makeText(CommentActivity.this,"Comment added!",Toast.LENGTH_SHORT).show();
                        //}else{
                           //Toast.makeText(CommentActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        //}
                    //}
                //});

    }


}