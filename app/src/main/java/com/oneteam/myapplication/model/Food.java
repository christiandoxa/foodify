package com.oneteam.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Food implements Parcelable {
    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };
    @DocumentId
    private String uId;
    @ServerTimestamp
    private Date time;
    private String title;
    private String description;
    private String imageUrl;
    private List<String> nutrientContent;

    public Food() {
    }

    public Food(String title, String description, String imageUrl, List<String> nutrientContent) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.nutrientContent = nutrientContent;
    }

    protected Food(Parcel in) {
        uId = in.readString();
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        nutrientContent = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getNutrientContent() {
        return nutrientContent;
    }

    public void setNutrientContent(List<String> nutrientContent) {
        this.nutrientContent = nutrientContent;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeStringList(nutrientContent);
    }
}
