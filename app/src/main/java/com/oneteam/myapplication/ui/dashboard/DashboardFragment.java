package com.oneteam.myapplication.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.oneteam.myapplication.AddFoodActivity;
import com.oneteam.myapplication.R;
import com.oneteam.myapplication.adapter.FoodAdapter;
import com.oneteam.myapplication.model.Food;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment implements FoodAdapter.OnClickListener, View.OnClickListener {
    private static final int REQU_ADD = 711;
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private CardView cardViewBelum;
    private CardView cardViewSudah;
    private DashboardViewModel dashboardViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private ImageView imageView;
    private String waktu;
    private TextView textViewBelum;
    private TextView textViewGreetings;
    private TextView textViewQuestion;
    private TextView textViewRekomendasi;
    private TextView textViewSudah;

    private void fetchRealtimeData() {
        db.collection("food_recommendations").addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            List<Food> foods = new ArrayList<>();
            for (QueryDocumentSnapshot document : value) {
                Food food = document.toObject(Food.class);
                foods.add(food);
            }
            dashboardViewModel.setFoodsValue(foods);
        });
    }

    private void goToAddFood() {
        Intent intent = new Intent(getContext(), AddFoodActivity.class);
        startActivityForResult(intent, REQU_ADD);
    }

    private void handleSudahMakan(boolean sudah) {
        if (sudah) {
            cardViewSudah.setOnClickListener(v -> {
            });
            cardViewSudah.setCardBackgroundColor(Color.parseColor("#FF5722"));
            textViewSudah.setTextColor(Color.parseColor("#FFFFFF"));
            cardViewBelum.setCardBackgroundColor(Color.parseColor("#E5E5E5"));
            textViewBelum.setTextColor(Color.parseColor("#000000"));
        } else {
            cardViewSudah.setOnClickListener(v -> goToAddFood());
            cardViewSudah.setCardBackgroundColor(Color.parseColor("#E5E5E5"));
            textViewSudah.setTextColor(Color.parseColor("#000000"));
            cardViewBelum.setCardBackgroundColor(Color.parseColor("#FF5722"));
            textViewBelum.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void handleTime() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String date = sharedPref.getString("date", null);
        if (date != null) {
            try {
                CharSequence dateNow = android.text.format.DateFormat.format("dd/MM/yyyy", Calendar.getInstance().getTime());
                @SuppressLint("SimpleDateFormat") Date dateFromPref = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                @SuppressLint("SimpleDateFormat") Date dateNowParsed = new SimpleDateFormat("dd/MM/yyyy").parse(dateNow.toString());
                if (dateFromPref.compareTo(dateNowParsed) < 0) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("siang", false);
                    editor.putBoolean("malam", false);
                    editor.putBoolean("pagi", false);
                    editor.apply();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "error parsing date", Toast.LENGTH_SHORT).show();
            }
        }
        boolean makanSiang = sharedPref.getBoolean("siang", false);
        boolean makanMalam = sharedPref.getBoolean("malam", false);
        boolean makanPagi = sharedPref.getBoolean("pagi", false);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String question;
        if (hour >= 12 && hour < 17) {
            handleSudahMakan(makanSiang);
            waktu = "Siang";
            question = "makan siang";
        } else if (hour >= 17 && hour < 24) {
            handleSudahMakan(makanMalam);
            waktu = "Malam";
            question = "makan malam";
        } else {
            handleSudahMakan(makanPagi);
            waktu = "Pagi";
            question = "sarapan";
        }
        textViewRekomendasi.setText("Rekomendasi " + waktu + " Hari Ini");
        textViewQuestion.setText("Sudahkah kamu " + question + " hari ini?");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQU_ADD && resultCode == Activity.RESULT_OK) {
            handleTime();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == cardViewSudah.getId()) {
            goToAddFood();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onFoodItemClick(Food food) {
        Intent intent = new Intent(getContext(), AddFoodActivity.class);
        intent.putExtra("food", food);
        startActivityForResult(intent, REQU_ADD);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.profileImage);
        textViewGreetings = view.findViewById(R.id.textViewGreetings);
        textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewRekomendasi = view.findViewById(R.id.textViewRekomendasi);
        textViewBelum = view.findViewById(R.id.textViewBelum);
        textViewSudah = view.findViewById(R.id.textViewSudah);
        FoodAdapter adapter = new FoodAdapter(this, this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFood);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        dashboardViewModel.getFoods().observe(getViewLifecycleOwner(), foods -> {
            if (foods != null) {
                Collections.shuffle(foods);
                adapter.setFoodsData(foods);
            }
        });
        cardViewSudah = view.findViewById(R.id.cardViewSudah);
        cardViewSudah.setOnClickListener(this);
        cardViewBelum = view.findViewById(R.id.cardViewBelum);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageView);
        textViewGreetings.setText("Halo " + firebaseUser.getDisplayName().split(" ")[0] + ",");
        handleTime();
        fetchRealtimeData();
    }
}
