package com.github.guuilp.popularmovies.service;

import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.model.Reviews;
import com.github.guuilp.popularmovies.model.Videos;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Guilherme on 03/05/2017.
 */

public interface TheMovieDBService {
    @GET("/3/movie/top_rated")
    Call<Movies> getTopRatedMovies(@Query("api_key") String api_key);

    @GET("/3/movie/popular")
    Call<Movies> getPopularMovies(@Query("api_key") String api_key);

    @GET("/3/movie/{movieId}/videos")
    Call<Videos> getVideos(@Path("movieId") Integer movieId, @Query("api_key") String api_key);

    @GET("/3/movie/{movieId}/reviews")
    Call<Reviews> getReviews(@Path("movieId") Integer movieId, @Query("api_key") String api_key);

    Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://api.themoviedb.org/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
}
