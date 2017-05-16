package com.github.guuilp.popularmovies.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.github.guuilp.popularmovies.util.Sort;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListActivity extends AppCompatActivity implements MoviesAdapter.PopularMoviesOnClickHandler {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final String TAG = MoviesListActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_IMAGE_TRANSITION_NAME = "movie_image_transition_name";

    private List<Movies.Result> moviesData = null;

    private String[] projection = {MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_POSTER_IMAGE,
            MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE,
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Movies.Result.PARCELABLE_KEY, new ArrayList<Parcelable>(moviesData));
        super.onSaveInstanceState(outState);
    }

    private void loadMoviesData(Sort sortBy){
        if (isOnline()){
            showMoviesDataView();
            TheMovieDBService service = TheMovieDBService.retrofit.create(TheMovieDBService.class);

            mLoadingIndicator.setVisibility(View.VISIBLE);

            if(sortBy == Sort.POPULAR){
                processPopularMovies(service);
            } else if (sortBy == Sort.TOP_RATED){
                processTopRatedMovies(service);
            } else {
                processFavoriteMovies();
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

    //Got this method here: http://stackoverflow.com/a/4009133/3394588
    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
            loadMoviesData(Sort.POPULAR);
            return true;
        }

        if(itemId == R.id.sort_top_rated) {
            mMoviesAdapter.setMovieList(null);
            loadMoviesData(Sort.TOP_RATED);
            return true;
        }

        if(itemId == R.id.sort_favorites){
            mMoviesAdapter.setMovieList(null);
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
        PopularMoviesQueryHandler popularMoviesQueryHandler = new PopularMoviesQueryHandler(getContentResolver());

        List<Object> lista = new ArrayList<>();
        lista.add(0, mMoviesAdapter);
        lista.add(1, moviesData);

        Cursor query = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        query.moveToFirst();

        while (query.isAfterLast() == false){
            DatabaseUtils.dumpCursor(query);
//            Log.d("Test", query.getString(0));
            query.moveToNext();
        }

//        popularMoviesQueryHandler.startQuery(PopularMoviesQueryHandler.ID_MOVIE_TOKEN_ALL,
//                lista,
//                MoviesContract.MoviesEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                null);

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        showMoviesDataView();
    }
}
