package com.oneteam.myapplication.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oneteam.myapplication.model.Food;

import java.util.List;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<List<Food>> foods;

    public DashboardViewModel() {
        foods = new MutableLiveData<>();
    }

    public LiveData<List<Food>> getFoods() {
        return foods;
    }

    public void setFoodsValue(List<Food> foods) {
        this.foods.setValue(foods);
    }
}
