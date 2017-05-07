package com.github.guuilp.popularmovies.util;

import android.net.Uri;

import com.github.guuilp.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

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
}
