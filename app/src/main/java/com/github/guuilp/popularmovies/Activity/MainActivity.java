package com.github.guuilp.popularmovies.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.net.Network;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.guuilp.popularmovies.Adapter.PopularMoviesAdapter;
import com.github.guuilp.popularmovies.Model.Movies;
import com.github.guuilp.popularmovies.Model.Result;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.Util.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.PopularMoviesOnClickHandler{

    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources rs = getResources();
        int numColumns = rs.getInteger(R.integer.list_columns);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_popular_movies);
        mErrorMessageDisplay = (TextView)findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Resource based on https://discussions.udacity.com/t/is-there-a-way-to-fit-columns-in-gridlayoutmanager/221936/4
        GridLayoutManager layoutManager = new GridLayoutManager(this, numColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPopularMoviesAdapter = new PopularMoviesAdapter(this, this);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        loadMoviesData(NetworkUtils.POPULAR_SORT);
    }

    private void loadMoviesData(String sortBy){
        showMoviesDataView();

        new FetchMoviesTask().execute(sortBy);
    }

    @Override
    public void onListItemClick(Result result) {
        Toast.makeText(MainActivity.this, "Implementar a segunda tela", Toast.LENGTH_LONG).show();
    }

    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
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
            mPopularMoviesAdapter.setMovieList(null);
            loadMoviesData(NetworkUtils.POPULAR_SORT);
            return true;
        }
        if(itemId == R.id.sort_top_rated) {
            mPopularMoviesAdapter.setMovieList(null);
            loadMoviesData(NetworkUtils.TOP_RATED_SORT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String sortBy = params[0];
            URL moviesUrl = NetworkUtils.buildUrl(sortBy);

            return NetworkUtils.returnDataFromWebService(moviesUrl);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "onPostExecute");
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(result != null){
                showMoviesDataView();
                Gson gson = new Gson();
                Movies data = gson.fromJson(result, Movies.class);
                mPopularMoviesAdapter.setMovieList(data);
            } else {
                showErrorMessage();
            }
        }
    }
}