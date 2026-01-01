package com.example.timeflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeflow.R;
import com.example.timeflow.models.ActivityModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView tvEmpty;
    private HistoryAdapter adapter;
    private final List<ActivityModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recycler = findViewById(R.id.recyclerHistory);
        tvEmpty = findViewById(R.id.tvEmpty);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(list);
        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(ActivityModel activity) {
                // Ouvrir l'écran de modification
                Intent intent = new Intent(HistoryActivity.this, EditTimeActivity.class);
                intent.putExtra("activityId", activity.getDocumentId());
                intent.putExtra("title", activity.getTitle());
                intent.putExtra("duration", activity.getDuration());
                intent.putExtra("description", activity.getDescription() != null ? activity.getDescription() : "");
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(ActivityModel activity) {
                // Confirmer la suppression
                new AlertDialog.Builder(HistoryActivity.this)
                        .setTitle("Supprimer l'activité")
                        .setMessage("Êtes-vous sûr de vouloir supprimer cette activité ?")
                        .setPositiveButton("SUPPRIMER", (dialog, which) -> deleteActivity(activity))
                        .setNegativeButton("ANNULER", null)
                        .show();
            }
        });
        recycler.setAdapter(adapter);

        loadData();
    }

    private void deleteActivity(ActivityModel activity) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || activity.getDocumentId() == null) {
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("activities")
                .document(activity.getDocumentId())
                .delete()
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Activité supprimée", Toast.LENGTH_SHORT).show();
                    loadData(); // Recharger la liste
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void loadData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Erreur de connexion");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("activities")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    list.clear();

                    for (var doc : snapshot) {
                        ActivityModel activity = doc.toObject(ActivityModel.class);
                        activity.setDocumentId(doc.getId()); // Stocker l'ID du document
                        list.add(activity);
                    }

                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Erreur lors du chargement");
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Recharger après modification
    }
}
