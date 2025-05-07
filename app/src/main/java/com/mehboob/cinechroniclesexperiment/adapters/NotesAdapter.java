package com.mehboob.cinechroniclesexperiment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mehboob.cinechroniclesexperiment.R;
import com.mehboob.cinechroniclesexperiment.models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private final Context context;
    private final List<Note> notes;
    private final OnNoteClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onDeleteClick(Note note);
    }

    public NotesAdapter(Context context, List<Note> notes, OnNoteClickListener listener) {
        this.context = context;
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());

        // Format and set date
        String formattedDate = dateFormat.format(new Date(note.getTimestamp()));
        holder.tvDate.setText(formattedDate);

        // Set movie title
        if (note.getMovieTitle() != null && !note.getMovieTitle().isEmpty()) {
            holder.tvMovieTitle.setText(note.getMovieTitle());
            holder.tvMovieTitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvMovieTitle.setVisibility(View.GONE);
        }

        // Load movie poster if available
        if (note.getMoviePosterUrl() != null && !note.getMoviePosterUrl().isEmpty()) {
            Glide.with(context)
                    .load(note.getMoviePosterUrl())
                    .placeholder(R.drawable.placeholder_movie)
                    .error(R.drawable.placeholder_movie)
                    .into(holder.ivMoviePoster);
            holder.ivMoviePoster.setVisibility(View.VISIBLE);
        } else {
            holder.ivMoviePoster.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(note);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;
        TextView tvDate;
        TextView tvMovieTitle;
        ImageView ivMoviePoster;
        ImageView ivDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvContent = itemView.findViewById(R.id.tv_note_content);
            tvDate = itemView.findViewById(R.id.tv_note_date);
            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            ivMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
