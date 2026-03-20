package narek.hakobyan.mypassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MasterPasswordActivity extends AppCompatActivity {

    private MasterPasswordManager masterPasswordManager;
    private boolean hasExistingPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_password);

        masterPasswordManager = new MasterPasswordManager(this);
        hasExistingPassword = masterPasswordManager.hasMasterPassword();

        TextView title = findViewById(R.id.tvMasterPasswordTitle);
        TextView subtitle = findViewById(R.id.tvMasterPasswordSubtitle);
        EditText passwordInput = findViewById(R.id.etMasterPassword);
        Button actionButton = findViewById(R.id.btnMasterPasswordAction);

        if (hasExistingPassword) {
            title.setText(R.string.enter_master_password_title);
            subtitle.setText(R.string.enter_master_password_subtitle);
            passwordInput.setHint(R.string.enter_master_password_hint);
            actionButton.setText(R.string.unlock_button);
        } else {
            title.setText(R.string.create_master_password_title);
            subtitle.setText(R.string.create_master_password_subtitle);
            passwordInput.setHint(R.string.create_master_password_hint);
            actionButton.setText(R.string.create_password_button);
        }

        actionButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError(getString(R.string.master_password_required));
                return;
            }

            if (password.length() < 4) {
                passwordInput.setError(getString(R.string.master_password_too_short));
                return;
            }

            if (hasExistingPassword) {
                if (masterPasswordManager.verifyPassword(password)) {
                    openPasswordsScreen();
                } else {
                    passwordInput.setError(getString(R.string.master_password_incorrect));
                    passwordInput.requestFocus();
                }
            } else {
                masterPasswordManager.saveMasterPassword(password);
                Toast.makeText(this, R.string.master_password_created, Toast.LENGTH_SHORT).show();
                openPasswordsScreen();
            }
        });
    }

    private void openPasswordsScreen() {
        Intent intent = new Intent(this, main_displey.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
