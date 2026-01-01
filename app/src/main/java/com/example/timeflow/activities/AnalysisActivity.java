package com.example.timeflow.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.timeflow.R;
import com.example.timeflow.views.SimplePieChart;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisActivity extends AppCompatActivity {

    private TextView tvTotalTime, tvActivityCount, tvAverage, tvTodayTime, tvTodayCount, tvAdvice;
    private SimplePieChart pieChart;
    private LinearLayout legendContainer;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvActivityCount = findViewById(R.id.tvActivityCount);
        tvAverage = findViewById(R.id.tvAverage);
        tvTodayTime = findViewById(R.id.tvTodayTime);
        tvTodayCount = findViewById(R.id.tvTodayCount);
        tvAdvice = findViewById(R.id.tvAdvice);
        pieChart = findViewById(R.id.pieChart);
        legendContainer = findViewById(R.id.legendContainer);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadAnalysis();
    }

    private void loadAnalysis() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        // Calculer le début de la journée
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();
        Timestamp startOfDayTimestamp = new Timestamp(startOfDay);

        // Charger toutes les activités
        db.collection("users")
                .document(uid)
                .collection("activities")
                .get()
                .addOnSuccessListener(query -> {

                    long totalMinutes = 0;
                    int count = 0;
                    long todayMinutes = 0;
                    int todayCount = 0;
                    
                    // Catégories pour analyse
                    Map<String, Long> categoryTime = new HashMap<>();
                    categoryTime.put("Études", 0L);
                    categoryTime.put("Loisirs", 0L);
                    categoryTime.put("Sport", 0L);
                    categoryTime.put("Autre", 0L);

                    for (QueryDocumentSnapshot doc : query) {
                        Long duration = doc.getLong("duration");
                        Timestamp createdAt = doc.getTimestamp("createdAt");
                        String title = doc.getString("title");
                        
                        if (duration != null) {
                            totalMinutes += duration;
                            count++;
                            
                            // Vérifier si l'activité est d'aujourd'hui
                            if (createdAt != null && createdAt.compareTo(startOfDayTimestamp) >= 0) {
                                todayMinutes += duration;
                                todayCount++;
                            }
                            
                            // Catégoriser l'activité
                            String category = categorizeActivity(title);
                            categoryTime.put(category, categoryTime.get(category) + duration);
                        }
                    }

                    // Afficher les statistiques
                    displayStats(totalMinutes, count, todayMinutes, todayCount);
                    
                    // Afficher le graphique
                    displayChart(categoryTime);
                    
                    // Générer les conseils
                    generateAdvice(categoryTime, totalMinutes);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private String categorizeActivity(String title) {
        if (title == null) return "Autre";
        
        String lower = title.toLowerCase();
        if (lower.contains("étude") || lower.contains("cours") || lower.contains("devoir") || 
            lower.contains("révision") || lower.contains("examen") || lower.contains("lecture") ||
            lower.contains("apprendre") || lower.contains("travail")) {
            return "Études";
        } else if (lower.contains("sport") || lower.contains("gym") || lower.contains("course") ||
                   lower.contains("entraînement") || lower.contains("football") || lower.contains("basket")) {
            return "Sport";
        } else if (lower.contains("jeu") || lower.contains("film") || lower.contains("série") ||
                   lower.contains("musique") || lower.contains("divertissement") || lower.contains("loisir") ||
                   lower.contains("sortie") || lower.contains("amusement")) {
            return "Loisirs";
        }
        return "Autre";
    }

    private void displayStats(long totalMinutes, int count, long todayMinutes, int todayCount) {
        // Statistiques globales
        tvActivityCount.setText(String.valueOf(count));
        
        // Convertir en heures et minutes
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        if (hours > 0) {
            tvTotalTime.setText(hours + "h " + minutes + "min");
        } else {
            tvTotalTime.setText(totalMinutes + " min");
        }

        if (count > 0) {
            long average = totalMinutes / count;
            tvAverage.setText(average + " min");
        } else {
            tvAverage.setText("0 min");
        }

        // Statistiques du jour
        long todayHours = todayMinutes / 60;
        long todayMins = todayMinutes % 60;
        if (todayHours > 0) {
            tvTodayTime.setText(todayHours + "h " + todayMins + "min");
        } else {
            tvTodayTime.setText(todayMinutes + " min");
        }
        
        tvTodayCount.setText(String.valueOf(todayCount));
    }

    private void displayChart(Map<String, Long> categoryTime) {
        List<SimplePieChart.SliceData> slices = new ArrayList<>();
        long total = 0;
        
        for (Long value : categoryTime.values()) {
            total += value;
        }
        
        if (total == 0) {
            pieChart.setVisibility(View.GONE);
            return;
        }
        
        pieChart.setVisibility(View.VISIBLE);
        
        // Couleurs pour chaque catégorie - Thème Orange/Noir
        int colorEtudes = ContextCompat.getColor(this, R.color.neonOrange);
        int colorLoisirs = ContextCompat.getColor(this, R.color.neonYellow);
        int colorSport = Color.parseColor("#00D4FF");
        int colorAutre = Color.parseColor("#999999");
        
        // Créer les slices
        if (categoryTime.get("Études") > 0) {
            slices.add(new SimplePieChart.SliceData(
                "Études",
                categoryTime.get("Études"),
                colorEtudes,
                Color.WHITE
            ));
        }
        
        if (categoryTime.get("Loisirs") > 0) {
            slices.add(new SimplePieChart.SliceData(
                "Loisirs",
                categoryTime.get("Loisirs"),
                colorLoisirs,
                Color.WHITE
            ));
        }
        
        if (categoryTime.get("Sport") > 0) {
            slices.add(new SimplePieChart.SliceData(
                "Sport",
                categoryTime.get("Sport"),
                colorSport,
                Color.WHITE
            ));
        }
        
        if (categoryTime.get("Autre") > 0) {
            slices.add(new SimplePieChart.SliceData(
                "Autre",
                categoryTime.get("Autre"),
                colorAutre,
                Color.WHITE
            ));
        }
        
        pieChart.setData(slices);
        
        // Créer la légende
        legendContainer.removeAllViews();
        for (SimplePieChart.SliceData slice : slices) {
            LinearLayout legendItem = new LinearLayout(this);
            legendItem.setOrientation(LinearLayout.HORIZONTAL);
            legendItem.setPadding(0, 8, 0, 8);
            legendItem.setGravity(Gravity.CENTER_VERTICAL);
            
            // Color dot
            View dot = new View(this);
            dot.setBackgroundColor(slice.color);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(24, 24);
            dotParams.setMargins(0, 0, 12, 0);
            dot.setLayoutParams(dotParams);
            
            // Label
            TextView label = new TextView(this);
            long percentage = Math.round((slice.value / (float) total) * 100);
            label.setText(slice.label + " (" + percentage + "%)");
            label.setTextSize(12);
            label.setTextColor(ContextCompat.getColor(this, R.color.textDark));
            
            legendItem.addView(dot);
            legendItem.addView(label);
            legendContainer.addView(legendItem);
        }
    }

    private void generateAdvice(Map<String, Long> categoryTime, long totalMinutes) {
        long etudesTime = categoryTime.get("Études");
        long loisirsTime = categoryTime.get("Loisirs");
        long sportTime = categoryTime.get("Sport");
        long total = etudesTime + loisirsTime + sportTime + categoryTime.get("Autre");
        
        if (total == 0) {
            tvAdvice.setText("Ajoutez des activités pour recevoir des conseils personnalisés.");
            return;
        }
        
        StringBuilder advice = new StringBuilder();
        
        // Analyse études vs loisirs
        float etudesPercent = (etudesTime / (float) total) * 100;
        float loisirsPercent = (loisirsTime / (float) total) * 100;
        float sportPercent = (sportTime / (float) total) * 100;
        
        // Conseils sur l'équilibre
        if (etudesPercent > 70) {
            advice.append("• Vous consacrez beaucoup de temps aux études. Pensez à prendre des pauses régulières pour maintenir votre productivité.\n\n");
        } else if (etudesPercent < 30 && totalMinutes > 120) {
            advice.append("• Vous pourriez augmenter votre temps d'étude pour de meilleurs résultats.\n\n");
        }
        
        if (loisirsPercent > 50) {
            advice.append("• Vous avez beaucoup de temps de loisir. Essayez d'équilibrer avec vos études pour un meilleur rendement.\n\n");
        } else if (loisirsPercent < 20 && totalMinutes > 180) {
            advice.append("• N'oubliez pas de prendre du temps pour vous détendre. Les loisirs sont importants pour votre bien-être.\n\n");
        }
        
        if (sportPercent < 10 && totalMinutes > 240) {
            advice.append("• L'activité physique est essentielle. Essayez d'inclure au moins 30 minutes de sport par jour.\n\n");
        } else if (sportPercent > 0) {
            advice.append("• Excellent ! Vous maintenez une activité physique régulière.\n\n");
        }
        
        // Conseils sur le temps total
        if (totalMinutes < 60) {
            advice.append("• Vous avez enregistré peu d'activités. Essayez de suivre votre temps plus régulièrement.\n\n");
        } else if (totalMinutes > 600) {
            advice.append("• Vous êtes très actif ! Assurez-vous de bien vous reposer pour maintenir cet élan.\n\n");
        }
        
        // Conseils d'étude selon le temps de loisir
        if (loisirsTime > 0 && etudesTime > 0) {
            float ratio = loisirsTime / (float) etudesTime;
            if (ratio > 1.5) {
                advice.append("💡 Conseil d'étude : Vous avez plus de temps de loisir que d'études. Essayez la technique Pomodoro : 25 min d'étude, 5 min de pause. Cela vous permettra d'étudier efficacement tout en gardant du temps pour vous.\n\n");
            } else if (ratio < 0.5) {
                advice.append("💡 Conseil d'étude : Vous étudiez beaucoup. Utilisez vos moments de loisir pour réviser de manière détendue (écouter des podcasts éducatifs, regarder des documentaires).\n\n");
            } else {
                advice.append("💡 Conseil d'étude : Vous avez un bon équilibre ! Continuez à alterner études et loisirs pour maintenir votre motivation.\n\n");
            }
        }
        
        // Conseils généraux
        if (advice.length() == 0) {
            advice.append("• Continuez à suivre vos activités pour obtenir des analyses plus détaillées.\n\n");
            advice.append("• Essayez d'équilibrer études, loisirs et sport pour un mode de vie sain.");
        }
        
        tvAdvice.setText(advice.toString().trim());
    }
}
