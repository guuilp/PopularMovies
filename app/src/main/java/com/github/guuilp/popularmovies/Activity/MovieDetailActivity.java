package com.github.guuilp.popularmovies.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.guuilp.popularmovies.Model.Result;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.Util.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MovieDetailActivity extends AppCompatActivity{

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private ImageView mMoviePoster;
    private ImageView mMovieBanner;
    private TextView mOriginalTitle;
    private TextView mPlotSynopsis;
    private TextView mUserRating;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mMovieBanner = (ImageView) findViewById(R.id.tv_movie_banner);
        mOriginalTitle = (TextView) findViewById(R.id.tv_movie_title);
        mPlotSynopsis = (TextView) findViewById(R.id.tv_movie_overview);
        mUserRating = (TextView) findViewById(R.id.tv_movie_rating);
        mReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);

        Result result = getIntent().getExtras().getParcelable("movie");
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            String url = NetworkUtils.buildCoverUrl("w500", result.getPosterPath());
            Picasso.with(this).load(url).into(mMoviePoster);

            url = NetworkUtils.buildCoverUrl("w500", result.getBackdropPath());
            Picasso.with(this).load(url).into(mMovieBanner);
        } else {
            String url = NetworkUtils.buildCoverUrl("w500", result.getPosterPath());
            Picasso.with(this).load(url).into(mMoviePoster);

            url = NetworkUtils.buildCoverUrl("w780", result.getBackdropPath());
            Picasso.with(this).load(url).into(mMovieBanner);
        }

        mOriginalTitle.setText(result.getTitle());
        mPlotSynopsis.setText(result.getOverview());
        mUserRating.setText(String.valueOf(result.getVoteAverage()) + "/10");
        mReleaseDate.setText(result.getReleaseDate());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", null);
        super.onSaveInstanceState(outState);
    }

}