package com.github.guuilp.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Guilherme on 06/05/2017.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.github.guuilp.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "originalLanguage";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTECOUNT = "voteCount";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
    }
}