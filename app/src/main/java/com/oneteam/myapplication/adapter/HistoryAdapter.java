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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oneteam.myapplication.R;
import com.oneteam.myapplication.model.Food;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final Fragment fragment;
    public OnClickListener onClickListener;
    FirebaseStorage firebaseStorage;
    String userId;
    private List<Food> foods;

    public HistoryAdapter(OnClickListener onClickListener, Fragment fragment, String userId) {
        this.onClickListener = onClickListener;
        this.fragment = fragment;
        firebaseStorage = FirebaseStorage.getInstance();
        this.userId = userId;
    }

    @Override
    public int getItemCount() {
        return foods == null ? 0 : foods.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(foods.get(position), onClickListener, fragment, firebaseStorage, userId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_history, parent, false);
        return new ViewHolder(view);
    }

    public void setFoodsData(List<Food> foods) {
        if (foods != null) {
            this.foods = foods;
            notifyDataSetChanged();
        }
    }

    public interface OnClickListener {
        void onHistoryItemClick(Food food);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView textViewCardTitle;
        private final TextView textViewDescription;
        private final TextView textViewDate;
        private final TextView textViewKandunganGizi;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewHistory);
            textViewCardTitle = itemView.findViewById(R.id.textViewHistoryTitle);
            imageView = itemView.findViewById(R.id.imageViewFood);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDescription = itemView.findViewById(R.id.textViewDeskripsiMakanan);
            textViewKandunganGizi = itemView.findViewById(R.id.textViewKandunganGizi);
        }

        public void bind(Food food, OnClickListener onClickListener, Fragment fragment, FirebaseStorage firebaseStorage, String userId) {
            CharSequence date = android.text.format.DateFormat.format("dd/MM/yyyy", food.getTime());
            textViewDate.setText(date);
            textViewCardTitle.setText(food.getTitle());
            textViewDescription.setText("Deskripsi: " + food.getDescription());
            StringBuilder kandunganGizi = new StringBuilder();
            for (String gizi : food.getNutrientContent()) {
                kandunganGizi.append(gizi).append(", ");
            }
            CharSequence gizi = kandunganGizi.subSequence(0, kandunganGizi.length() - 2);
            textViewKandunganGizi.setText("Kandungan Gizi: " + gizi);
            if (food.getImageUrl().contains("http")) {
                Glide.with(fragment).load(food.getImageUrl()).into(imageView);
            } else {
                StorageReference storageReference = firebaseStorage.getReference(String.format("%s/food_history/%s", userId, food.getImageUrl()));
                Glide.with(fragment).load(storageReference).into(imageView);
            }
            cardView.setOnClickListener(v -> onClickListener.onHistoryItemClick(food));
        }
    }
}
