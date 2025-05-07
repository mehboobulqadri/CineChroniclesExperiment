package com.mehboob.cinechroniclesexperiment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.mehboob.cinechroniclesexperiment.R;
import com.mehboob.cinechroniclesexperiment.models.Note;

import java.util.UUID;

public class AddEditNoteDialog extends Dialog {
    private final Context context;
    private final Note note;
    private final NoteDialogListener listener;

    private EditText etTitle;
    private EditText etContent;
    private EditText etMovieTitle;
    private Button btnSave;
    private Button btnCancel;
    private TextView tvDialogTitle;

    public interface NoteDialogListener {
        void onNoteSaved(Note note);
    }

    public AddEditNoteDialog(@NonNull Context context, Note note, NoteDialogListener listener) {
        super(context);
        this.context = context;
        this.note = note;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_edit_note);

        // Initialize views
        etTitle = findViewById(R.id.et_note_title);
        etContent = findViewById(R.id.et_note_content);
        etMovieTitle = findViewById(R.id.et_movie_title);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        tvDialogTitle = findViewById(R.id.tv_dialog_title);

        // Set dialog title based on mode (add or edit)
        if (note != null) {
            tvDialogTitle.setText("Edit Note");
            populateFields();
        } else {
            tvDialogTitle.setText("Add Note");
        }

        // Set click listeners
        btnSave.setOnClickListener(v -> saveNote());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void populateFields() {
        etTitle.setText(note.getTitle());
        etContent.setText(note.getContent());
        etMovieTitle.setText(note.getMovieTitle());
    }

    private void saveNote() {
        // Validate inputs
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String movieTitle = etMovieTitle.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title cannot be empty");
            return;
        }

        if (content.isEmpty()) {
            etContent.setError("Content cannot be empty");
            return;
        }

        // Create or update note
        Note savedNote;
        if (note != null) {
            // Update existing note
            savedNote = note;
            savedNote.setTitle(title);
            savedNote.setContent(content);
            savedNote.setMovieTitle(movieTitle);
            savedNote.setTimestamp(System.currentTimeMillis());
        } else {
            // Create new note
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            savedNote = new Note(
                    UUID.randomUUID().toString(),
                    title,
                    content,
                    "", // movieId
                    movieTitle,
                    "", // moviePosterUrl
                    userId
            );
        }

        // Notify listener
        if (listener != null) {
            listener.onNoteSaved(savedNote);
        }

        // Dismiss dialog
        dismiss();
    }
}
