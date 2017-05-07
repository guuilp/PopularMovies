package com.github.guuilp.popularmovies.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.guuilp.popularmovies.BuildConfig;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.adapter.VideoAdapter;
import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.model.Reviews;
import com.github.guuilp.popularmovies.model.Videos;
import com.github.guuilp.popularmovies.service.TheMovieDBService;
import com.github.guuilp.popularmovies.util.ImageSize;
import com.github.guuilp.popularmovies.util.NetworkUtils;
import com.github.guuilp.popularmovies.util.Sort;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private Movies.Result movie;

    private ImageView mMoviePoster;
    private ImageView mMovieBanner;
    private TextView mOriginalTitle;
    private TextView mPlotSynopsis;
    private TextView mUserRating;
    private TextView mReleaseDate;
    private ListView mTrailers;
    private SwipeSelector mReviews;

    private List<Videos.Result> twoVideosData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        supportPostponeEnterTransition();

        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mMovieBanner = (ImageView) findViewById(R.id.tv_movie_banner);
        mOriginalTitle = (TextView) findViewById(R.id.tv_movie_title);
        mPlotSynopsis = (TextView) findViewById(R.id.tv_movie_overview);
        mUserRating = (TextView) findViewById(R.id.tv_movie_rating);
        mReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mTrailers = (ListView) findViewById(R.id.lv_trailers);
        mReviews = (SwipeSelector) findViewById(R.id.ssReview);

        Bundle bundle = getIntent().getExtras();


        if (bundle != null){
            movie = bundle.getParcelable(Movies.Result.PARCELABLE_KEY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String imageTransitionName = bundle.getString(MoviesListActivity.EXTRA_MOVIE_IMAGE_TRANSITION_NAME);
                mMoviePoster.setTransitionName(imageTransitionName);
            }
        }

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            String url = NetworkUtils.buildCoverUrl(ImageSize.DEFAULT.toString(), movie.getPosterPath());

            Picasso.with(this)
                    .load(url)
                    .noFade()
                    .into(mMoviePoster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportStartPostponedEnterTransition();
                        }
                    });

            url = NetworkUtils.buildCoverUrl(ImageSize.DEFAULT.toString(), movie.getBackdropPath());
            Picasso.with(this).load(url).into(mMovieBanner);
        } else {
            String url = NetworkUtils.buildCoverUrl(ImageSize.DEFAULT.toString(), movie.getPosterPath());

            Picasso.with(this)
                    .load(url)
                    .noFade()
                    .into(mMoviePoster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportStartPostponedEnterTransition();
                        }
                    });

            url = NetworkUtils.buildCoverUrl(ImageSize.A_LITTLE_BIGGER.toString(), movie.getBackdropPath());
            Picasso.with(this).load(url).into(mMovieBanner);
        }

        mOriginalTitle.setText(movie.getTitle());
        mPlotSynopsis.setText(movie.getOverview());
        mUserRating.setText(String.valueOf(movie.getVoteAverage()) + getString(R.string.total_rating_movie));
        mReleaseDate.setText(movie.getReleaseDate());

        TheMovieDBService service = TheMovieDBService.retrofit.create(TheMovieDBService.class);

        processVideos(service);

        mTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Videos.Result video = (Videos.Result) mTrailers.getItemAtPosition(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + video.getKey())));
            }
        });

        processReviews(service);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Movies.Result.PARCELABLE_KEY, null);
        outState.putParcelable(Videos.Result.PARCELABLE_KEY, null);
        outState.putParcelable(Reviews.Result.PARCELABLE_KEY, null);

        super.onSaveInstanceState(outState);
    }

    private void processReviews(TheMovieDBService service) {
        Call<Reviews> callReview = service.getReviews(movie.getId(), BuildConfig.THE_MOVIE_DB_API_TOKEN);

        callReview.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {

                List<Reviews.Result> reviews = response.body().getResults();

                if (reviews.size() > 0) {

                    List<SwipeItem> listaSwipe = new ArrayList<SwipeItem>();

                    int contador = 0;

                    SwipeItem[] swipeItems = new SwipeItem[reviews.size()];

                    for (Reviews.Result review : reviews) {

                        String reviewText = review.getContent();

                        String reviewText300 = null;
                        if (reviewText.length() > 300) {
                            reviewText300 = review.getContent().substring(0, Math.min(review.getContent().length(), 300)) + "...";
                        } else {
                            reviewText300 = reviewText;
                        }

                        swipeItems[contador] = new SwipeItem(contador, "Review " + (contador + 1), reviewText300);
                        contador++;
                    }

                    if (swipeItems.length > 0) {
                        mReviews.setItems(swipeItems);
                    }

                    Log.d(TAG, response.body().getResults().toString());
                } else {
                    mReviews.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Log.d(TAG, "Error while getting reviews");
            }
        });
    }

    private void processVideos(TheMovieDBService service) {
        Call<Videos> callVideos = service.getVideos(movie.getId(), BuildConfig.THE_MOVIE_DB_API_TOKEN);

        callVideos.enqueue(new Callback<Videos>() {
            @Override
            public void onResponse(Call<Videos> call, Response<Videos> response) {
                Videos videos = response.body();

                if(videos != null) {
                    twoVideosData = new ArrayList<>();

                    if (videos.getResults().size() > 1) {
                        twoVideosData.add(videos.getResults().get(0));
                        twoVideosData.add(videos.getResults().get(1));
                    } else {
                        twoVideosData.add(videos.getResults().get(0));
                    }

                    VideoAdapter videoAdapter = new VideoAdapter(twoVideosData, MovieDetailActivity.this);

                    mTrailers.setAdapter(videoAdapter);
                }
            }

            @Override
            public void onFailure(Call<Videos> call, Throwable t) {
                Log.d(TAG, "Error while getting the YouTube videos.");
            }
        });
    }
}