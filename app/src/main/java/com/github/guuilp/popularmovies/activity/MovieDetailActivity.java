package com.github.guuilp.popularmovies.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.guuilp.popularmovies.BuildConfig;
import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.adapter.VideoAdapter;
import com.github.guuilp.popularmovies.data.MoviesContract;
import com.github.guuilp.popularmovies.data.ReviewsContract;
import com.github.guuilp.popularmovies.data.VideosContract;
import com.github.guuilp.popularmovies.model.Movies;
import com.github.guuilp.popularmovies.model.Reviews;
import com.github.guuilp.popularmovies.model.Videos;
import com.github.guuilp.popularmovies.service.TheMovieDBService;
import com.github.guuilp.popularmovies.util.ImageSize;
import com.github.guuilp.popularmovies.util.NetworkUtils;
import com.github.guuilp.popularmovies.handler.PopularMoviesQueryHandler;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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
    private MenuItem mMenuItem;

    private List<Videos.Result> reducedVideosData = new ArrayList<>();
    List<Reviews.Result> reviews = null;

    //This object allows to work with content provider in a background task
    PopularMoviesQueryHandler popularMoviesQueryHandler;

    private static final int ID_MOVIE_LOADER = 62;
    private static final int ID_REVIEW_LOADER = 72;
    private static final int ID_VIDEOS_LOADER = 82;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        supportPostponeEnterTransition();

        popularMoviesQueryHandler = new PopularMoviesQueryHandler(getContentResolver());

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

        final String urlPoster = NetworkUtils.buildCoverUrl(ImageSize.DEFAULT.toString(), movie.getPosterPath());
        final String urlBackdrop = NetworkUtils.buildCoverUrl(ImageSize.A_LITTLE_BIGGER.toString(), movie.getBackdropPath());

        if(NetworkUtils.isOnline(this)){
            Picasso.with(this)
                    .load(urlPoster)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
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

            Picasso.with(this)
                    .load(urlBackdrop)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(mMovieBanner);
        } else {
            Picasso.with(this)
                    .load(urlPoster)
                    .networkPolicy(NetworkPolicy.OFFLINE)
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

            Picasso.with(this)
                    .load(urlBackdrop)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(mMovieBanner);
        }

        mOriginalTitle.setText(movie.getTitle());
        mPlotSynopsis.setText(movie.getOverview());
        mUserRating.setText(String.valueOf(movie.getVoteAverage()) + getString(R.string.total_rating_movie));
        mReleaseDate.setText(movie.getReleaseDate());

        mTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Videos.Result video = (Videos.Result) mTrailers.getItemAtPosition(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + video.getKey())));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_movie, menu);

        mMenuItem = menu.findItem(R.id.action_favorite);;

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_favorite:

                if (item.getIcon().getConstantState().equals(ContextCompat.getDrawable(MovieDetailActivity.this, R.drawable.ic_favorite_pink_24px).getConstantState())){

                    popularMoviesQueryHandler.startDelete(PopularMoviesQueryHandler.ID_MOVIE_TOKEN,
                            item,
                            ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, movie.getId()),
                            null,
                            null);

                    popularMoviesQueryHandler.startDelete(PopularMoviesQueryHandler.ID_REVIEW_TOKEN,
                            null,
                            ContentUris.withAppendedId(ReviewsContract.ReviewsEntry.CONTENT_URI, movie.getId()),
                            null,
                            null);

                    popularMoviesQueryHandler.startDelete(PopularMoviesQueryHandler.ID_VIDEO_TOKEN,
                            null,
                            ContentUris.withAppendedId(VideosContract.VideosEntry.CONTENT_URI, movie.getId()),
                            null,
                            null);
                } else {

                    popularMoviesQueryHandler.startInsert(PopularMoviesQueryHandler.ID_MOVIE_TOKEN,
                            item,
                            MoviesContract.MoviesEntry.CONTENT_URI,
                            movie.getContentValues(mMoviePoster, mMovieBanner));

                    for (ContentValues contentValue : videosToContentValue()) {
                        popularMoviesQueryHandler.startInsert(PopularMoviesQueryHandler.ID_REVIEW_TOKEN,
                                null,
                                VideosContract.VideosEntry.CONTENT_URI,
                                contentValue);
                    }

                    for(ContentValues contentValue : reviewsToContentValue()){
                        popularMoviesQueryHandler.startInsert(PopularMoviesQueryHandler.ID_VIDEO_TOKEN,
                                null,
                                ReviewsContract.ReviewsEntry.CONTENT_URI,
                                contentValue);
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

                reviews = response.body().getResults();

                if (reviews.size() > 0) {

                    SwipeItem[] swipeItems = new SwipeItem[reviews.size()];

                    for(int i = 0; i < reviews.size(); i++){
                        Reviews.Result review = reviews.get(i);

                        String reviewText = review.getContent();

                        String reviewText300 = null;

                        if (reviewText.length() > 300) {
                            reviewText300 = review.getContent().substring(0, Math.min(review.getContent().length(), 300)) + "...";
                        } else {
                            reviewText300 = reviewText;
                        }

                        swipeItems[i] = new SwipeItem(i, "Review " + (i + 1), reviewText300);
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

                    if (videos.getResults().size() > 1) {
                        reducedVideosData.add(videos.getResults().get(0));
                        reducedVideosData.add(videos.getResults().get(1));
                    } else {
                        reducedVideosData.add(videos.getResults().get(0));
                    }

                    VideoAdapter videoAdapter = new VideoAdapter(reducedVideosData, MovieDetailActivity.this);

                    mTrailers.setAdapter(videoAdapter);
                }
            }

            @Override
            public void onFailure(Call<Videos> call, Throwable t) {
                Log.d(TAG, "Error while getting the YouTube videos.");
            }
        });
    }

    private ContentValues[] videosToContentValue(){
        ContentValues[] cvVideos = new ContentValues[reducedVideosData.size()];


        for(int i = 0; i < reducedVideosData.size(); i++){

            ContentValues cvVideo = new ContentValues();

            cvVideo.put(VideosContract.VideosEntry.COLUMN_ID, reducedVideosData.get(i).getId());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_MOVIE_ID, movie.getId());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_ISO6391, reducedVideosData.get(i).getIso6391());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_ISO31661, reducedVideosData.get(i).getIso31661());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_KEY, reducedVideosData.get(i).getKey());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_NAME, reducedVideosData.get(i).getName());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_SITE, reducedVideosData.get(i).getSite());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_SIZE, reducedVideosData.get(i).getSize());
            cvVideo.put(VideosContract.VideosEntry.COLUMN_TYPE, reducedVideosData.get(i).getType());

            cvVideos[i] = cvVideo;
        }

        return cvVideos;
    }

    private ContentValues[] reviewsToContentValue(){
        ContentValues[] cvReviews = new ContentValues[reviews.size()];


        for(int i = 0; i < reviews.size(); i++){

            ContentValues cvReview = new ContentValues();
            cvReview.put(ReviewsContract.ReviewsEntry.COLUMN_ID, reviews.get(i).getId());
            cvReview.put(ReviewsContract.ReviewsEntry.COLUMN_MOVIE_ID, movie.getId());
            cvReview.put(ReviewsContract.ReviewsEntry.COLUMN_AUTHOR, reviews.get(i).getAuthor());
            cvReview.put(ReviewsContract.ReviewsEntry.COLUMN_CONTENT, reviews.get(i).getContent());
            cvReview.put(ReviewsContract.ReviewsEntry.COLUMN_URL, reviews.get(i).getUrl());

            cvReviews[i] = cvReview;
        }

        return cvReviews;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case ID_MOVIE_LOADER:
                return new CursorLoader(this,
                        ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, movie.getId()),
                        null,
                        null,
                        null,
                        null);

            case ID_VIDEOS_LOADER:
                return new CursorLoader(this,
                        ContentUris.withAppendedId(VideosContract.VideosEntry.CONTENT_URI, movie.getId()),
                        null,
                        null,
                        null,
                        null);

            case ID_REVIEW_LOADER:
                return new CursorLoader(this,
                        ContentUris.withAppendedId(ReviewsContract.ReviewsEntry.CONTENT_URI, movie.getId()),
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemented yet: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()){
            case ID_MOVIE_LOADER:
                if (data.getCount() > 0){
                    mMenuItem.setIcon(R.drawable.ic_favorite_pink_24px);
                    getSupportLoaderManager().initLoader(ID_REVIEW_LOADER, null, this);
                    getSupportLoaderManager().initLoader(ID_VIDEOS_LOADER, null, this);
                } else {
                    TheMovieDBService service = TheMovieDBService.retrofit.create(TheMovieDBService.class);

                    processVideos(service);

                    processReviews(service);
                }

                break;
            case ID_VIDEOS_LOADER:
                try {
                    mTrailers.setAdapter(null);
                    reducedVideosData.clear();

                    while (data.moveToNext()) {
                        reducedVideosData.add(Videos.Result.fromCursor(data));

                        VideoAdapter videoAdapter = new VideoAdapter(reducedVideosData, MovieDetailActivity.this);

                        mTrailers.setAdapter(videoAdapter);
                    }
                } finally {
                    data.close();
                }

                break;
            case ID_REVIEW_LOADER:
                try {
                    if (data.getCount() > 0) {
                        SwipeItem[] swipeItems = new SwipeItem[data.getCount()];

                        while (data.moveToNext()) {
                            swipeItems[data.getPosition()] = new SwipeItem(data.getPosition(), "Review " + (data.getPosition() + 1), data.getString(data.getColumnIndexOrThrow(ReviewsContract.ReviewsEntry.COLUMN_CONTENT)));
                        }

                        mReviews.setItems(swipeItems);
                    }
                } finally{
                    data.close();
                }

                break;
            default:
                throw new RuntimeException("Loader not implemented yet: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}