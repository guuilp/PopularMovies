package com.github.guuilp.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        Cursor cursor;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_MOVIES_WITH_DATE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MoviesEntry.COLUMN_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_VIDEOS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        VideosContract.VideosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_VIDEOS_WITH_DATE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        VideosContract.VideosEntry.TABLE_NAME,
                        projection,
                        VideosContract.VideosEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_REVIEWS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        ReviewsContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_REVIEWS_WITH_DATE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        ReviewsContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        ReviewsContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:{
                long id = mOpenHelper.getWritableDatabase().insert(
                            MoviesContract.MoviesEntry.TABLE_NAME,
                            null,
                            values);
                if(id > 0) returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into: " + uri);

                break;
            }

            case CODE_VIDEOS:{
                long id = mOpenHelper.getWritableDatabase().insert(
                        VideosContract.VideosEntry.TABLE_NAME,
                        null,
                        values);
                if(id > 0) returnUri = ContentUris.withAppendedId(VideosContract.VideosEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into: " + uri);

                break;
            }

            case CODE_REVIEWS:{
                long id = mOpenHelper.getWritableDatabase().insert(
                        ReviewsContract.ReviewsEntry.TABLE_NAME,
                        null,
                        values);
                if(id > 0) returnUri = ContentUris.withAppendedId(ReviewsContract.ReviewsEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into: " + uri);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)){
            case CODE_VIDEOS: {
                db.beginTransaction();
                int rowsInserted = 0;

                try{
                    for(ContentValues value : values){
                        long _id = db.insert(VideosContract.VideosEntry.TABLE_NAME, null, value);

                        if(_id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            }

            case CODE_REVIEWS: {
                db.beginTransaction();
                int rowsInserted = 0;

                try{
                    for(ContentValues value : values){
                        long _id = db.insert(ReviewsContract.ReviewsEntry.TABLE_NAME, null, value);

                        if(_id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int tasksDeleted;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:{
                tasksDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        null,
                        null);
                break;
            }

            case CODE_MOVIES_WITH_DATE:{
                tasksDeleted = mOpenHelper.getWritableDatabase().delete(
                                    MoviesContract.MoviesEntry.TABLE_NAME,
                                    MoviesContract.MoviesEntry.COLUMN_ID + " = ?",
                                    new String[]{uri.getPathSegments().get(1)});
                break;
            }

            case CODE_VIDEOS_WITH_DATE:{
                tasksDeleted = mOpenHelper.getWritableDatabase().delete(
                                    VideosContract.VideosEntry.TABLE_NAME,
                                    VideosContract.VideosEntry.COLUMN_MOVIE_ID + " = ?",
                                    new String[]{uri.getPathSegments().get(1)});
                break;
            }

            case CODE_REVIEWS_WITH_DATE:{
                tasksDeleted = mOpenHelper.getWritableDatabase().delete(
                                    ReviewsContract.ReviewsEntry.TABLE_NAME,
                                    ReviewsContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                                    new String[]{uri.getPathSegments().get(1)});
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
