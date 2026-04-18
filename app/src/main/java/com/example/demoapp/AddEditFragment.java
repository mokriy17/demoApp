package com.example.demoapp;
import com.example.demoapp.Item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditFragment extends Fragment {
    private EditText etTitle, etDesc, etPrice;
    private AppDatabase db;
    private SessionManager session;
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private Item current;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_edit, container, false);
        etTitle = v.findViewById(R.id.et_title);
        etDesc = v.findViewById(R.id.et_desc);
        etPrice = v.findViewById(R.id.et_price);
        Button btnSave = v.findViewById(R.id.btn_save);

        db = AppDatabase.getInstance(getContext());
        session = new SessionManager(getContext());
        int editId = getArguments() != null ? getArguments().getInt("item_id", -1) : -1;

        if (editId != -1) loadItem(editId);
        btnSave.setOnClickListener(view -> save(editId));
        return v;
    }

    private void loadItem(int id) {
        exec.execute(() -> {
            current = db.itemDao().getAllByUser(session.getUserId()).stream()
                    .filter(i -> i.id == id).findFirst().orElse(null);
            requireActivity().runOnUiThread(() -> {
                if (current != null) {
                    etTitle.setText(current.title);
                    etDesc.setText(current.description);
                    etPrice.setText(String.valueOf(current.price));
                }
            });
        });
    }

    private void save(int editId) {
        String t = etTitle.getText().toString().trim();
        String d = etDesc.getText().toString().trim();
        String p = etPrice.getText().toString().trim();

        if (t.isEmpty() || p.isEmpty()) {
            Toast.makeText(getContext(), "Заполните поля", Toast.LENGTH_SHORT).show();
            return;
        }
        double price = Double.parseDouble(p);

        exec.execute(() -> {
            Item item = (editId != -1 && current != null) ? current : new Item();
            item.title = t;
            item.description = d;
            item.price = price;
            item.userId = session.getUserId();
            if (editId == -1) item.createdAt = System.currentTimeMillis();

            if (editId != -1) db.itemDao().update(item);
            else db.itemDao().insert(item);

            requireActivity().runOnUiThread(() ->
                    requireActivity().getSupportFragmentManager().popBackStack());
        });
    }
}