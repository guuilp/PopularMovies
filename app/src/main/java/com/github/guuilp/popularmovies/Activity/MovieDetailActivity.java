package com.github.guuilp.popularmovies.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.guuilp.popularmovies.model.Result;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.util.ImageSize;
import com.github.guuilp.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity{

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private Result movie;

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

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            movie = bundle.getParcelable(Result.PARCELABLE_KEY);
        }

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            String url = NetworkUtils.buildCoverUrl(ImageSize.DEFAULT.toString(), movie.getPosterPath());
            Picasso.with(this).load(url).into(mMoviePoster);

            url = NetworkUtils.buildCoverUrl(ImageSize.DEFAULT.toString(), movie.getBackdropPath());
            Picasso.with(this).load(url).into(mMovieBanner);
        } else {
            String url = NetworkUtils.buildCoverUrl(ImageSize.DEFAULT.toString(), movie.getPosterPath());
            Picasso.with(this).load(url).into(mMoviePoster);

            url = NetworkUtils.buildCoverUrl(ImageSize.A_LITTLE_BIGGER.toString(), movie.getBackdropPath());
            Picasso.with(this).load(url).into(mMovieBanner);
        }

        mOriginalTitle.setText(movie.getTitle());
        mPlotSynopsis.setText(movie.getOverview());
        mUserRating.setText(String.valueOf(movie.getVoteAverage()) + getString(R.string.total_rating_movie));
        mReleaseDate.setText(movie.getReleaseDate());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Result.PARCELABLE_KEY, null);
        super.onSaveInstanceState(outState);
    }

}