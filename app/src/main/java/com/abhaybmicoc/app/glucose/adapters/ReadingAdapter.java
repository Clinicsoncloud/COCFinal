package com.abhaybmicoc.app.glucose.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhaybmicoc.app.R;

import java.util.List;

/**
 * Created by maniteja on 2/7/18.
 */

public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.MyViewHolder> {

private List<String> moviesList;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title, year, genre;

    public MyViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        genre = (TextView) view.findViewById(R.id.genre);
        year = (TextView) view.findViewById(R.id.year);
    }
}


    public ReadingAdapter(List<String> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reading_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String movie = moviesList.get(position);
        String[] reading = movie.split("_");
        if(reading != null && reading.length == 3)
        {
            holder.title.setText(reading[0]);
            holder.genre.setText(reading[1]);
            holder.year.setText(reading[2]);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
