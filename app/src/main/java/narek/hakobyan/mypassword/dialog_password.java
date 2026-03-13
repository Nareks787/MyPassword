
package narek.hakobyan.mypassword;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class dialog_password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_password);

        EditText site = findViewById(R.id.etSite);
        EditText login = findViewById(R.id.etLogin);
        EditText password = findViewById(R.id.etPassword);
        Button save = findViewById(R.id.btnSave);

        save.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.insertPassword(
                    site.getText().toString(),
                    login.getText().toString(),
                    password.getText().toString()
            );
            finish();
        });
    }
}
