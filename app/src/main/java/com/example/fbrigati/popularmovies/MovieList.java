package com.example.fbrigati.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;


public class MovieList extends Fragment {

    public final static String ID_MESSAGE = "com.example.fbrigati.popularmovies.MESSAGE";

    MovieObjectAdapter movieAdapter;

    private ArrayList<MovieObject> movieList;


    public MovieObject[] movieObjects = {new MovieObject("",
            "Mock Original",
            "Mock Overview",
            "01/01/1979",
            "some.jpg",
            "some2.jpg",
            "2.58")};


    public final static String LOG_TAG = MovieList.class.getSimpleName();


    public MovieList() {
        // Required empty public constructor
    }

    @Override
         public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                 // Inflate the menu; this adds items to the action bar if it is present.
                 inflater.inflate(R.menu.fragment_main, menu);
             }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState == null || !savedInstanceState.containsKey(ID_MESSAGE)) {
          movieList = new ArrayList<MovieObject>(Arrays.asList(movieObjects));
                     }
                 else {
                         movieList = savedInstanceState.getParcelableArrayList(ID_MESSAGE);
                     }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
           outState.putParcelableArrayList(ID_MESSAGE, movieList);
           super.onSaveInstanceState(outState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_main, container, false);

        movieAdapter = new MovieObjectAdapter(getActivity(),
                movieList);

        //Get a reference to the listView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);

                MovieObject mvObj = movieAdapter.getItem(position);
                movieAdapter.notifyDataSetChanged();

                intent.putExtra(ID_MESSAGE, mvObj);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        requestMovies();
    }

    private void requestMovies() {

        //Get shared preferences..
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String QUERY_SORT = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        String lang = "en-US";
        final String LANG_PARAM = "language";
        final String APIKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon()
                .appendPath(QUERY_SORT)
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter(LANG_PARAM,lang)
                .build();

        //Instantiate Request..
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = builtUri.toString();
        Log.v(LOG_TAG, "Url being called: " + url);

        //String request..
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Check response...
                        if (!response.isEmpty()) {
                            try {
                                getMovieDataFromJson(response);
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "There was an error retrieving data from server, " +
                                        "please check your internet connection and try again",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "There was an error " +
                                    "please check your internet connection and try again"
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                },  new Response.ErrorListener(){
                        @Override
                                public void onErrorResponse(VolleyError error){
                            Toast.makeText(getActivity(), "There was an error " +
                                            "please check your internet connection and try again"
                                    , Toast.LENGTH_LONG).show();
                        }
                });
        //Add the request to RequestQueue
        queue.add(stringRequest);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


        private void getMovieDataFromJson(String movieDBJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String JO_LIST = "results";
            final String JO_TITLE = "title";
            final String JO_ORIGINAL_TITLE = "original_title";
            final String JO_OVERVIEW = "overview";
            final String JO_RELEASE_DATE = "release_date";
            final String JO_POSTER = "poster_path";
            final String JO_BACKDROP_PATH = "backdrop_path";
            final String JO_VOTE_AVG = "vote_average";

            JSONObject moviesJson = new JSONObject(movieDBJsonStr);
            JSONArray moviesList = moviesJson.getJSONArray(JO_LIST);

            if (moviesList.length()>0) movieAdapter.clear();

            for(int i = 0; i < moviesList.length(); i++) {


                // Get the JSON object representing a single movie
                JSONObject movieObject = moviesList.getJSONObject(i);

                //initialize new MovieObject..
                MovieObject mvObj = new MovieObject(
                        movieObject.getString(JO_TITLE),
                        movieObject.getString(JO_ORIGINAL_TITLE),
                        movieObject.getString(JO_OVERVIEW),
                        movieObject.getString(JO_RELEASE_DATE),
                        movieObject.getString(JO_POSTER),
                        movieObject.getString(JO_BACKDROP_PATH),
                        movieObject.getString(JO_VOTE_AVG)
                );

                movieAdapter.add(mvObj);
            }

            return;
        }

}
