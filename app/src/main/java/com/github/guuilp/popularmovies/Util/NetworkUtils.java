package com.github.guuilp.popularmovies.Util;

import android.net.Uri;
import android.util.Log;

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
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POPULAR_URL = "http://api.themoviedb.org/3/movie/popular";

    private static final String TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated";

    private static final String COVER_IMAGE_URL = "http://image.tmdb.org/t/p/";

    private static final String API_KEY = "";

    public static final String POPULAR_SORT = "Popular";
    public static final String TOP_RATED_SORT = "Top Rated";

    public static URL buildUrl(String sortBy){
        String baseURl;

        if(sortBy.equals("Popular")){
            baseURl = POPULAR_URL;
        } else {
            baseURl = TOP_RATED_URL;
        }

        Uri builtUri = Uri.parse(baseURl).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static String buildCoverUrl(String size, String posterPath){
        posterPath = posterPath.substring(1);
        Uri builtUri = Uri.parse(COVER_IMAGE_URL).buildUpon()
                .appendPath(size)
                .appendPath(posterPath)
                .build();

        return builtUri.toString();
    }

    public static String returnDataFromWebService(URL moviesUrl){
        HttpURLConnection urlConn = null;

        try{
            urlConn = (HttpURLConnection) moviesUrl.openConnection();

            InputStream in = urlConn.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if(hasInput){
                return scanner.next();
            } else{
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConn.disconnect();
        }

        return null;
    }
}
