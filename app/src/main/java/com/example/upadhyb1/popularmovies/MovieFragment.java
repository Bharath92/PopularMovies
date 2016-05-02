package com.example.upadhyb1.popularmovies;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.upadhyb1.popularmovies.data.MovieContract;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MovieFragment extends Fragment {

    MovieAdapter movieAdapter;
    GridView moviesGridView;
    int pageNumber = 1;
    EndlessScrollListener scrollListener;
    ArrayList<HashMap<String, String>> movieList;
    String sortby = "popularity";
    String mJson;
    String type = new String();
    public MovieFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.menu_options_sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            scrollListener.setPageNumber(1);
            ((Callback) getActivity()).onItemSelected(null);
            updateMovies("", pageNumber);
            type = "";
            scrollListener.hasMorePages();
            return true;
        } else if(id == R.id.sort_popularity){
            scrollListener.setPageNumber(1);
            sortby = "popularity";
            ((Callback) getActivity()).onItemSelected(null);
            updateMovies("popularity", pageNumber);
            type = "popularity";
            scrollListener.hasMorePages();
            return true;
        } else if(id == R.id.sort_rating){
            scrollListener.setPageNumber(1);
            sortby = "vote_average";
            ((Callback) getActivity()).onItemSelected(null);
            updateMovies("vote_average", pageNumber);
            type = "vote_average";
            scrollListener.hasMorePages();
            return true;
        } else if(id == R.id.favorites){
            ((Callback) getActivity()).onItemSelected(null);
            displayFavorites();
            type = "favorites";
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("type", type);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        moviesGridView = (GridView) rootView.findViewById(R.id.movies_grid);
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(
                getActivity(),
                movieList, // list of data to show
                R.layout.poster, // the layout of a single item
                new String[]{ "movie_poster" },
                new int[]{ R.id.poster });

        moviesGridView.setAdapter(movieAdapter);

        scrollListener=new EndlessScrollListener(moviesGridView,new EndlessScrollListener.RefreshList() {

            @Override
            public void onRefresh(int p) {
                Log.d("MovieFragment", "On Refresh invoked.." + p);
                if(savedInstanceState != null){
                    if(savedInstanceState.getString("type").equals("favorites")){
                        displayFavorites();;
                    } else {
                        updateMovies(savedInstanceState.getString("type"), p);
                    }
                } else {
                    updateMovies("", p);
                }


            }
        });
        moviesGridView.setOnScrollListener(scrollListener);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                HashMap<String, String> movie = movieList.get(position);
                ((Callback) getActivity()).onItemSelected(movie);
               /* detailsIntent.putExtra(Constants.MOVIE_ID, movieList.get(position).get(Constants.MOVIE_ID));
                detailsIntent.putExtra(Constants.MOVIE_ORIGINAL_TITLE, movieList.get(position).get(Constants.MOVIE_ORIGINAL_TITLE));
                detailsIntent.putExtra(Constants.MOVIE_POSTER, movieList.get(position).get(Constants.MOVIE_POSTER));
                detailsIntent.putExtra(Constants.MOVIE_RELEASE_DATE, movieList.get(position).get(Constants.MOVIE_RELEASE_DATE));
                detailsIntent.putExtra(Constants.MOVIE_VOTE_AVERAGE, movieList.get(position).get(Constants.MOVIE_VOTE_AVERAGE));
                detailsIntent.putExtra(Constants.MOVIE_VOTE_COUNT, movieList.get(position).get(Constants.MOVIE_VOTE_COUNT));
                detailsIntent.putExtra(Constants.MOVIE_OVERVIEW, movieList.get(position).get(Constants.MOVIE_OVERVIEW));
                detailsIntent.putExtra(Constants.MOVIE_POPULARITY, movieList.get(position).get(Constants.MOVIE_POPULARITY));
                detailsIntent.putExtra(Constants.FAVORITE, movieList.get(position).get(Constants.FAVORITE));
                startActivity(detailsIntent);*/
            }
        });
        return rootView;
    }

    public interface Callback {
        public void onItemSelected(HashMap<String, String> movie);
    }

    public void displayFavorites(){
        movieList = new ArrayList<HashMap<String, String>>();
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            HashMap<String,String> movieDetails = new HashMap<String,String>();
            movieDetails.put(Constants.MOVIE_ID, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            movieDetails.put(Constants.MOVIE_POSTER, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            movieDetails.put(Constants.MOVIE_OVERVIEW, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION)));
            movieDetails.put(Constants.MOVIE_ORIGINAL_TITLE, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
            movieDetails.put(Constants.MOVIE_TITLE, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
            movieDetails.put(Constants.MOVIE_VOTE_COUNT, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)));
            movieDetails.put(Constants.MOVIE_VOTE_AVERAGE, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
            movieDetails.put(Constants.MOVIE_POPULARITY, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)));
            movieDetails.put(Constants.MOVIE_RELEASE_DATE, cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movieDetails.put(Constants.FAVORITE, 1+"");
            movieList.add(movieDetails);
        }
        movieAdapter = new MovieAdapter(
                getActivity(),
                movieList, // list of data to show
                R.layout.poster, // the layout of a single item
                new String[]{"poster_path"},
                new int[]{R.id.poster});

        moviesGridView.setAdapter(movieAdapter);

        scrollListener.noMorePages();
    }


    private void updateMovies(String sortby, int pNumber){
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String vote_count = prefs.getString(getString(R.string.pref_vote_count), getString(R.string.pref_vote_count_default));
        String sort_type = prefs.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_default));
        movieTask.execute(vote_count,sort_type,sortby,pNumber+"");
    }
    public class FetchMovieTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        MovieJsonParser movieJson = new MovieJsonParser(getActivity());
        ProgressDialog progressDialog;
        String pageNumber;
        Toast error;

        @Override
        protected  void onPreExecute(){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                if(!checkOnlineState()){
                    Toast.makeText(getActivity(), "Internet connectivity is down! You can browse favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Fetching the movies");
                }
            } else {
                error = Toast.makeText(getActivity(),
                        "Internet permission not granted",
                        Toast.LENGTH_LONG);
                error.show();
            }
        }

        public boolean checkOnlineState() {
            ConnectivityManager CManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo NInfo = CManager.getActiveNetworkInfo();
            if (NInfo != null && NInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(String movieJsonStr) {
            if(movieJsonStr == null){
                error = Toast.makeText(getActivity(),
                        "Can't connect to the server",
                        Toast.LENGTH_LONG);
                error.show();
                return;
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            try {
                if(pageNumber.equals("1")) {
                    Log.d(LOG_TAG,"page 1");
                    movieList = movieJson.getMovieList(movieJsonStr);
                    movieAdapter = new MovieAdapter(
                            getActivity(),
                            movieList, // list of data to show
                            R.layout.poster, // the layout of a single item
                            new String[]{"poster_path"},
                            new int[]{R.id.poster});

                    moviesGridView.setAdapter(movieAdapter);
                } else {
                    Log.d(LOG_TAG,"page "+pageNumber);
                    movieList.addAll(movieJson.getMovieList(movieJsonStr));
                    movieAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error ", e);
            }
        }

        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String format = "json";
            pageNumber = params[3];
            String movieJsonStr;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                // private final String urlHttp = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appId=863b84c16f7a5712c094bd7b1c5d9c3c";
                final String MOVIE_BASE_URL = getResources().getString(R.string.moviedb_base_url);
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";
                //Adding vote count because while sorting in ascending order, some results are irrelevant
                final String VOTE_COUNT = "vote_count.gte";
                Uri builtUri = Uri.parse(MOVIE_BASE_URL + "discover/movie").buildUpon().appendQueryParameter(VOTE_COUNT, params[0]).appendQueryParameter("page", params[3]).appendQueryParameter(API_KEY, getResources().getString(R.string.API_KEY)).build();;
                if(params[2].equals("")) {
                } else if(params[2].equals("popularity")){
                    builtUri = Uri.parse(MOVIE_BASE_URL + "movie/popular").buildUpon().appendQueryParameter(VOTE_COUNT, params[0]).appendQueryParameter("page", params[3]).appendQueryParameter(API_KEY, getResources().getString(R.string.API_KEY)).build();
                } else if(params[2].equals("vote_average")){
                    builtUri = Uri.parse(MOVIE_BASE_URL + "movie/top_rated").buildUpon().appendQueryParameter(VOTE_COUNT, params[0]).appendQueryParameter("page", params[3]).appendQueryParameter(API_KEY, getResources().getString(R.string.API_KEY)).build();
                }
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                if(movieJson.hasMorePages(movieJsonStr)) {
                    scrollListener.notifyMorePages();
                } else {
                    scrollListener.noMorePages();
                }
                return movieJsonStr;



            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;

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


        }


    }
}
