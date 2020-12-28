package com.oneteam.myapplication.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.oneteam.myapplication.AddFoodActivity;
import com.oneteam.myapplication.R;
import com.oneteam.myapplication.adapter.HistoryAdapter;
import com.oneteam.myapplication.model.Food;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnClickListener {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private FirebaseFirestore db;
    private HistoryViewModel historyViewModel;
    private String userId;

    private void fetchRealtimeData() {
        db.collection("food_history").document(userId).collection("history").orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    List<Food> foods = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        Food food = document.toObject(Food.class);
                        foods.add(food);
                    }
                    historyViewModel.setFoodsValue(foods);
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onHistoryItemClick(Food food) {
        Intent intent = new Intent(getContext(), AddFoodActivity.class);
        intent.putExtra("read", true);
        intent.putExtra("food", food);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory);
        HistoryAdapter adapter = new HistoryAdapter(this, this, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        historyViewModel.getFoods().observe(getViewLifecycleOwner(), foods -> {
            if (foods != null) {
                adapter.setFoodsData(foods);
            }
        });
        fetchRealtimeData();
    }
}
