package com.example.montefit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrainingSessionAdapter extends RecyclerView.Adapter<TrainingSessionAdapter.ViewHolder> {

    private List<TrainingSessionItem> sessionItems;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }

    private OnItemClickListener listener;

    public TrainingSessionAdapter(List<TrainingSessionItem> sessionItems, OnItemClickListener listener) {
        this.sessionItems = sessionItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrainingSessionItem item = sessionItems.get(position);
        holder.text1.setText(item.getExercise().getName() + " (" + item.getExercise().getBodyPart() + ")");

        StringBuilder setsInfo = new StringBuilder();
        List<TrainingSessionItem.SetDetail> sets = item.getSets();
        for (int i = 0; i < sets.size(); i++) {
            TrainingSessionItem.SetDetail set = sets.get(i);
            setsInfo.append("Set ").append(i + 1).append(": ")
                    .append(set.reps).append("x").append(set.weight).append("kg");
            if (i < sets.size() - 1)
                setsInfo.append(" | ");
        }

        holder.text2.setText(setsInfo.toString());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return sessionItems.size();
    }

    public void updateList(List<TrainingSessionItem> newList) {
        this.sessionItems = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
