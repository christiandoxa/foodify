package com.oneteam.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oneteam.myapplication.R;
import com.oneteam.myapplication.model.Food;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    List<Food> foods;
    OnClickListener onClickListener;
    Fragment fragment;

    public FoodAdapter(OnClickListener onClickListener, Fragment fragment) {
        this.onClickListener = onClickListener;
        this.fragment = fragment;
    }

    @Override
    public int getItemCount() {
        return foods == null ? 0 : foods.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(foods.get(position), onClickListener, fragment);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_food, parent, false);
        return new ViewHolder(view);
    }

    public void setFoodsData(List<Food> foods) {
        this.foods = foods;
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onFoodItemClick(Food food);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewFood;
        ImageView imageViewFood;
        TextView textViewFoodTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewFood = itemView.findViewById(R.id.cardViewFood);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            textViewFoodTitle = itemView.findViewById(R.id.textViewFoodTitle);
        }

        public void bind(Food food, OnClickListener onClickListener, Fragment fragment) {
            textViewFoodTitle.setText(food.getTitle());
            Glide.with(fragment).load(food.getImageUrl()).into(imageViewFood);
            cardViewFood.setOnClickListener(v -> onClickListener.onFoodItemClick(food));
        }
    }
}
