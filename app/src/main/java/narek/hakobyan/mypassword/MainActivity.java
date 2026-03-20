package narek.hakobyan.mypassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button open = findViewById(R.id.btnOpen);
        Button about = findViewById(R.id.about_us);

        open.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MasterPasswordActivity.class);
            startActivity(intent);
        });

        about.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, about_ua.class);
            startActivity(intent);
        });
    }
}
