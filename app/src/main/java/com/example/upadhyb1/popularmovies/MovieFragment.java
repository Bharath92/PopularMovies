package com.example.upadhyb1.popularmovies;

import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
            updateMovies(sortby,pageNumber);
            return true;
        } else if(id == R.id.sort_popularity){
            sortby = "popularity";
            updateMovies("popularity",pageNumber);
            return true;
        } else if(id == R.id.sort_rating){
            sortby = "vote_average";
            updateMovies("vote_average",pageNumber);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                updateMovies(sortby, p);

            }
        });
        moviesGridView.setOnScrollListener(scrollListener);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
                detailsIntent.putExtra(Constants.MOVIE_ORIGINAL_TITLE, movieList.get(position).get(Constants.MOVIE_ORIGINAL_TITLE));
                detailsIntent.putExtra(Constants.MOVIE_POSTER, movieList.get(position).get(Constants.MOVIE_POSTER));
                detailsIntent.putExtra(Constants.MOVIE_RELEASE_DATE, movieList.get(position).get(Constants.MOVIE_RELEASE_DATE));
                detailsIntent.putExtra(Constants.MOVIE_VOTE_AVERAGE, movieList.get(position).get(Constants.MOVIE_VOTE_AVERAGE));
                detailsIntent.putExtra(Constants.MOVIE_VOTE_COUNT, movieList.get(position).get(Constants.MOVIE_VOTE_COUNT));
                detailsIntent.putExtra(Constants.MOVIE_OVERVIEW, movieList.get(position).get(Constants.MOVIE_OVERVIEW));

                startActivity(detailsIntent);
            }
        });
        return rootView;
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
        MovieJsonParser movieJson = new MovieJsonParser();
        ProgressDialog progressDialog;
        String pageNumber = new String();

        @Override
        protected  void onPreExecute(){
            progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Fetching the movies");
        }

        @Override
        protected void onPostExecute(String movieJsonStr) {
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
            String movieJsonStr = null;

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

                Uri builtUri = Uri.parse(MOVIE_BASE_URL + "discover/movie").buildUpon().appendQueryParameter(SORT_BY,params[2]+"."+params[1]).appendQueryParameter(VOTE_COUNT,params[0]).appendQueryParameter("page",params[3]).appendQueryParameter(API_KEY,getResources().getString(R.string.API_KEY)).build();

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
