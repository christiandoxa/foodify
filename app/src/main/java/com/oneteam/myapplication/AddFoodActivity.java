package com.oneteam.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oneteam.myapplication.model.Food;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddFoodActivity extends AppCompatActivity {
    private boolean ayamSelected = false;
    private boolean buahSelected = false;
    private boolean dagingSelected = false;
    private boolean ikanSelected = false;
    private boolean isRead = false;
    private boolean nasiSelected = false;
    private boolean rotiSelected = false;
    private boolean sayurSelected = false;
    private boolean susuSelected = false;
    private boolean telurSelected = false;
    private EditText editTextDescription;
    private EditText editTextJudul;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private Food foodIntent;
    private ImageView imageViewFood;
    private MaterialCardView cardViewAyam;
    private MaterialCardView cardViewBuah;
    private MaterialCardView cardViewDaging;
    private MaterialCardView cardViewIkan;
    private MaterialCardView cardViewNasi;
    private MaterialCardView cardViewRoti;
    private MaterialCardView cardViewSayur;
    private MaterialCardView cardViewSusu;
    private MaterialCardView cardViewTelur;
    private MaterialCardView cardViewGetPhoto;
    private String imageFileName;
    private String userId;
    private TextView textViewGetPhoto;
    private Uri fileUri;

    private void flipImageSelected(boolean selected, MaterialCardView v) {
        if (selected) {
            v.setStrokeColor(Color.parseColor("#F26225"));
            v.setStrokeWidth(2);
        } else {
            v.setStrokeWidth(0);
        }
    }

    private void getPhoto() {
        ImagePicker.Companion.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start((resultCode, data) -> {
                    if (resultCode == Activity.RESULT_OK) {
                        textViewGetPhoto.setText("Ulangi Ambil Foto");
                        fileUri = data.getData();
                        imageViewFood.setImageURI(fileUri);
                        String fileExtension = fileUri.getLastPathSegment().split("\\.")[1];
                        UUID uuid = UUID.randomUUID();
                        imageFileName = String.format("%s.%s", uuid.toString(), fileExtension);
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                });
    }

    private void handleFileUpload() {
        StorageReference storageReference = firebaseStorage.getReference()
                .child(String.format("%s/food_history/%s", userId, imageFileName));
        UploadTask uploadTask = storageReference.putFile(fileUri);
        uploadTask
                .addOnFailureListener(e -> Toast.makeText(this, "Upload image failed", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> submitData());
    }

    private void handleFoodData(Food food) {
        if (food.getImageUrl().contains("http")) {
            Glide.with(this).load(food.getImageUrl()).into(imageViewFood);
        } else {
            StorageReference storageReference = firebaseStorage.getReference(String.format("%s/food_history/%s", userId, food.getImageUrl()));
            Glide.with(this).load(storageReference).into(imageViewFood);
        }
        editTextJudul.setText(food.getTitle());
        editTextDescription.setText(food.getDescription());
        if (isRead) {
            cardViewGetPhoto.setVisibility(View.INVISIBLE);
            editTextJudul.setEnabled(false);
            editTextJudul.setTextColor(Color.parseColor("#000000"));
            editTextDescription.setEnabled(false);
            editTextDescription.setTextColor(Color.parseColor("#000000"));
            cardViewNasi.setOnClickListener(null);
            cardViewSayur.setOnClickListener(null);
            cardViewSusu.setOnClickListener(null);
            cardViewBuah.setOnClickListener(null);
            cardViewIkan.setOnClickListener(null);
            cardViewDaging.setOnClickListener(null);
            cardViewAyam.setOnClickListener(null);
            cardViewRoti.setOnClickListener(null);
            cardViewTelur.setOnClickListener(null);
        }
        handleFoodNutrient(food.getNutrientContent());
    }

    private void handleFoodNutrient(List<String> nutrientContent) {
        if (nutrientContent != null && nutrientContent.size() > 0) {
            for (String nutrient : nutrientContent) {
                handleFoodNutrientToCardRender(nutrient);
            }
        }
    }

    private void handleFoodNutrientToCardRender(String nutrient) {
        switch (nutrient.toLowerCase()) {
            case "nasi":
                nasiSelected = true;
                flipImageSelected(nasiSelected, cardViewNasi);
                break;
            case "susu":
                susuSelected = true;
                flipImageSelected(susuSelected, cardViewSusu);
                break;
            case "sayur":
                sayurSelected = true;
                flipImageSelected(sayurSelected, cardViewSayur);
                break;
            case "buah":
                buahSelected = true;
                flipImageSelected(buahSelected, cardViewBuah);
                break;
            case "daging":
                dagingSelected = true;
                flipImageSelected(dagingSelected, cardViewDaging);
                break;
            case "ayam":
                ayamSelected = true;
                flipImageSelected(ayamSelected, cardViewAyam);
                break;
            case "ikan":
                ikanSelected = true;
                flipImageSelected(ikanSelected, cardViewIkan);
                break;
            case "telur":
                telurSelected = true;
                flipImageSelected(telurSelected, cardViewTelur);
                break;
            case "roti":
                rotiSelected = true;
                flipImageSelected(rotiSelected, cardViewRoti);
                break;
        }
    }

    private void handlePreferences() {
        SharedPreferences sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Calendar calendar = Calendar.getInstance();
        CharSequence date = android.text.format.DateFormat.format("dd/MM/yyyy", calendar.getTime());
        editor.putString("date", date.toString());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12 && hour < 17) {
            editor.putBoolean("siang", true);
        } else if (hour >= 17 && hour < 24) {
            editor.putBoolean("malam", true);
        } else {
            editor.putBoolean("pagi", true);
        }
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        getSupportActionBar().setTitle("Tambah Makanan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        textViewGetPhoto = findViewById(R.id.textViewAmbilFoto);
        imageViewFood = findViewById(R.id.imageViewFood);
        editTextJudul = findViewById(R.id.editTextJudul);
        editTextDescription = findViewById(R.id.editTextDescription);
        cardViewGetPhoto = findViewById(R.id.cardViewAmbilFoto);
        cardViewNasi = findViewById(R.id.cardViewNasi);
        cardViewSayur = findViewById(R.id.cardViewSayur);
        cardViewSusu = findViewById(R.id.cardViewSusu);
        cardViewBuah = findViewById(R.id.cardViewBuah);
        cardViewIkan = findViewById(R.id.cardViewIkan);
        cardViewDaging = findViewById(R.id.cardViewDaging);
        cardViewAyam = findViewById(R.id.cardViewAyam);
        cardViewRoti = findViewById(R.id.cardViewRoti);
        cardViewTelur = findViewById(R.id.cardViewTelur);
        cardViewGetPhoto.setOnClickListener(v -> getPhoto());
        setCardViewOnClickListener();
        isRead = getIntent().getBooleanExtra("read", false);
        foodIntent = getIntent().getParcelableExtra("food");
        if (foodIntent != null) {
            getSupportActionBar().setTitle(foodIntent.getTitle());
            handleFoodData(foodIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isRead) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.action_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.submit) {
            if (imageFileName == null && foodIntent == null) {
                Toast.makeText(this, "Anda belum menambahkan gambar", Toast.LENGTH_SHORT).show();
            } else if (foodIntent != null && imageFileName == null) {
                submitData();
            } else {
                handleFileUpload();
            }
        }
        return true;
    }

    private void setCardViewOnClickListener() {
        cardViewNasi.setOnClickListener(v -> {
            nasiSelected = !nasiSelected;
            flipImageSelected(nasiSelected, (MaterialCardView) v);
        });
        cardViewSayur.setOnClickListener(v -> {
            sayurSelected = !sayurSelected;
            flipImageSelected(sayurSelected, (MaterialCardView) v);
        });
        cardViewSusu.setOnClickListener(v -> {
            susuSelected = !susuSelected;
            flipImageSelected(susuSelected, (MaterialCardView) v);
        });
        cardViewBuah.setOnClickListener(v -> {
            buahSelected = !buahSelected;
            flipImageSelected(buahSelected, (MaterialCardView) v);
        });
        cardViewIkan.setOnClickListener(v -> {
            ikanSelected = !ikanSelected;
            flipImageSelected(ikanSelected, (MaterialCardView) v);
        });
        cardViewDaging.setOnClickListener(v -> {
            dagingSelected = !dagingSelected;
            flipImageSelected(dagingSelected, (MaterialCardView) v);
        });
        cardViewAyam.setOnClickListener(v -> {
            ayamSelected = !ayamSelected;
            flipImageSelected(ayamSelected, (MaterialCardView) v);
        });
        cardViewRoti.setOnClickListener(v -> {
            rotiSelected = !rotiSelected;
            flipImageSelected(rotiSelected, (MaterialCardView) v);
        });
        cardViewTelur.setOnClickListener(v -> {
            telurSelected = !telurSelected;
            flipImageSelected(telurSelected, (MaterialCardView) v);
        });
    }

    private void submitData() {
        String title = editTextJudul.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        List<String> nutrientContent = new ArrayList<>();
        if (ayamSelected) nutrientContent.add("ayam");
        if (buahSelected) nutrientContent.add("buah");
        if (dagingSelected) nutrientContent.add("daging");
        if (ikanSelected) nutrientContent.add("ikan");
        if (nasiSelected) nutrientContent.add("nasi");
        if (rotiSelected) nutrientContent.add("roti");
        if (sayurSelected) nutrientContent.add("sayur");
        if (susuSelected) nutrientContent.add("susu");
        if (telurSelected) nutrientContent.add("telur");
        String imageUrl = imageFileName == null ? foodIntent.getImageUrl() : imageFileName;
        Food food = new Food(title, description, imageUrl, nutrientContent);
        db.collection("food_history").document(userId).collection("history").add(food)
                .addOnSuccessListener(aVoid -> {
                    handlePreferences();
                    Toast.makeText(this, "Tambah history makanan berhasil", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Tambah history makanan gagal", Toast.LENGTH_SHORT).show());
    }
}
