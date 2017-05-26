package com.github.guuilp.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * Created by Guilherme on 16/02/2017.
 */

public final class NetworkUtils {

    private static final String COVER_IMAGE_URL = "http://image.tmdb.org/t/p/";

    public static String buildCoverUrl(String size, String posterPath){
        posterPath = posterPath.substring(1);
        Uri builtUri = Uri.parse(COVER_IMAGE_URL).buildUpon()
                .appendPath(size)
                .appendPath(posterPath)
                .build();

        return builtUri.toString();
    }

    //Got this method here: http://stackoverflow.com/a/4009133/3394588
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
