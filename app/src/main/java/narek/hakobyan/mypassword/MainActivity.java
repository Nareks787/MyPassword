package narek.hakobyan.mypassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MasterPasswordManager masterPasswordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        masterPasswordManager = new MasterPasswordManager(this);

        Button open = findViewById(R.id.btnOpen);
        Button about = findViewById(R.id.about_us);

        open.setOnClickListener(v -> {
            if (masterPasswordManager.hasMasterPassword()) {
                showUnlockDialog();
            } else {
                showCreateMasterPasswordDialog();
            }
        });

        about.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, about_ua.class);
            startActivity(intent);
        });
    }

    private void showCreateMasterPasswordDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, 0);

        EditText passwordInput = new EditText(this);
        passwordInput.setHint("Create master password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText confirmInput = new EditText(this);
        confirmInput.setHint("Confirm master password");
        confirmInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(passwordInput);
        layout.addView(confirmInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Master password")
                .setMessage("Create password to protect the app")
                .setView(layout)
                .setCancelable(false)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();
            String confirm = confirmInput.getText().toString().trim();

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextUtils.equals(password, confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            masterPasswordManager.saveMasterPassword(password);
            dialog.dismiss();
            openPasswordScreen();
        }));

        dialog.show();
    }

    private void showUnlockDialog() {
        EditText passwordInput = new EditText(this);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        passwordInput.setPadding(padding, padding, padding, padding);
        passwordInput.setHint("Enter master password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Unlock app")
                .setView(passwordInput)
                .setCancelable(false)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Unlock", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();
            if (masterPasswordManager.verifyMasterPassword(password)) {
                dialog.dismiss();
                openPasswordScreen();
            } else {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            }
        }));

        dialog.show();
    }

    private void openPasswordScreen() {
        Intent intent = new Intent(MainActivity.this, main_displey.class);
        startActivity(intent);
    }
}
