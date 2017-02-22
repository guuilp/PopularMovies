package com.github.guuilp.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.model.Result;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.util.ImageSize;
import com.github.guuilp.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Guilherme on 16/02/2017.
 */
public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder>{

    private final PopularMoviesOnClickHandler mClickHandler;
    private Movies movieList;
    private Context context;
    private static final String TAG = PopularMoviesAdapter.class.getSimpleName();

    public interface PopularMoviesOnClickHandler{
        void onListItemClick(Result movie);
    }

    public PopularMoviesAdapter(PopularMoviesOnClickHandler clickHandler, Context context) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    @Override
    public PopularMoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForListItem = R.layout.popular_movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForListItem, parent, false);

        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularMoviesViewHolder holder, int position) {
        Result result = movieList.getResults().get(position);
        String url = NetworkUtils.buildCoverUrl(ImageSize.SMALL.toString(), result.getPosterPath());
        Picasso.with(context).load(url).into(holder.ivCoverMovie);
    }

    @Override
    public int getItemCount() {
        if(null == movieList) return 0;
        if(null == movieList.getResults()) return 0;
        return movieList.getResults().size();
    }

    public void setMovieList(Movies movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView ivCoverMovie;

        public PopularMoviesViewHolder(View itemView) {
            super(itemView);
            ivCoverMovie = (ImageView) itemView.findViewById(R.id.iv_movie_cover);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result result = movieList.getResults().get(adapterPosition);
            mClickHandler.onListItemClick(result);
        }
    }
}
