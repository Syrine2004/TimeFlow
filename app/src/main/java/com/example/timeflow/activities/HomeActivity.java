package com.example.timeflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timeflow.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private TextView tvTotalTime, tvActivityCount, tvUserEmail, tvWelcome;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Sécurité : utilisateur non connecté
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Views
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvActivityCount = findViewById(R.id.tvActivityCount);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvWelcome = findViewById(R.id.tvWelcome);

        String userEmail = auth.getCurrentUser().getEmail();
        tvUserEmail.setText(userEmail != null ? userEmail : "Utilisateur");

        // Message de bienvenue
        if (tvWelcome != null) {
            String welcomeMessage = getWelcomeMessage();
            tvWelcome.setText(welcomeMessage);
        }

        // Charger les stats du jour
        loadStats();
        
        // Animation d'entrée
        findViewById(R.id.cardMain).startAnimation(
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in)
        );

        // ➕ Ajouter activité
        findViewById(R.id.btnAddActivity).setOnClickListener(v ->
                startActivity(new Intent(this, AddTimeActivity.class))
        );

        // 📜 Historique
        findViewById(R.id.cardHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class))
        );

        // 📊 Analyse
        findViewById(R.id.cardAnalysis).setOnClickListener(v ->
                startActivity(new Intent(this, AnalysisActivity.class))
        );

        // 👤 Profil
        findViewById(R.id.cardProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );
    }

    private String getWelcomeMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hour < 12) {
            return "Bonjour";
        } else if (hour < 18) {
            return "Bon après-midi";
        } else {
            return "Bonsoir";
        }
    }

    private void loadStats() {
        String uid = auth.getCurrentUser().getUid();

        // Calculer le début de la journée
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();
        Timestamp startOfDayTimestamp = new Timestamp(startOfDay);

        db.collection("users")
                .document(uid)
                .collection("activities")
                .get()
                .addOnSuccessListener(query -> {

                    long todayMinutes = 0;
                    int todayCount = 0;

                    for (QueryDocumentSnapshot doc : query) {
                        Long duration = doc.getLong("duration");
                        Timestamp createdAt = doc.getTimestamp("createdAt");
                        
                        // Compter seulement les activités d'aujourd'hui
                        if (duration != null && createdAt != null && 
                            createdAt.compareTo(startOfDayTimestamp) >= 0) {
                            todayMinutes += duration;
                            todayCount++;
                        }
                    }

                    // Formater le temps
                    if (todayMinutes >= 60) {
                        long hours = todayMinutes / 60;
                        long minutes = todayMinutes % 60;
                        if (minutes > 0) {
                            tvTotalTime.setText(hours + "h " + minutes + "min");
                        } else {
                            tvTotalTime.setText(hours + "h");
                        }
                    } else {
                        tvTotalTime.setText(todayMinutes + " min");
                    }
                    
                    tvActivityCount.setText(String.valueOf(todayCount));
                })
                .addOnFailureListener(e -> {
                    tvTotalTime.setText("0 min");
                    tvActivityCount.setText("0");
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharge les stats après retour depuis Add / History
        loadStats();
    }
}

