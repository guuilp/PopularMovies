package com.github.guuilp.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Guilherme on 06/05/2017.
 */

public class VideosContract {

    public static final String CONTENT_AUTHORITY = "com.github.guuilp.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_VIDEOS = "videos";

    public static final class VideosEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VIDEOS)
                .build();

        public static final String TABLE_NAME = "videos";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_ISO6391 = "iso6391";
        public static final String COLUMN_ISO31661 = "iso31661";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";
    }
}