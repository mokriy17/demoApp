package com.example.demoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private RecyclerView rv;
    private TextView tvEmpty;
    private ItemAdapter adapter;
    private AppDatabase db;
    private SessionManager session;
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rv = v.findViewById(R.id.rv_home);
        tvEmpty = v.findViewById(R.id.tv_home_empty);
        Button fab = v.findViewById(R.id.fab_add);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter(item -> {
            ItemDetailFragment detail = new ItemDetailFragment();
            Bundle args = new Bundle();
            args.putInt("item_id", item.id);
            detail.setArguments(args);
            ((MainActivity) requireActivity()).openInnerFragment(detail);
        });
        rv.setAdapter(adapter);

        db = AppDatabase.getInstance(getContext());
        session = new SessionManager(getContext());

        fab.setOnClickListener(view ->
                ((MainActivity) requireActivity()).openInnerFragment(new AddEditFragment()));

        loadData();
        return v;
    }

    private void loadData() {
        exec.execute(() -> {
            List<Item> list = db.itemDao().getAllByUser(session.getUserId());
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