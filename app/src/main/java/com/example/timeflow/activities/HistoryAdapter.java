package com.example.timeflow.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timeflow.R;
import com.example.timeflow.models.ActivityModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<ActivityModel> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(ActivityModel activity);
        void onDeleteClick(ActivityModel activity);
    }

    public HistoryAdapter(List<ActivityModel> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ActivityModel a = list.get(position);
        h.tvTitle.setText(a.getTitle());
        
        // Formater la durée en heures et minutes
        long duration = a.getDuration();
        if (duration >= 60) {
            long hours = duration / 60;
            long minutes = duration % 60;
            if (minutes > 0) {
                h.tvDuration.setText("⏱️ " + hours + "h " + minutes + "min");
            } else {
                h.tvDuration.setText("⏱️ " + hours + "h");
            }
        } else {
            h.tvDuration.setText("⏱️ " + duration + " min");
        }

        // Afficher la description
        if (a.getDescription() == null || a.getDescription().isEmpty()) {
            h.tvDescription.setVisibility(View.GONE);
        } else {
            h.tvDescription.setVisibility(View.VISIBLE);
            h.tvDescription.setText(a.getDescription());
        }

        // Afficher la date
        if (h.tvDate != null) {
            Timestamp createdAt = a.getCreatedAt();
            if (createdAt != null) {
                Date date = createdAt.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy 'à' HH:mm", Locale.getDefault());
                h.tvDate.setText("📅 " + sdf.format(date));
                h.tvDate.setVisibility(View.VISIBLE);
            } else {
                h.tvDate.setVisibility(View.GONE);
            }
        }

        // Listeners pour les boutons
        h.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(a);
            }
        });

        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(a);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDuration, tvDescription, tvDate;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDuration = v.findViewById(R.id.tvDuration);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvDate = v.findViewById(R.id.tvDate);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
