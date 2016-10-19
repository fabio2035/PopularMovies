package com.example.fbrigati.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity {

    public final static String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    public MovieObject movieObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_movie_detail, new DetailFragment())
                    .commit();
        }else
        {
            movieObj = savedInstanceState.getParcelable(MovieList.ID_MESSAGE);
            //Log.v(LOG_TAG, "It has savedInstance! with: " + movieObj.title);
        }
    }

    public static class DetailFragment extends Fragment {

        private int movieID;

        public DetailFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            //get intent...
            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra(MovieList.ID_MESSAGE)) {
                //Bundle movieObject = intent.getExtras();

                //Log.v(LOG_TAG, "Number of items in movie array: " + MovieList);
                MovieObject mvObj = intent.getExtras().getParcelable(MovieList.ID_MESSAGE);

                //Post original title
                TextView title = (TextView) rootView.findViewById(R.id.movie_original_title);
                title.setText(mvObj.getOriginal_title());

                //Post poster image
                ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image_detail);

                Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_IMAGE_BASE_URL).buildUpon()
                        .appendEncodedPath(BuildConfig.MOVIEDB_PIC_SIZE_BIG).appendEncodedPath(mvObj.getPoster_path())
                        .build();
                String ref = builtUri.toString().replace("%2F","");

                Picasso.with(getContext()).load(ref).into(poster);
                Log.v(LOG_TAG,"Url reference: " + ref);

                //Post synopsis
                TextView synopsis = (TextView) rootView.findViewById(R.id.movie_synopsis);
                synopsis.setText(mvObj.getOverview());

                //Post ratings
                TextView rating = (TextView) rootView.findViewById(R.id.movies_rating);
                rating.setText(mvObj.getVote_average());

                //Post release date
                TextView rel_date = (TextView) rootView.findViewById(R.id.movies_date_release);
                rel_date.setText(mvObj.getRelease_date());
            }
            return rootView;
        }
    }
}
