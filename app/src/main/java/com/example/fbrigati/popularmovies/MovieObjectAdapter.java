package com.example.fbrigati.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by FBrigati on 10/10/2016.
 */

public class MovieObjectAdapter extends ArrayAdapter<MovieObject> {

    public final String LOG_TAG = MovieObjectAdapter.class.getSimpleName();

    public MovieObjectAdapter(Activity context, List<MovieObject> movieObjectList) {
        super(context, 0, movieObjectList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
                MovieObject movieObject = getItem(position);


                 // Adapters recycle views to AdapterViews.
                 // If this is a new View object we're getting, then inflate the layout.
                 // If not, this view already has the layout inflated from a previous call to getView,
                 // and we modify the View widgets as usual.
                 if (convertView == null) {
                     convertView = LayoutInflater.from(getContext()).inflate(
                             R.layout.fragment_movie_item, parent, false);

                 }

                 ImageView iconView = (ImageView) convertView.findViewById(R.id.poster_image);

                Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_IMAGE_BASE_URL).buildUpon()
                        .appendPath(BuildConfig.MOVIEDB_PIC_SIZE_SMALL).appendPath(movieObject.poster_path)
                .build();


                String ref = builtUri.toString().replace("%2F","");

                 Picasso.with(getContext()).load(ref).into(iconView);

                 TextView versionNameView = (TextView) convertView.findViewById(R.id.movie_title);
                 versionNameView.setText(movieObject.title );

                 return convertView;

    }
}
