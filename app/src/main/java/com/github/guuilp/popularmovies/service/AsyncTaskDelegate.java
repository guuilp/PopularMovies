package com.github.guuilp.popularmovies.service;

/**
 * Created by Guilherme on 22/02/2017.
 */

public interface AsyncTaskDelegate {
    void processStart();
    void processFinish(String result);
}
