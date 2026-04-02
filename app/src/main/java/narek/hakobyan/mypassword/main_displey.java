package narek.hakobyan.mypassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class main_displey extends AppCompatActivity {

    RecyclerView listView;
    ArrayList<DatabaseHelper.PasswordEntry> entries;
    ArrayList<String> displayList;
    PasswordAdapter adapter;
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

        adapter = new PasswordAdapter(displayList, position -> {
            DatabaseHelper.PasswordEntry entry = entries.get(position);
            Intent intent = new Intent(this, PasswordDetailActivity.class);
            intent.putExtra("id", entry.id);
            startActivity(intent);
        });
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        loadPasswords();

        add.setOnClickListener(v -> {
            Intent intent = new Intent(this, dialog_password.class);
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

    static class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {
        interface OnItemClickListener {
            void onItemClick(int position);
        }

        private final ArrayList<String> items;
        private final OnItemClickListener listener;

        PasswordAdapter(ArrayList<String> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override
        public PasswordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new PasswordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PasswordViewHolder holder, int position) {
            holder.text1.setText(items.get(position));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class PasswordViewHolder extends RecyclerView.ViewHolder {
            TextView text1;

            PasswordViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
