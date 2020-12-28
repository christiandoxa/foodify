package com.oneteam.myapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oneteam.myapplication.EditProfileActivity;
import com.oneteam.myapplication.LoginActivity;
import com.oneteam.myapplication.R;

import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private CardView cardViewLogout;
    private ImageView imageViewEdit;
    private ImageView imageViewPhoto;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewBio;
    private TextView textViewTelepon;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

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
                if (bio == null) {
                    textViewBio.setText("-");
                } else {
                    textViewBio.setText(bio);
                }
                if (phone == null) {
                    textViewTelepon.setText("-");
                } else {
                    textViewTelepon.setText(phone);
                }
            } else {
                Log.d(TAG, "Current data: null");
                textViewBio.setText("-");
                textViewTelepon.setText("-");
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == imageViewEdit.getId()) {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        } else if (id == cardViewLogout.getId()) {
            signOut();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseUser = firebaseAuth.getCurrentUser();
        textViewName = view.findViewById(R.id.textViewName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewBio = view.findViewById(R.id.textViewBio);
        textViewTelepon = view.findViewById(R.id.textViewTelepon);
        imageViewPhoto = view.findViewById(R.id.profileImage);
        imageViewEdit = view.findViewById(R.id.imageViewEdit);
        cardViewLogout = view.findViewById(R.id.cardViewLogout);
        textViewName.setText(firebaseUser.getDisplayName());
        textViewEmail.setText(firebaseUser.getEmail());
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageViewPhoto);
        imageViewEdit.setOnClickListener(this);
        cardViewLogout.setOnClickListener(this);
        fetchBio();
    }

    private void signOut() {
        firebaseAuth.signOut();
        goToLogin();
    }
}
