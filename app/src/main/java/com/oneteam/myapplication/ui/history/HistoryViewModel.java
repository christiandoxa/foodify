package com.oneteam.myapplication.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oneteam.myapplication.model.Food;

import java.util.List;

public class HistoryViewModel extends ViewModel {
    private final MutableLiveData<List<Food>> foods;

    public HistoryViewModel() {
        foods = new MutableLiveData<>();
    }

    public LiveData<List<Food>> getFoods() {
        return foods;
    }

    public void setFoodsValue(List<Food> foods) {
        this.foods.setValue(foods);
    }
}
