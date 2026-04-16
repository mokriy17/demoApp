package com.example.demoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.VH> {
    private List<Item> items;
    private OnRemoveListener listener;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public interface OnRemoveListener {
        void onRemove(Item item);
    }

    public FavoriteAdapter(OnRemoveListener l) {
        this.listener = l;
    }

    public void submitList(List<Item> list) {
        items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fav, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Item i = items.get(position);
        holder.tvTitle.setText(i.title);
        holder.tvDesc.setText(i.description);
        holder.tvDate.setText("Создано: " + sdf.format(i.createdAt));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(i));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        Button btnRemove;

        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tv_f_title);
            tvDesc = v.findViewById(R.id.tv_f_desc);
            tvDate = v.findViewById(R.id.tv_f_date);
            btnRemove = v.findViewById(R.id.btn_f_remove);
        }
    }
}