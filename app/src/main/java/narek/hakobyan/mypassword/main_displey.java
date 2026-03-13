package narek.hakobyan.mypassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class main_displey extends AppCompatActivity {

    ListView listView;
    ArrayList<DatabaseHelper.PasswordEntry> entries;
    ArrayList<String> displayList;
    ArrayAdapter<String> adapter;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_displey);

        Button add = findViewById(R.id.btnAddPassword);
        listView = findViewById(R.id.listPasswords);

        dbHelper = new DatabaseHelper(this);
        entries = new ArrayList<>();
        displayList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        loadPasswords();

        add.setOnClickListener(v -> {
            Intent intent = new Intent(this, dialog_password.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            DatabaseHelper.PasswordEntry entry = entries.get(position);
            Intent intent = new Intent(this, PasswordDetailActivity.class);
            intent.putExtra("id", entry.id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPasswords();
    }

    private void loadPasswords() {
        entries.clear();
        displayList.clear();
        entries.addAll(dbHelper.getAllPasswords());
        for (DatabaseHelper.PasswordEntry e : entries) {
            displayList.add(e.site + "  —  " + e.login);
        }
        adapter.notifyDataSetChanged();
    }
}
