package com.example.montefit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> foodList;

    public interface OnFoodClickListener {
        void onDeleteClick(Food food);
    }

    private OnFoodClickListener listener;

    public FoodAdapter(List<Food> foodList, OnFoodClickListener listener) {
        this.foodList = foodList;
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
        Food food = foodList.get(position);
        holder.text1.setText(food.getName());
        holder.text2.setText(String.format("%d Kcal | P: %.1fg | C: %.1fg | G: %.1fg",
                food.getKcal(), food.getProtein(), food.getCarbs(), food.getFats()));

        holder.itemView.setOnClickListener(v -> {
            listener.onDeleteClick(food);
        });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onDeleteClick(food);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateList(List<Food> newList) {
        this.foodList = newList;
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
