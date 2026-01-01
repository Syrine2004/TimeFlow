package com.example.timeflow.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timeflow.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditTimeActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etHours, etMinutes, etDescription;
    private MaterialButton btnSave;
    private String activityId;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        // Récupérer les données de l'activité
        activityId = getIntent().getStringExtra("activityId");
        String title = getIntent().getStringExtra("title");
        long duration = getIntent().getLongExtra("duration", 0);
        String description = getIntent().getStringExtra("description");

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Modifier l'activité");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        etTitle = findViewById(R.id.etTitle);
        etHours = findViewById(R.id.etHours);
        etMinutes = findViewById(R.id.etMinutes);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        // Pré-remplir les champs
        if (title != null) etTitle.setText(title);
        if (description != null) etDescription.setText(description);
        
        // Convertir la durée en heures et minutes
        long hours = duration / 60;
        long minutes = duration % 60;
        etHours.setText(String.valueOf(hours));
        etMinutes.setText(String.valueOf(minutes));

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSave.setText("Modifier");
        btnSave.setOnClickListener(v -> updateActivity());
    }

    private void updateActivity() {
        String title = etTitle.getText() != null
                ? etTitle.getText().toString().trim()
                : "";

        String hoursStr = etHours.getText() != null
                ? etHours.getText().toString().trim()
                : "0";

        String minutesStr = etMinutes.getText() != null
                ? etMinutes.getText().toString().trim()
                : "0";

        String description = etDescription.getText() != null
                ? etDescription.getText().toString().trim()
                : "";

        // Validation
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Nom requis");
            etTitle.requestFocus();
            return;
        }

        int hours, minutes;
        try {
            hours = Integer.parseInt(hoursStr);
            minutes = Integer.parseInt(minutesStr);
            if (hours < 0 || minutes < 0 || minutes >= 60) {
                throw new NumberFormatException();
            }
            if (hours == 0 && minutes == 0) {
                etHours.setError("Durée requise");
                etHours.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etHours.setError("Durée invalide");
            etHours.requestFocus();
            return;
        }

        // Convertir en minutes totales
        int duration = (hours * 60) + minutes;

        if (auth.getCurrentUser() == null || activityId == null) {
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        // Mettre à jour dans Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("duration", duration);
        updates.put("description", description);

        db.collection("users")
                .document(uid)
                .collection("activities")
                .document(activityId)
                .update(updates)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Activité modifiée", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}

