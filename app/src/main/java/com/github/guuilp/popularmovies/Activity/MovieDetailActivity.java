package com.github.guuilp.popularmovies.Activity;

import android.content.Intent;
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
    private TextView mOriginalTitle;
    private TextView mPlotSynopsis;
    private TextView mUserRating;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_cover);
        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        mPlotSynopsis = (TextView) findViewById(R.id.tv_plot_synopsis);
        mUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        Result result = getIntent().getExtras().getParcelable("movie");

//        String url = NetworkUtils.buildCoverUrl("w185", result.getPosterPath());
//        Picasso.with(this).load(url).into(mMoviePoster);

        mOriginalTitle.setText(result.getTitle());
        mPlotSynopsis.setText(result.getOverview());
        mUserRating.setText(String.valueOf(result.getVoteAverage()));
        mReleaseDate.setText(result.getReleaseDate());

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")){

        } else {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", null);
        super.onSaveInstanceState(outState);
    }

}