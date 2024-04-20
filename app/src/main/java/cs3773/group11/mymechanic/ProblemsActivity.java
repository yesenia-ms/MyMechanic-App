package cs3773.group11.mymechanic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import cs3773.group11.mymechanic.ui.MapActivity;

public class ProblemsActivity extends AppCompatActivity {

    Button generateSolutionsButton;
    Button findNearbyMechanic;
    ImageButton profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_problems);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        generateSolutionsButton = findViewById(R.id.generateSolutionsButton);

        findNearbyMechanic = findViewById(R.id.find_mechanic_btn);

        ImageButton profileIcon = findViewById(R.id.imageButton3);
        // Set an OnClickListener to the profile icon to go to profile page
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProblemsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        // Set an OnClickListener to the ImageButton
        findNearbyMechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the MechanicActivity
                Intent intent = new Intent(ProblemsActivity.this, MapActivity.class);
                startActivity(intent); // Start the MechanicActivity
            }
        });

        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnItemSelectedListener(item ->  {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), GarageActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_dashboard) {
                return true;
            }
            return false;



        });

        generateSolutionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SolutionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}