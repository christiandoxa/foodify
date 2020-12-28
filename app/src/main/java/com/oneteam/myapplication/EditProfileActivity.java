package com.oneteam.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private EditText editTextBio;
    private EditText editTextPhone;
    private ImageView imageView;

    private void fetchBio() {
        db.collection("user").document(firebaseUser.getUid()).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Map<String, Object> data = snapshot.getData();
                String bio = data.get("bio").toString();
                String phone = data.get("phone").toString();
                if (bio != null) {
                    editTextBio.setText(bio);
                }
                if (phone != null) {
                    editTextPhone.setText(phone);
                }
            } else {
                Log.d(TAG, "Current data: null");
                editTextBio.setHint("ex: never be lazy");
                editTextPhone.setHint("ex: 082284773992");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.profileImage);
        editTextBio = findViewById(R.id.editTextBio);
        editTextPhone = findViewById(R.id.editTextTelepon);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageView);
        db = FirebaseFirestore.getInstance();
        fetchBio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.submit) {
            if (editTextPhone.getText().toString().isEmpty() || editTextBio.getText().toString().isEmpty()) {
                Toast.makeText(this, "Harap lengkapi data", Toast.LENGTH_SHORT).show();
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("bio", editTextBio.getText().toString());
                data.put("phone", editTextPhone.getText().toString());
                db.collection("user").document(firebaseUser.getUid()).set(data)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Update data berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update data, please try again.", Toast.LENGTH_SHORT).show();
                        });
            }
        }
        return true;
    }
}
