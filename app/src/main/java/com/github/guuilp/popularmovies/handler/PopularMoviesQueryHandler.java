package com.github.guuilp.popularmovies.handler;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.adapter.MoviesAdapter;
import com.github.guuilp.popularmovies.data.VideosContract;
import com.github.guuilp.popularmovies.model.Movies;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guilherme on 14/05/2017.
 */

public class PopularMoviesQueryHandler extends AsyncQueryHandler {

    public static final int ID_MOVIE_TOKEN = 44;
    public static final int ID_MOVIE_TOKEN_ALL = 54;
    public static final int ID_REVIEW_TOKEN = 45;
    public static final int ID_VIDEO_TOKEN = 46;

    public PopularMoviesQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        switch(token){
            case ID_MOVIE_TOKEN:
                //Verifies if the insertion worked. If so, paints with pink the heart on the action bar.
                if(ContentUris.parseId(uri) != -1){
                    MenuItem item = (MenuItem) cookie;

                    item.setIcon(R.drawable.ic_favorite_pink_24px);
                }

                break;
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int deletedRows) {
        switch(token){
            case ID_MOVIE_TOKEN:
                if(deletedRows > 0){
                    MenuItem item = (MenuItem) cookie;

                    item.setIcon(R.drawable.ic_favorite_white_24px);
                }

                break;
        }
    }

}
