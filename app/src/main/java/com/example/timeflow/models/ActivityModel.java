package com.example.timeflow.models;

import com.google.firebase.Timestamp;

public class ActivityModel {

    private String title;
    private long duration;
    private String description;
    private Timestamp createdAt;
    private String documentId; // ID du document Firestore

    public ActivityModel() {}

    // Getters
    public String getTitle() { return title; }
    public long getDuration() { return duration; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getDocumentId() { return documentId; }

    // Setters pour Firestore
    public void setTitle(String title) { this.title = title; }
    public void setDuration(long duration) { this.duration = duration; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}
