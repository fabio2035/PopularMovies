package com.example.fbrigati.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FBrigati on 10/18/2016.
 */

public class MovieObject implements Parcelable {
    String title;
    String original_title;
    String overview;
    String release_date;
    String poster_path;
    String backdrop_path;
    String vote_average;

    public MovieObject(String iTitle, String iOriginal_title,
                       String iOverview, String iRelease_date,
                       String iPoster_path,
                       String iBackdrop_path,
                       String iVote_average){
        this.title = iTitle;
        this.original_title = iOriginal_title;
        this.overview = iOverview;
        this.release_date = iRelease_date;
        this.poster_path = iPoster_path;
        this.backdrop_path = iBackdrop_path;
        this.vote_average = iVote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }


    protected MovieObject(Parcel in) {
        title = in.readString();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        vote_average = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeString(vote_average);
    }

    public static final Creator<MovieObject> CREATOR = new Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel in) {
            return new MovieObject(in);
        }

        @Override
        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };


}
