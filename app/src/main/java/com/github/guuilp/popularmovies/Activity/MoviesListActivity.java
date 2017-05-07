package com.github.guuilp.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.guuilp.popularmovies.BuildConfig;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.adapter.MoviesAdapter;
import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.service.TheMovieDBService;
import com.github.guuilp.popularmovies.util.Sort;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListActivity extends AppCompatActivity implements MoviesAdapter.PopularMoviesOnClickHandler {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final String TAG = MoviesListActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ITEM = "movie_image_url";
    public static final String EXTRA_MOVIE_IMAGE_TRANSITION_NAME = "movie_image_transition_name";

    private Movies moviesData = null;

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
            moviesData = savedInstanceState.getParcelable(Movies.Result.PARCELABLE_KEY);
            mMoviesAdapter.setMovieList(moviesData);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Movies.Result.PARCELABLE_KEY, moviesData);
        super.onSaveInstanceState(outState);
    }

    private void loadMoviesData(Sort sortBy){
        if (isOnline()){
            showMoviesDataView();
            TheMovieDBService service = TheMovieDBService.retrofit.create(TheMovieDBService.class);

            mLoadingIndicator.setVisibility(View.VISIBLE);

            if(sortBy == Sort.POPULAR){
                processPopularMovies(service);
            } else {
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

                    mMoviesAdapter.setMovieList(response.body());
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

                    mMoviesAdapter.setMovieList(response.body());
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
}