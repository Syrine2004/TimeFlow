package com.example.timeflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timeflow.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvCreatedAt, tvAvatar;
    private MaterialButton btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvAvatar = findViewById(R.id.tvAvatar);
        btnLogout = findViewById(R.id.btnLogout);

        MaterialToolbar toolbar = findViewById(R.id.toolbarProfile);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadProfile();

        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadProfile() {

        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this, "Profil introuvable", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String name = doc.getString("name");
                    String email = doc.getString("email");
                    
                    tvName.setText(name != null ? name : "Utilisateur");
                    tvEmail.setText(email != null ? email : "");

                    // Générer l'avatar avec l'initiale
                    if (name != null && !name.isEmpty()) {
                        String initial = String.valueOf(name.charAt(0)).toUpperCase();
                        tvAvatar.setText(initial);
                    } else if (email != null && !email.isEmpty()) {
                        String initial = String.valueOf(email.charAt(0)).toUpperCase();
                        tvAvatar.setText(initial);
                    } else {
                        tvAvatar.setText("U");
                    }

                    Date date = doc.getDate("createdAt");
                    if (date != null) {
                        String formatted = new SimpleDateFormat(
                                "dd MMM yyyy", Locale.getDefault()
                        ).format(date);
                        tvCreatedAt.setText("Compte créé le " + formatted);
                    } else {
                        tvCreatedAt.setText("Compte créé le --");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
