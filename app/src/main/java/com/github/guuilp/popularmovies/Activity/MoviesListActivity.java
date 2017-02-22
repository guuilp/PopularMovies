package com.github.guuilp.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.adapter.PopularMoviesAdapter;
import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.model.Result;
import com.github.guuilp.popularmovies.service.AsyncTaskDelegate;
import com.github.guuilp.popularmovies.service.MovieService;
import com.github.guuilp.popularmovies.util.Sort;
import com.google.gson.Gson;

public class MoviesListActivity extends AppCompatActivity implements PopularMoviesAdapter.PopularMoviesOnClickHandler, AsyncTaskDelegate {

    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final String TAG = MoviesListActivity.class.getSimpleName();

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

        if(mPopularMoviesAdapter == null)
            mPopularMoviesAdapter = new PopularMoviesAdapter(this, this);

        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey(Result.PARCELABLE_KEY)){
            loadMoviesData(Sort.POPULAR);
        } else {
            moviesData = savedInstanceState.getParcelable(Result.PARCELABLE_KEY);
            mPopularMoviesAdapter.setMovieList(moviesData);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Result.PARCELABLE_KEY, moviesData);
        super.onSaveInstanceState(outState);
    }

    private void loadMoviesData(Sort sortBy){
        if (isOnline()){
            showMoviesDataView();
            new MovieService(this).execute(String.valueOf(sortBy));
        } else {
            showErrorMessage(getString(R.string.connection_error_message));
        }
    }

    @Override
    public void onListItemClick(Result result) {
        Intent intent = new Intent(MoviesListActivity.this, MovieDetailActivity.class);
        intent.putExtra(Result.PARCELABLE_KEY, result);
        startActivity(intent);
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
            mPopularMoviesAdapter.setMovieList(null);
            loadMoviesData(Sort.POPULAR);
            return true;
        }

        if(itemId == R.id.sort_top_rated) {
            mPopularMoviesAdapter.setMovieList(null);
            loadMoviesData(Sort.TOP_RATED);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processStart() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void processFinish(String result) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(result != null){
            showMoviesDataView();

            Gson gson = new Gson();
            moviesData = gson.fromJson(result, Movies.class);

            mPopularMoviesAdapter.setMovieList(moviesData);
        } else {
            showErrorMessage(getString(R.string.error_message));
        }
    }
}