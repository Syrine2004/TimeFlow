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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTimeActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etHours, etMinutes, etDescription;
    private MaterialButton btnSave;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        etTitle = findViewById(R.id.etTitle);
        etHours = findViewById(R.id.etHours);
        etMinutes = findViewById(R.id.etMinutes);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveActivity());
    }

    private void saveActivity() {

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

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        // Data Firestore
        Map<String, Object> activity = new HashMap<>();
        activity.put("title", title);
        activity.put("duration", duration);
        activity.put("description", description);
        activity.put("createdAt", FieldValue.serverTimestamp());

        // Save
        db.collection("users")
                .document(uid)
                .collection("activities")
                .add(activity)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Activité ajoutée", Toast.LENGTH_SHORT).show();
                    finish(); // retour Home
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
