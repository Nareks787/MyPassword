package narek.hakobyan.mypassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MasterPasswordActivity extends AppCompatActivity {

    private static final String LOGIN_SECURITY_PREFS = "login_security_prefs";
    private static final String KEY_FAILED_ATTEMPTS = "failed_master_password_attempts";
    private static final int MAX_FAILED_ATTEMPTS = 10;

    private MasterPasswordManager masterPasswordManager;
    private SharedPreferences loginSecurityPreferences;
    private boolean hasExistingPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_password);

        masterPasswordManager = new MasterPasswordManager(this);
        loginSecurityPreferences = getSharedPreferences(LOGIN_SECURITY_PREFS, MODE_PRIVATE);
        hasExistingPassword = masterPasswordManager.hasMasterPassword();

        TextView title = findViewById(R.id.tvMasterPasswordTitle);
        TextView subtitle = findViewById(R.id.tvMasterPasswordSubtitle);
        EditText passwordInput = findViewById(R.id.etMasterPassword);
        EditText confirmInput = findViewById(R.id.etMasterPasswordConfirm);
        Button actionButton = findViewById(R.id.btnMasterPasswordAction);

        if (hasExistingPassword) {
            title.setText(R.string.enter_master_password_title);
            subtitle.setText(R.string.enter_master_password_subtitle);
            passwordInput.setHint(R.string.enter_master_password_hint);
            confirmInput.setVisibility(View.GONE);
            actionButton.setText(R.string.unlock_button);
        } else {
            title.setText(R.string.create_master_password_title);
            subtitle.setText(R.string.create_master_password_subtitle);
            passwordInput.setHint(R.string.create_master_password_hint);
            confirmInput.setHint(R.string.confirm_master_password_hint);
            confirmInput.setVisibility(View.VISIBLE);
            actionButton.setText(R.string.create_password_button);
            actionButton.setEnabled(false);
            addRegistrationValidationWatcher(passwordInput, confirmInput, actionButton);
        }

        actionButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError(getString(R.string.master_password_required));
                return;
            }

            if (!hasExistingPassword && !PasswordSecurityUtils.isValidPassword(password)) {
                passwordInput.setError(PasswordSecurityUtils.VALIDATION_ERROR_MESSAGE);
                return;
            }

            if (!hasExistingPassword && !TextUtils.equals(password, confirmInput.getText().toString().trim())) {
                confirmInput.setError(getString(R.string.passwords_do_not_match));
                return;
            }

            if (hasExistingPassword) {
                if (masterPasswordManager.verifyMasterPassword(password)) {
                    resetFailedAttempts();
                    openPasswordsScreen();
                } else {
                    int failedAttempts = incrementFailedAttempts();
                    if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                        performEmergencyWipe();
                        return;
                    }
                    passwordInput.setError(getString(R.string.master_password_incorrect));
                    passwordInput.requestFocus();
                }
            } else {
                resetFailedAttempts();
                masterPasswordManager.saveMasterPassword(password);
                Toast.makeText(this, R.string.master_password_created, Toast.LENGTH_SHORT).show();
                openPasswordsScreen();
            }
        });
    }

    private void addRegistrationValidationWatcher(EditText passwordInput, EditText confirmInput, Button actionButton) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = passwordInput.getText().toString().trim();
                String confirm = confirmInput.getText().toString().trim();
                boolean isValid = PasswordSecurityUtils.isValidPassword(password) && TextUtils.equals(password, confirm);
                actionButton.setEnabled(isValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No-op
            }
        };

        passwordInput.addTextChangedListener(watcher);
        confirmInput.addTextChangedListener(watcher);
    }

    private int incrementFailedAttempts() {
        int attempts = loginSecurityPreferences.getInt(KEY_FAILED_ATTEMPTS, 0) + 1;
        loginSecurityPreferences.edit().putInt(KEY_FAILED_ATTEMPTS, attempts).apply();
        return attempts;
    }

    private void resetFailedAttempts() {
        loginSecurityPreferences.edit().putInt(KEY_FAILED_ATTEMPTS, 0).apply();
    }

    private void performEmergencyWipe() {
        // ВНИМАНИЕ: Ниже выполняется безвозвратное удаление базы с паролями приложения.
        deleteDatabase("passwords.db");

        clearAllSharedPreferences();

        // ВНИМАНИЕ: После удаления ключа из Android Keystore старые зашифрованные данные восстановить нельзя.
        new CryptoManager().resetKeyMaterial();

        hasExistingPassword = false;
        resetFailedAttempts();
        Toast.makeText(this, "Data deleted after 10 failed login attempts", Toast.LENGTH_LONG).show();
        recreate();
    }

    private void clearAllSharedPreferences() {
        java.io.File sharedPrefsDir = new java.io.File(getApplicationInfo().dataDir, "shared_prefs");
        java.io.File[] prefFiles = sharedPrefsDir.listFiles();
        if (prefFiles == null) {
            return;
        }

        for (java.io.File prefFile : prefFiles) {
            String fileName = prefFile.getName();
            if (!fileName.endsWith(".xml")) {
                continue;
            }
            String prefName = fileName.substring(0, fileName.length() - 4);
            getSharedPreferences(prefName, MODE_PRIVATE).edit().clear().apply();
        }
    }

    private void openPasswordsScreen() {
        Intent intent = new Intent(this, main_displey.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
