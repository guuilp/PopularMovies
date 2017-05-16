
package com.github.guuilp.popularmovies.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.github.guuilp.popularmovies.data.MoviesContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.chalup.microorm.annotations.Column;

public class Movies implements Parcelable {

    @SerializedName("page")
    @Expose
    private Integer page;

    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    protected Movies(Parcel in) {
        this.page = in.readInt();
        this.results = new ArrayList<Result>();
        in.readList(results, (ClassLoader) Result.CREATOR);
        this.totalResults = in.readInt();
        this.totalPages = in.readInt();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeList(results);
        dest.writeInt(totalResults);
        dest.writeInt(totalPages);
    }

    public static class Result implements Parcelable {

        private Result(String posterPath, Boolean adult, String overview, String releaseDate, Integer id,
                       String originalTitle, String originalLanguage, String title, String backdropPath,
                       Double popularity, Integer voteCount, Boolean video, Double voteAverage){
            this.posterPath = posterPath;
            this.adult = adult;
            this.overview = overview;
            this.releaseDate = releaseDate;
            this.id = id;
            this.originalTitle = originalTitle;
            this.originalLanguage = originalLanguage;
            this.title = title;
            this.backdropPath = backdropPath;
            this.popularity = popularity;
            this.voteCount = voteCount;
            this.video = video;
            this.voteAverage = voteAverage;
        }


        public static final String PARCELABLE_KEY = "movie";

        private String source;

        @SerializedName("poster_path")
        @Expose
        private String posterPath;

        @SerializedName("adult")
        @Expose
        private Boolean adult;

        @SerializedName("overview")
        @Expose
        private String overview;

        @SerializedName("release_date")
        @Expose
        private String releaseDate;

        @SerializedName("genre_ids")
        @Expose
        private List<Integer> genreIds = null;

        @SerializedName("id")
        @Expose
        private Integer id;

        @SerializedName("original_title")
        @Expose
        private String originalTitle;

        @SerializedName("original_language")
        @Expose
        private String originalLanguage;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;

        @SerializedName("popularity")
        @Expose
        private Double popularity;

        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;

        @SerializedName("video")
        @Expose
        private Boolean video;

        @SerializedName("vote_average")
        @Expose
        private Double voteAverage;

        public String getSource(){
            return source;
        }

        public void setSource(String source){
            this.source = source;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public Boolean getAdult() {
            return adult;
        }

        public void setAdult(Boolean adult) {
            this.adult = adult;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public List<Integer> getGenreIds() {
            return genreIds;
        }

        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getOriginalTitle() {
            return originalTitle;
        }

        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        public String getOriginalLanguage() {
            return originalLanguage;
        }

        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBackdropPath() {
            return backdropPath;
        }

        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        public Double getPopularity() {
            return popularity;
        }

        public void setPopularity(Double popularity) {
            this.popularity = popularity;
        }

        public Integer getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
        }

        public Boolean getVideo() {
            return video;
        }

        public void setVideo(Boolean video) {
            this.video = video;
        }

        public Double getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(Double voteAverage) {
            this.voteAverage = voteAverage;
        }

        protected Result(Parcel in) {
            posterPath = in.readString();
            adult = (in.readInt() == 0) ? false : true;
            overview = in.readString();
            releaseDate = in.readString();

            genreIds = new ArrayList<Integer>();
            in.readList(genreIds, null);

            id = in.readInt();
            originalTitle = in.readString();
            originalLanguage = in.readString();
            title = in.readString();
            backdropPath = in.readString();
            popularity = in.readDouble();
            voteCount = in.readInt();
            video = (in.readInt() == 0) ? false : true;
            voteAverage = in.readDouble();
        }

        public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel in) {
                return new Result(in);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(posterPath);
            dest.writeInt(adult ? 1 : 0);
            dest.writeString(overview);
            dest.writeString(releaseDate);
            dest.writeList(genreIds);
            dest.writeInt(id);
            dest.writeString(originalTitle);
            dest.writeString(originalLanguage);
            dest.writeString(title);
            dest.writeString(backdropPath);
            dest.writeDouble(popularity);
            dest.writeInt(voteCount);
            dest.writeInt(video ? 1 : 0);
            dest.writeDouble(voteAverage);
        }

        public ContentValues getContentValues(ImageView poster, ImageView banner){
            ContentValues cvMovies = new ContentValues();

            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_POSTER_IMAGE, bitmapToByteArray(poster));
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_IMAGE, bitmapToByteArray(banner));
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, posterPath);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_ADULT, adult ? 1 : 0);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_ID, id);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, backdropPath);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, popularity);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_VOTECOUNT, voteCount);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_VIDEO, video ? 1 : 0);
            cvMovies.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, voteAverage);

            return cvMovies;
        }

        public static Result fromCursor(Cursor c){
            String posterPath = c.getString(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH));
            Boolean adult = c.getInt(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_ADULT)) == 1;
            String overview = c.getString(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_OVERVIEW));
            String releaseDate = c.getString(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE));
            Integer id = c.getInt(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_ID));
            String originalTitle = c.getString(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE));
            String originalLanguage = c.getString(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE));
            String title = c.getString(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_TITLE));
            String backdropPath = c.getString(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH));
            Double popularity = c.getDouble(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_POPULARITY));
            Integer voteCount = c.getInt(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_VOTECOUNT));
            Boolean video = c.getInt(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_VIDEO)) == 1;
            Double voteAverage = c.getDouble(c.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE));

            return new Movies.Result(posterPath, adult, overview, releaseDate, id, originalTitle, originalLanguage, title, backdropPath, popularity, voteCount, video, voteAverage);
        }

        private byte[] bitmapToByteArray(ImageView imageView) {
            final int lnth = ((BitmapDrawable) imageView.getDrawable()).getBitmap().getByteCount();

            ByteBuffer dst= ByteBuffer.allocate(lnth);
            ((BitmapDrawable) imageView.getDrawable()).getBitmap().copyPixelsToBuffer( dst);
            return dst.array();
        }
    }
}
