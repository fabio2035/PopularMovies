package com.example.fbrigati.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class MovieList extends Fragment {

    public final static String ID_MESSAGE = "com.example.fbrigati.popularmovies.MESSAGE";

    MovieObjectAdapter movieAdapter;

    private ArrayList<MovieObject> movieList;


    public MovieObject[] movieObjects = {new MovieObject("...",
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

        //movieAdapter = new MovieObjectAdapter(getActivity(),
        //        new ArrayList<MovieObject>());

        movieAdapter = new MovieObjectAdapter(getActivity(),
                movieList);

        //movieObjectAdapter.notifyDataSetChanged();
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
        getMovies();
    }

    private void getMovies() {
        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        fetchMovies.execute();
    }


    @Override
    public void onDetach() {
        super.onDetach();
      //  mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, String> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        ArrayList<MovieObject> movieObjects = new ArrayList<MovieObject>();


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


            //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //String unitType = pref.getString(getString(R.string.pref_temperature_key), getString(R.string.pref_temper_default));

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

                movieObjects.add(mvObj);
            }

            return;
        }

        @Override
        protected String doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieDBJsonStr = null;

            String lang = "en-US";

            try {

                //String[] data = params[0];

                //final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final String QUERY_SORT = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
                //final String QUERY_TOPR = "top_rated";
                final String LANG_PARAM = "language";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(QUERY_SORT)
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .appendQueryParameter(LANG_PARAM,lang)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to MovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieDBJsonStr= null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieDBJsonStr = null;
                }
                movieDBJsonStr = buffer.toString();
                Log.v(LOG_TAG, "MovieDB string: " + movieDBJsonStr);

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieDBJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try{
                getMovieDataFromJson(movieDBJsonStr);
                return "Success!";
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){

            if (result != null){
                movieAdapter.clear();

            for(MovieObject mvObj:movieObjects){
                movieAdapter.add(mvObj);
            }
        }
        }
    }
}
