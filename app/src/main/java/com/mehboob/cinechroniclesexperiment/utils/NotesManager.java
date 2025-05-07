package com.mehboob.cinechroniclesexperiment.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mehboob.cinechroniclesexperiment.models.Note;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;

public class NotesManager {
    private static final String TAG = "NotesManager";
    private static final String NOTES_KEY = "user_notes";

    private final Context context;
    private final PreferenceManager preferenceManager;
    private final DatabaseReference notesRef;
    private final String userId;

    public interface NotesCallback {
        void onNotesLoaded(List<Note> notes);
        void onNoteAdded(Note note);
        void onNoteUpdated(Note note);
        void onNoteDeleted(String noteId);
        void onError(String errorMessage);
    }

    public NotesManager(Context context) {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);

        // Get current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase reference
        notesRef = FirebaseDatabase.getInstance().getReference().child("notes").child(userId);
    }

    public void getAllNotes(NotesCallback callback) {
        // First try to get from Firebase
        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Note> notes = new ArrayList<>();

                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    if (note != null) {
                        notes.add(note);
                    }
                }

                // Sort notes by timestamp (newest first)
                Collections.sort(notes, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));

                // Save to local storage for offline access
                saveNotesLocally(notes);

                callback.onNotesLoaded(notes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading notes from Firebase", databaseError.toException());

                // Try to load from local storage as fallback
                List<Note> localNotes = getNotesFromLocalStorage();
                callback.onNotesLoaded(localNotes);
                callback.onError("Failed to load notes from server: " + databaseError.getMessage());
            }
        });
    }

    public void addNote(Note note, NotesCallback callback) {
        // Generate ID if not provided
        if (note.getId() == null || note.getId().isEmpty()) {
            note.setId(UUID.randomUUID().toString());
        }

        // Set user ID
        note.setUserId(userId);

        // Set timestamp if not set
        if (note.getTimestamp() == 0) {
            note.setTimestamp(System.currentTimeMillis());
        }

        // Save to Firebase
        notesRef.child(note.getId()).setValue(note)
                .addOnSuccessListener(aVoid -> {
                    // Add to local cache
                    List<Note> notes = getNotesFromLocalStorage();
                    notes.add(note);
                    saveNotesLocally(notes);

                    callback.onNoteAdded(note);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding note", e);
                    callback.onError("Failed to add note: " + e.getMessage());
                });
    }

    public void updateNote(Note note, NotesCallback callback) {
        // Ensure note has an ID
        if (note.getId() == null || note.getId().isEmpty()) {
            callback.onError("Cannot update note without ID");
            return;
        }

        // Update timestamp
        note.setTimestamp(System.currentTimeMillis());

        // Update in Firebase
        notesRef.child(note.getId()).setValue(note)
                .addOnSuccessListener(aVoid -> {
                    // Update in local cache
                    List<Note> notes = getNotesFromLocalStorage();
                    for (int i = 0; i < notes.size(); i++) {
                        if (notes.get(i).getId().equals(note.getId())) {
                            notes.set(i, note);
                            break;
                        }
                    }
                    saveNotesLocally(notes);

                    callback.onNoteUpdated(note);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating note", e);
                    callback.onError("Failed to update note: " + e.getMessage());
                });
    }

    public void deleteNote(String noteId, NotesCallback callback) {
        // Delete from Firebase
        notesRef.child(noteId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remove from local cache
                    List<Note> notes = getNotesFromLocalStorage();
                    notes.removeIf(note -> note.getId().equals(noteId));
                    saveNotesLocally(notes);

                    callback.onNoteDeleted(noteId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting note", e);
                    callback.onError("Failed to delete note: " + e.getMessage());
                });
    }

    private List<Note> getNotesFromLocalStorage() {
        String notesJson = preferenceManager.getString(NOTES_KEY, "");
        if (notesJson.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Note>>() {}.getType();
            return gson.fromJson(notesJson, type);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing notes from local storage", e);
            return new ArrayList<>();
        }
    }

    private void saveNotesLocally(List<Note> notes) {
        try {
            Gson gson = new Gson();
            String notesJson = gson.toJson(notes);
            preferenceManager.setString(NOTES_KEY, notesJson);
        } catch (Exception e) {
            Log.e(TAG, "Error saving notes to local storage", e);
        }
    }
}
