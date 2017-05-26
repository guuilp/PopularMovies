package com.github.guuilp.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.github.guuilp.popularmovies.data.MoviesContract.*;
import com.github.guuilp.popularmovies.data.ReviewsContract.*;
import com.github.guuilp.popularmovies.data.VideosContract.*;
/**
 * Created by Guilherme on 06/05/2017.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popularmovies.db";
    private static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
            MoviesEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MoviesEntry.COLUMN_POSTER_PATH       + " TEXT NOT NULL,"                     +
            MoviesEntry.COLUMN_ADULT             + " INTEGER NOT NULL,"                  +
            MoviesEntry.COLUMN_OVERVIEW          + " TEXT NOT NULL,"                     +
            MoviesEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL,"                     +
            MoviesEntry.COLUMN_ID                + " INTEGER NOT NULL,"                  +
            MoviesEntry.COLUMN_ORIGINAL_TITLE    + " TEXT NOT NULL,"                     +
            MoviesEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL,"                     +
            MoviesEntry.COLUMN_TITLE             + " TEXT NOT NULL,"                     +
            MoviesEntry.COLUMN_BACKDROP_PATH     + " TEXT NOT NULL,"                     +
            MoviesEntry.COLUMN_POPULARITY        + " REAL NOT NULL,"                     +
            MoviesEntry.COLUMN_VOTECOUNT         + " INTEGER NOT NULL,"                  +
            MoviesEntry.COLUMN_VIDEO             + " INTEGER NOT NULL,"                  +
            MoviesEntry.COLUMN_VOTE_AVERAGE      + " REAL NOT NULL,"                     +
            "UNIQUE (" + MoviesEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

    private static final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
            ReviewsEntry._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReviewsEntry.COLUMN_ID       + " TEXT NOT NULL,"                     +
            ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"                  +
            ReviewsEntry.COLUMN_AUTHOR   + " TEXT NOT NULL,"                     +
            ReviewsEntry.COLUMN_CONTENT  + " TEXT NOT NULL,"                     +
            ReviewsEntry.COLUMN_URL      + " TEXT NOT NULL,"                     +
            "UNIQUE (" + ReviewsEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

    private static final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideosEntry.TABLE_NAME + " (" +
            VideosEntry._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            VideosEntry.COLUMN_ID       + " TEXT NOT NULL,"                     +
            VideosEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"                  +
            VideosEntry.COLUMN_ISO6391  + " TEXT NOT NULL,"                     +
            VideosEntry.COLUMN_ISO31661 + " TEXT NOT NULL,"                     +
            VideosEntry.COLUMN_KEY      + " TEXT NOT NULL,"                     +
            VideosEntry.COLUMN_NAME     + " TEXT NOT NULL,"                     +
            VideosEntry.COLUMN_SITE     + " TEXT NOT NULL,"                     +
            VideosEntry.COLUMN_SIZE     + " INTEGER NOT NULL,"                  +
            VideosEntry.COLUMN_TYPE     + " TEXT NOT NULL,"                     +
            "UNIQUE (" + VideosEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideosEntry.TABLE_NAME);
        onCreate(db);
    }
}
