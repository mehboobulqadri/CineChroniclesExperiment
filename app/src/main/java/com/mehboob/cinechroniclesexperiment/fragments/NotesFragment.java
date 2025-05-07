package com.mehboob.cinechroniclesexperiment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mehboob.cinechroniclesexperiment.adapters.NotesAdapter;
import com.mehboob.cinechroniclesexperiment.databinding.FragmentNotesBinding;
import com.mehboob.cinechroniclesexperiment.dialogs.AddEditNoteDialog;
import com.mehboob.cinechroniclesexperiment.models.Note;
import com.mehboob.cinechroniclesexperiment.utils.NotesManager;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment implements NotesAdapter.OnNoteClickListener {
    private FragmentNotesBinding binding;
    private NotesAdapter notesAdapter;
    private NotesManager notesManager;
    private List<Note> notesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize notes manager
        notesManager = new NotesManager(requireContext());

        // Set up RecyclerView
        setupRecyclerView();

        // Set up FAB
        binding.fabAddNote.setOnClickListener(v -> showAddNoteDialog());

        // Load notes
        loadNotes();
    }

    private void setupRecyclerView() {
        binding.rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        notesAdapter = new NotesAdapter(requireContext(), notesList, this);
        binding.rvNotes.setAdapter(notesAdapter);
    }

    private void loadNotes() {
        // Show loading indicator
        binding.progressBar.setVisibility(View.VISIBLE);

        notesManager.getAllNotes(new NotesManager.NotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                binding.progressBar.setVisibility(View.GONE);

                notesList.clear();
                notesList.addAll(notes);
                notesAdapter.notifyDataSetChanged();

                // Show empty state if no notes
                updateEmptyState();
            }

            @Override
            public void onNoteAdded(Note note) {
                // Not used here
            }

            @Override
            public void onNoteUpdated(Note note) {
                // Not used here
            }

            @Override
            public void onNoteDeleted(String noteId) {
                // Not used here
            }

            @Override
            public void onError(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (notesList.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
            binding.rvNotes.setVisibility(View.GONE);
        } else {
            binding.emptyState.setVisibility(View.GONE);
            binding.rvNotes.setVisibility(View.VISIBLE);
        }
    }

    private void showAddNoteDialog() {
        AddEditNoteDialog dialog = new AddEditNoteDialog(requireContext(), null, new AddEditNoteDialog.NoteDialogListener() {
            @Override
            public void onNoteSaved(Note note) {
                addNote(note);
            }
        });
        dialog.show();
    }

    private void showEditNoteDialog(Note note) {
        AddEditNoteDialog dialog = new AddEditNoteDialog(requireContext(), note, new AddEditNoteDialog.NoteDialogListener() {
            @Override
            public void onNoteSaved(Note updatedNote) {
                updateNote(updatedNote);
            }
        });
        dialog.show();
    }

    private void addNote(Note note) {
        notesManager.addNote(note, new NotesManager.NotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                // Not used here
            }

            @Override
            public void onNoteAdded(Note addedNote) {
                // Add to list and update UI
                notesList.add(0, addedNote); // Add to beginning of list
                notesAdapter.notifyItemInserted(0);
                binding.rvNotes.scrollToPosition(0);

                // Update empty state
                updateEmptyState();

                Toast.makeText(requireContext(), "Note added successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoteUpdated(Note note) {
                // Not used here
            }

            @Override
            public void onNoteDeleted(String noteId) {
                // Not used here
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNote(Note note) {
        notesManager.updateNote(note, new NotesManager.NotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                // Not used here
            }

            @Override
            public void onNoteAdded(Note note) {
                // Not used here
            }

            @Override
            public void onNoteUpdated(Note updatedNote) {
                // Update in list and refresh UI
                for (int i = 0; i < notesList.size(); i++) {
                    if (notesList.get(i).getId().equals(updatedNote.getId())) {
                        notesList.set(i, updatedNote);
                        notesAdapter.notifyItemChanged(i);
                        break;
                    }
                }

                Toast.makeText(requireContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoteDeleted(String noteId) {
                // Not used here
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNote(String noteId) {
        notesManager.deleteNote(noteId, new NotesManager.NotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                // Not used here
            }

            @Override
            public void onNoteAdded(Note note) {
                // Not used here
            }

            @Override
            public void onNoteUpdated(Note note) {
                // Not used here
            }

            @Override
            public void onNoteDeleted(String deletedNoteId) {
                // Remove from list and update UI
                for (int i = 0; i < notesList.size(); i++) {
                    if (notesList.get(i).getId().equals(deletedNoteId)) {
                        notesList.remove(i);
                        notesAdapter.notifyItemRemoved(i);
                        break;
                    }
                }

                // Update empty state
                updateEmptyState();

                Toast.makeText(requireContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNoteClick(Note note) {
        showEditNoteDialog(note);
    }

    @Override
    public void onDeleteClick(Note note) {
        deleteNote(note.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
