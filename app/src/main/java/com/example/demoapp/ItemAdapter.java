package com.example.demoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {
    private List<Item> items;
    private OnItemClick listener;
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    public interface OnItemClick {
        void onClick(Item item);
    }

    public ItemAdapter(OnItemClick l) {
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
                .inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Item i = items.get(position);
        holder.tvTitle.setText(i.title);
        holder.tvDesc.setText(i.description);
        holder.tvPrice.setText(String.format("%.2f ₽", i.price));
        holder.btnFav.setText(i.isFavorite ? "★" : "☆");
        holder.itemView.setOnClickListener(v -> listener.onClick(i));
        holder.btnFav.setOnClickListener(v -> {
            i.isFavorite = !i.isFavorite;
            exec.execute(() ->
                    AppDatabase.getInstance(v.getContext()).itemDao().update(i));
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvPrice;
        Button btnFav;

        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tv_m_title);
            tvDesc = v.findViewById(R.id.tv_m_desc);
            tvPrice = v.findViewById(R.id.tv_m_price);
            btnFav = v.findViewById(R.id.btn_m_fav);
        }
    }
}