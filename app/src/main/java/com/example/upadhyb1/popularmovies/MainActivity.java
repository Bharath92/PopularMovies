package com.example.upadhyb1.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements  MovieFragment.Callback, MovieDetailsFragment.Callback{

    boolean mTwoPane;
    private final static String MOVIEDETAILFRAGMENT_TAG = "MDFTAG";
    private String mLocation;
    MovieFragment movieFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailsFragment(), MOVIEDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
            setSupportActionBar(toolbar);
            mTwoPane = false;
        }

        movieFragment = ((MovieFragment)getSupportFragmentManager().findFragmentById((R.id.container)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation))
            MovieFragment ff = (MovieFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        if ( null != ff ) {
            ff.onLocationChanged();
        }
        MovieDetailsFragment df = (MovieDetailsFragment)getSupportFragmentManager().findFragmentByTag(MOVIEDETAILFRAGMENT_TAG);
        if ( null != df ) {
            //df.onLocationChanged(location);
        }
        mLocation = location;*/
    }

    @Override
    public void onItemSelected(HashMap<String,String> movie){
        if(movie == null && mTwoPane){
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,fragment, MOVIEDETAILFRAGMENT_TAG).commit();
            return;
        } else if(movie == null && !mTwoPane){
            return;
        } else if(mTwoPane){
            Bundle args = new Bundle();
            args.putString(Constants.MOVIE_ID, movie.get(Constants.MOVIE_ID));
            args.putString(Constants.MOVIE_ORIGINAL_TITLE, movie.get(Constants.MOVIE_ORIGINAL_TITLE));
            args.putString(Constants.MOVIE_POSTER, movie.get(Constants.MOVIE_POSTER));
            args.putString(Constants.MOVIE_RELEASE_DATE, movie.get(Constants.MOVIE_RELEASE_DATE));
            args.putString(Constants.MOVIE_VOTE_AVERAGE, movie.get(Constants.MOVIE_VOTE_AVERAGE));
            args.putString(Constants.MOVIE_VOTE_COUNT, movie.get(Constants.MOVIE_VOTE_COUNT));
            args.putString(Constants.MOVIE_OVERVIEW, movie.get(Constants.MOVIE_OVERVIEW));
            args.putString(Constants.MOVIE_POPULARITY, movie.get(Constants.MOVIE_POPULARITY));
            args.putString(Constants.FAVORITE, movie.get(Constants.FAVORITE));
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,fragment, MOVIEDETAILFRAGMENT_TAG).commit();

        } else {
            Intent detailsIntent = new Intent(this, DetailsActivity.class);
            detailsIntent.putExtra(Constants.MOVIE_ID, movie.get(Constants.MOVIE_ID));
            detailsIntent.putExtra(Constants.MOVIE_ORIGINAL_TITLE, movie.get(Constants.MOVIE_ORIGINAL_TITLE));
            detailsIntent.putExtra(Constants.MOVIE_POSTER, movie.get(Constants.MOVIE_POSTER));
            detailsIntent.putExtra(Constants.MOVIE_RELEASE_DATE, movie.get(Constants.MOVIE_RELEASE_DATE));
            detailsIntent.putExtra(Constants.MOVIE_VOTE_AVERAGE, movie.get(Constants.MOVIE_VOTE_AVERAGE));
            detailsIntent.putExtra(Constants.MOVIE_VOTE_COUNT, movie.get(Constants.MOVIE_VOTE_COUNT));
            detailsIntent.putExtra(Constants.MOVIE_OVERVIEW, movie.get(Constants.MOVIE_OVERVIEW));
            detailsIntent.putExtra(Constants.MOVIE_POPULARITY, movie.get(Constants.MOVIE_POPULARITY));
            detailsIntent.putExtra(Constants.FAVORITE, movie.get(Constants.FAVORITE));
            startActivity(detailsIntent);
        }

    }

    @Override
    public void onUnfavorited(){
        Log.d("MainActivity","inside callback");
        if(mTwoPane){
            movieFragment.displayFavorites();
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,fragment, MOVIEDETAILFRAGMENT_TAG).commit();
            return;
        }
    }

}
