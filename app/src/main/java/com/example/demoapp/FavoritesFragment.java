package com.example.demoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesFragment extends Fragment {
    private RecyclerView rv;
    private TextView tvEmpty;
    private FavoriteAdapter adapter;
    private AppDatabase db;
    private SessionManager session;
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        rv = v.findViewById(R.id.rv_fav);
        tvEmpty = v.findViewById(R.id.tv_fav_empty);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FavoriteAdapter(item -> {
            exec.execute(() -> {
                item.isFavorite = false;
                db.itemDao().update(item);
                requireActivity().runOnUiThread(this::loadData);
            });
        });
        rv.setAdapter(adapter);

        db = AppDatabase.getInstance(getContext());
        session = new SessionManager(getContext());
        loadData();
        return v;
    }

    private void loadData() {
        exec.execute(() -> {
            List<Item> list = db.itemDao().getFavoritesByUser(session.getUserId());
            requireActivity().runOnUiThread(() -> {
                adapter.submitList(list);
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}