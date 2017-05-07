package com.github.guuilp.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Guilherme on 06/05/2017.
 */

public class ReviewsContract {

    public static final String CONTENT_AUTHORITY = "com.github.guuilp.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_REVIEWS = "reviews";

    public static final class ReviewsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS)
                .build();

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
    }
}