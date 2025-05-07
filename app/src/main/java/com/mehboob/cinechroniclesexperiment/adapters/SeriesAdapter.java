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
import com.mehboob.cinechroniclesexperiment.models.Series;

import java.util.ArrayList;
import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {
    private Context context;
    private List<Series> seriesList = new ArrayList<>();
    private OnSeriesClickListener listener;

    public interface OnSeriesClickListener {
        void onSeriesClick(Series series);
    }

    public SeriesAdapter(Context context) {
        this.context = context;
    }

    public void setOnSeriesClickListener(OnSeriesClickListener listener) {
        this.listener = listener;
    }

    public void setSeries(List<Series> seriesList) {
        this.seriesList = seriesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_series, parent, false);
        return new SeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        Series series = seriesList.get(position);

        holder.tvTitle.setText(series.getTitle());
        holder.tvSeasons.setText(series.getSeasons() + " Seasons");

        Glide.with(context)
                .load(series.getPosterUrl())
                .placeholder(R.drawable.placeholder_series)
                .error(R.drawable.placeholder_series)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSeriesClick(series);
            }
        });
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    public class SeriesViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvSeasons;

        public SeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSeasons = itemView.findViewById(R.id.tv_seasons);
        }
    }
}
