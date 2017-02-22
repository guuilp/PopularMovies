package com.github.guuilp.popularmovies.service;

import android.os.AsyncTask;

import com.github.guuilp.popularmovies.util.NetworkUtils;
import com.github.guuilp.popularmovies.util.Sort;

import java.net.URL;

/**
 * Created by Guilherme on 22/02/2017.
 */

public class MovieService extends AsyncTask<String, Void, String> {

    private AsyncTaskDelegate delegate = null;

    public MovieService(AsyncTaskDelegate responder){
        this.delegate = responder;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(delegate != null) {
            delegate.processStart();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        Sort sortBy = Sort.fromCode(params[0]);
        URL moviesUrl = NetworkUtils.buildUrl(sortBy);

        return NetworkUtils.returnDataFromWebService(moviesUrl);
    }

    @Override
    protected void onPostExecute(String result) {
        if(delegate != null) {
            delegate.processFinish(result);
        }
    }
}