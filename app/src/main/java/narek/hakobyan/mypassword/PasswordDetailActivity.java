package narek.hakobyan.mypassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordDetailActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    int entryId;
    DatabaseHelper.PasswordEntry entry;

    TextView tvSite, tvLogin, tvPassword;
    Button btnShowPassword, btnEdit, btnDelete;

    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_detail);

        dbHelper = new DatabaseHelper(this);
        entryId = getIntent().getIntExtra("id", -1);

        tvSite = findViewById(R.id.tvSite);
        tvLogin = findViewById(R.id.tvLogin);
        tvPassword = findViewById(R.id.tvPassword);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        loadEntry();

        btnShowPassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                tvPassword.setText(entry.password);
                btnShowPassword.setText("Скрыть пароль");
            } else {
                tvPassword.setText("••••••••");
                btnShowPassword.setText("Показать пароль");
            }
        });

        btnEdit.setOnClickListener(v -> showEditDialog());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удалить")
                    .setMessage("Удалить запись для " + entry.site + "?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        dbHelper.deletePassword(entryId);
                        Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void loadEntry() {
        entry = dbHelper.getPasswordById(entryId);
        if (entry != null) {
            tvSite.setText(entry.site);
            tvLogin.setText(entry.login);
            tvPassword.setText("••••••••");
            passwordVisible = false;
            btnShowPassword.setText("Показать пароль");
        }
    }

    private void showEditDialog() {
        View redactor = getLayoutInflater().inflate(R.layout.redactor, null);
        EditText etSite = redactor.findViewById(R.id.etSite);
        EditText etLogin = redactor.findViewById(R.id.etLogin);
        EditText etPassword = redactor.findViewById(R.id.etPassword);

        etSite.setText(entry.site);
        etLogin.setText(entry.login);
        etPassword.setText(entry.password);

        new AlertDialog.Builder(this)
                .setTitle("Редактировать")
                .setView(redactor)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String newSite = etSite.getText().toString().trim();
                    String newLogin = etLogin.getText().toString().trim();
                    String newPassword = etPassword.getText().toString().trim();

                    if (newSite.isEmpty()) {
                        Toast.makeText(this, "Введите сайт", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dbHelper.updatePassword(entryId, newSite, newLogin, newPassword);
                    Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
                    loadEntry();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
