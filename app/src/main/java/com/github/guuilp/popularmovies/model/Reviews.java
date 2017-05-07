
package com.github.guuilp.popularmovies.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reviews implements Parcelable{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    public final static Parcelable.Creator<Reviews> CREATOR = new Creator<Reviews>() {

        @SuppressWarnings({"unchecked"})
        public Reviews createFromParcel(Parcel in) {
            Reviews instance = new Reviews();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (Reviews.Result.class.getClassLoader()));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalResults = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Reviews[] newArray(int size) {
            return (new Reviews[size]);
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(page);
        dest.writeList(results);
        dest.writeValue(totalPages);
        dest.writeValue(totalResults);
    }

    public int describeContents() {
        return  0;
    }

    public static class Result implements Parcelable{

        public static final String PARCELABLE_KEY = "review";

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("author")
        @Expose
        private String author;

        @SerializedName("content")
        @Expose
        private String content;

        @SerializedName("url")
        @Expose
        private String url;

        public final static Parcelable.Creator<Result> CREATOR = new Creator<Result>() {

            @SuppressWarnings({"unchecked"})
            public Result createFromParcel(Parcel in) {
                Result instance = new Result();
                instance.id = ((String) in.readValue((String.class.getClassLoader())));
                instance.author = ((String) in.readValue((String.class.getClassLoader())));
                instance.content = ((String) in.readValue((String.class.getClassLoader())));
                instance.url = ((String) in.readValue((String.class.getClassLoader())));
                return instance;
            }

            public Result[] newArray(int size) {
                return (new Result[size]);
            }
        };

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(id);
            dest.writeValue(author);
            dest.writeValue(content);
            dest.writeValue(url);
        }

        public int describeContents() {
            return  0;
        }
    }
}
