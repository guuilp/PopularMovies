package com.github.guuilp.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Guilherme on 06/05/2017.
 */

public class PopularMoviesProvider extends ContentProvider{

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_WITH_DATE = 101;
    public static final int CODE_VIDEOS = 200;
    public static final int CODE_VIDEOS_WITH_DATE = 201;
    public static final int CODE_REVIEWS = 300;
    public static final int CODE_REVIEWS_WITH_DATE = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopularMoviesDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String moviesAuthority = MoviesContract.CONTENT_AUTHORITY;
        final String reviewsAuthority = ReviewsContract.CONTENT_AUTHORITY;
        final String videosAuthority = VideosContract.CONTENT_AUTHORITY;

        matcher.addURI(moviesAuthority, MoviesContract.PATH_MOVIES, CODE_MOVIES);
        matcher.addURI(moviesAuthority, MoviesContract.PATH_MOVIES + "/#", CODE_MOVIES_WITH_DATE);

        matcher.addURI(reviewsAuthority, ReviewsContract.PATH_REVIEWS, CODE_REVIEWS);
        matcher.addURI(reviewsAuthority, ReviewsContract.PATH_REVIEWS + "/#", CODE_REVIEWS_WITH_DATE);

        matcher.addURI(videosAuthority, VideosContract.PATH_VIDEOS, CODE_VIDEOS);
        matcher.addURI(videosAuthority, VideosContract.PATH_VIDEOS + "/#", CODE_VIDEOS_WITH_DATE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
