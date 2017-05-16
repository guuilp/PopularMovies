package com.github.guuilp.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.util.ImageSize;
import com.github.guuilp.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guilherme on 16/02/2017.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private final PopularMoviesOnClickHandler mClickHandler;
    private List<Movies.Result> movieList;
    private Context context;
    private static final String TAG = MoviesAdapter.class.getSimpleName();

    public interface PopularMoviesOnClickHandler {
        void onListItemClick(Movies.Result movie, ImageView shareImageView);
    }

    public MoviesAdapter(PopularMoviesOnClickHandler clickHandler, Context context) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForListItem = R.layout.popular_movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForListItem, parent, false);

        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        Movies.Result result = movieList.get(position);
        String url = NetworkUtils.buildCoverUrl(ImageSize.SMALL.toString(), result.getPosterPath());
        Picasso.with(context).load(url).into(holder.ivCoverMovie);

        ViewCompat.setTransitionName(holder.ivCoverMovie, result.getTitle());
    }

    @Override
    public int getItemCount() {
        if (null == movieList) return 0;
        return movieList.size();
    }

    public void setMovieList(List<Movies.Result> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView ivCoverMovie;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            ivCoverMovie = (ImageView) itemView.findViewById(R.id.iv_movie_cover);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movies.Result result = movieList.get(adapterPosition);
            mClickHandler.onListItemClick(result, ivCoverMovie);
        }
    }
}
