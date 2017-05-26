package com.github.guuilp.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.guuilp.popularmovies.BuildConfig;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.adapter.MoviesAdapter;
import com.github.guuilp.popularmovies.data.MoviesContract;
import com.github.guuilp.popularmovies.handler.PopularMoviesQueryHandler;
import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.service.TheMovieDBService;
import com.github.guuilp.popularmovies.util.NetworkUtils;
import com.github.guuilp.popularmovies.util.Sort;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MoviesAdapter.PopularMoviesOnClickHandler {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final String TAG = MoviesListActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_IMAGE_TRANSITION_NAME = "movie_image_transition_name";

    public List<Movies.Result> moviesData = null;

    private static final int ID_MOVIES_LOADER = 22;

    private String[] projection = {MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_POSTER_PATH,
            MoviesContract.MoviesEntry.COLUMN_ADULT,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_ID,
            MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH,
            MoviesContract.MoviesEntry.COLUMN_POPULARITY,
            MoviesContract.MoviesEntry.COLUMN_VOTECOUNT,
            MoviesContract.MoviesEntry.COLUMN_VIDEO,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        Resources rs = getResources();
        int numColumns = rs.getInteger(R.integer.list_columns);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_popular_movies);
        mErrorMessageDisplay = (TextView)findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Resource based on https://discussions.udacity.com/t/is-there-a-way-to-fit-columns-in-gridlayoutmanager/221936/4
        GridLayoutManager layoutManager = new GridLayoutManager(this, numColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        if(mMoviesAdapter == null)
            mMoviesAdapter = new MoviesAdapter(this, this);

        mRecyclerView.setAdapter(mMoviesAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey(Movies.Result.PARCELABLE_KEY)){
            loadMoviesData(Sort.POPULAR);
        } else {
            moviesData = savedInstanceState.getParcelableArrayList(Movies.Result.PARCELABLE_KEY);
            mMoviesAdapter.setMovieList(moviesData);
        }

        getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Movies.Result.PARCELABLE_KEY, new ArrayList<Parcelable>(moviesData));
        super.onSaveInstanceState(outState);
    }

    private void loadMoviesData(Sort sortBy){
        if (sortBy == Sort.FAVORITES){
            showMoviesDataView();

            mLoadingIndicator.setVisibility(View.VISIBLE);

            processFavoriteMovies();
        } else if (NetworkUtils.isOnline(this)){
            showMoviesDataView();

            TheMovieDBService service = TheMovieDBService.retrofit.create(TheMovieDBService.class);

            mLoadingIndicator.setVisibility(View.VISIBLE);

            if(sortBy == Sort.POPULAR){
                processPopularMovies(service);
            } else if (sortBy == Sort.TOP_RATED){
                processTopRatedMovies(service);
            }
        } else {
            showErrorMessage(getString(R.string.connection_error_message));
        }
    }

    @Override
    public void onListItemClick(Movies.Result result, ImageView shareImageView) {
        Intent intent = new Intent(MoviesListActivity.this, MovieDetailActivity.class);
        intent.putExtra(Movies.Result.PARCELABLE_KEY, result);
        intent.putExtra(EXTRA_MOVIE_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                shareImageView,
                ViewCompat.getTransitionName(shareImageView));

        startActivity(intent, options.toBundle());
    }

    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setText(errorMessage);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.sort_popular){
            mMoviesAdapter.setMovieList(null);
            moviesData = null;
            loadMoviesData(Sort.POPULAR);
            return true;
        }

        if(itemId == R.id.sort_top_rated) {
            mMoviesAdapter.setMovieList(null);
            moviesData = null;
            loadMoviesData(Sort.TOP_RATED);
            return true;
        }

        if(itemId == R.id.sort_favorites){
            mMoviesAdapter.setMovieList(null);
            moviesData = null;
            loadMoviesData(Sort.FAVORITES);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void processTopRatedMovies(TheMovieDBService service) {
        Call<Movies> topRatedMovies = service.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);

        topRatedMovies.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.d(TAG, call.request().url().toString());

                mLoadingIndicator.setVisibility(View.INVISIBLE);

                if(response != null){
                    showMoviesDataView();

                    mMoviesAdapter.setMovieList(response.body().getResults());

                    moviesData = response.body().getResults();
                } else {
                    showErrorMessage(getString(R.string.error_message));
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                showErrorMessage(getString(R.string.error_message));
            }
        });
    }

    private void processPopularMovies(TheMovieDBService service) {
        Call<Movies> popularMovies = service.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);

        popularMovies.enqueue(new Callback<Movies>() {

            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);

                if(response != null){
                    showMoviesDataView();

                    mMoviesAdapter.setMovieList(response.body().getResults());

                    moviesData = response.body().getResults();
                } else {
                    showErrorMessage(getString(R.string.error_message));
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                showErrorMessage(getString(R.string.error_message));
            }
        });
    }

    private void processFavoriteMovies() {
        getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case ID_MOVIES_LOADER:
                return new CursorLoader(this,
                        MoviesContract.MoviesEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        List<Movies.Result> moviesDataFromCursor = new ArrayList<>();

        try {
            while (data.moveToNext()) {
                moviesDataFromCursor.add(Movies.Result.fromCursor(data));
            }
        } finally {
            data.close();
        }

        if(moviesDataFromCursor.size() > 0){
            moviesData = moviesDataFromCursor;

            mMoviesAdapter.setMovieList(moviesDataFromCursor);

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            showMoviesDataView();
        } else {
            moviesData = null;
            mMoviesAdapter.setMovieList(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesData = null;
        mMoviesAdapter.setMovieList(null);
    }
}
