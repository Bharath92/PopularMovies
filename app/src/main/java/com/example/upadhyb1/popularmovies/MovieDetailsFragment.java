package com.example.upadhyb1.popularmovies;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.upadhyb1.popularmovies.adapter.ReviewAdapter;
import com.example.upadhyb1.popularmovies.adapter.TrailerAdapter;
import com.example.upadhyb1.popularmovies.data.MovieContract;
import com.example.upadhyb1.popularmovies.utility.MovieReviewsParser;
import com.example.upadhyb1.popularmovies.utility.MovieTrailersParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MovieDetailsFragment extends Fragment {
    final private String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private Uri mUri;
    static final String DETAIL_URI = "URI";
    ListView reviewsListView;
    ListView trailersListView;
    ArrayList<HashMap<String, String>> reviewList;
    ArrayList<HashMap<String, String>> trailerList;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    int favorite = 0;
    String trailer = new String();
    ShareActionProvider mShareActionProvider;
    String movie_id;
    String popularity;
    String rating;
    String vote_count;
    String description;
    String poster;
    String release_date;
    String title;

    private Intent createShareForecastIntent(){
        if(trailer.length() == 0){
            Toast.makeText(getActivity(),
                    "No trailer to share!",
                    Toast.LENGTH_LONG).show();
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        if(trailer.equals("")){
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the trailer of " + title + "!");
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the trailer of " + title + " : " + Constants.YOUTUBE_URL + trailer);
        }
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.movie_details, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

    }
    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            /*long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);*/
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = getArguments();
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.getExtras() != null){
            movie_id = intent.getStringExtra(Constants.MOVIE_ID);
            popularity = intent.getStringExtra(Constants.MOVIE_POPULARITY);
            rating = intent.getStringExtra(Constants.MOVIE_VOTE_AVERAGE);
            vote_count = intent.getStringExtra(Constants.MOVIE_VOTE_COUNT);
            description = intent.getStringExtra(Constants.MOVIE_OVERVIEW);
            poster = intent.getStringExtra(Constants.MOVIE_POSTER);
            release_date = intent.getStringExtra(Constants.MOVIE_RELEASE_DATE);
            title = intent.getStringExtra(Constants.MOVIE_ORIGINAL_TITLE);
            favorite = Integer.parseInt(intent.getStringExtra(Constants.FAVORITE));
        } else if(args != null){
            movie_id = args.getString(Constants.MOVIE_ID);
            popularity = args.getString(Constants.MOVIE_POPULARITY);
            rating = args.getString(Constants.MOVIE_VOTE_AVERAGE);
            vote_count = args.getString(Constants.MOVIE_VOTE_COUNT);
            description = args.getString(Constants.MOVIE_OVERVIEW);
            poster = args.getString(Constants.MOVIE_POSTER);
            release_date = args.getString(Constants.MOVIE_RELEASE_DATE);
            title = args.getString(Constants.MOVIE_ORIGINAL_TITLE);
            favorite = Integer.parseInt(args.getString(Constants.FAVORITE));
        } else {
            Log.d(LOG_TAG,"null view");
            return null;
        }
        View rootView =  inflater.inflate(R.layout.fragment_movie_details, container, false);
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if(favorite == 1){
            fab.setImageResource(android.R.drawable.btn_star_big_on);
        }
        reviewsListView = (ListView)  rootView.findViewById(R.id.review_list);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(
                getActivity(),
                reviewList, // list of data to show
                R.layout.movie_review, // the layout of a single item
                new String[]{Constants.REVIEW_AUTHOR ,Constants.REVIEW_CONTENT },
                new int[]{ R.id.reviewHeading, R.id.reviewDesc });

        reviewsListView.setAdapter(reviewAdapter);

        reviewsListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        trailersListView = (ListView)  rootView.findViewById(R.id.trailer_list);
        trailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(
                getActivity(),
                trailerList, // list of data to show
                R.layout.movie_trailer, // the layout of a single item
                new String[]{Constants.NAME },
                new int[]{ R.id.list_item_trailer_title});

        trailersListView.setAdapter(trailerAdapter);

        trailersListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("MovieDetailsFragment","checking whether a click");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP: {
                        float endX = event.getX();
                        float endY = event.getY();
                        if (isAClick(startX, endX, startY, endY)) {
                            return true;
                            //launchFullPhotoActivity(imageUrls);// WE HAVE A CLICK!!
                        }
                        break;
                    }
                }
                Log.d("MovieDetailsFragment","is not a click");
                v.getParent().requestDisallowInterceptTouchEvent(true); //specific to my project
                return false; //specific to my project
            }
        });
        getReviews(movie_id);
        getTrailers(movie_id);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorite == 0) {
                    ContentValues fav = new ContentValues();
                    String downloadImageUrl = "http://image.tmdb.org/t/p/w185"+poster;
                    DownloadMoviePosterTask downloadMoviePosterTask = new DownloadMoviePosterTask();
                    downloadMoviePosterTask.execute(poster.substring(1), downloadImageUrl);
                    fav.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_id);
                    fav.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                    fav.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                    fav.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
                    fav.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, description);
                    fav.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,vote_count);
                    fav.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster);
                    fav.put(MovieContract.MovieEntry.COLUMN_POPULARITY, Double.parseDouble(popularity));
                    Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, fav);
                    long insertedId = MovieContract.MovieEntry.getIdFromUri(insertedUri);
                    if(insertedId > 0) {
                        favorite = 1;
                        fab.setImageResource(android.R.drawable.btn_star_big_on);
                        Toast.makeText(getActivity(),
                                "Added to favorites!",
                                Toast.LENGTH_LONG).show();

                    }
                } else {
                    int deleted = getActivity().getContentResolver().delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{movie_id});
                    if(deleted > 0) {
                        File file = new File(getContext().getFilesDir(), poster.substring(1));
                        boolean delete = file.delete();
                        Log.d("MovieDetailsFragment", delete+"");
                        fab.setImageResource(android.R.drawable.btn_star_big_off);
                        favorite = 0;
                        Toast.makeText(getActivity(),
                                "Removed from favorites!",
                                Toast.LENGTH_LONG).show();
                        ((Callback) getActivity()).onUnfavorited();
                    }
                }
            }
        });

        TextView detailsTitle = (TextView) rootView.findViewById(R.id.movie_title);
        ImageView detailsPoster = (ImageView) rootView.findViewById(R.id.details_movie_poster);
        TextView detailsRating = (TextView) rootView.findViewById(R.id.movie_rating);
        TextView detailsReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        TextView detailsOverview = (TextView) rootView.findViewById(R.id.movie_description);
        String imgUrl = "http://image.tmdb.org/t/p/";
        detailsPoster.setScaleType(ImageView.ScaleType.FIT_XY);
        Uri imageUri;
        if(favorite == 1){
            File file = new File(rootView.getContext().getFilesDir(), poster.substring(1));
            Log.d("MovieDetailsFragment",file.getPath());
            imageUri = Uri.fromFile(file);
            Log.d("MovieDetailsFragment",imageUri.toString());
        } else {
            imageUri = Uri.parse(imgUrl).buildUpon()
                    .appendPath("w185")
                    .appendPath(poster.substring(1))
                    .build();
        }
        Picasso.with(getActivity()).load(imageUri).into(detailsPoster);
        detailsOverview.setText(description);
        detailsTitle.setText(title);
        detailsRating.setText(rating+"/10 ("+vote_count+" votes)");
        detailsReleaseDate.setText("Release date : "+release_date);
        return rootView;
    }

    public interface Callback{
        public void onUnfavorited();
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > 5/*C.CLICK_ACTION_THRESHHOLD*/ || differenceY > 5) {
            return false;
        }
        return true;
    }

    public void getTrailers(String id){
        FetchMovieDetailsTask fetchTask = new FetchMovieDetailsTask();
        fetchTask.execute("trailers",id);
    }

    public void getReviews(String id){
        FetchMovieDetailsTask fetchTask = new FetchMovieDetailsTask();
        fetchTask.execute("reviews", id);
    }

    public class DownloadMoviePosterTask extends AsyncTask<String, Void, Void>{
        protected Void doInBackground(String... params) {
            File file = new File(getContext().getFilesDir(), params[0]);
            Log.d("MovieDetailsFragment",file.getPath());
            try {
                URL url = new URL(params[1]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                if(file.createNewFile())
                {
                    file.createNewFile();
                }
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();
                int totalSize = urlConnection.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ( (bufferLength = inputStream.read(buffer)) > 0 )
                {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize) ;
                }
                fileOutput.close();
                if(downloadedSize==totalSize){
                    Log.d("MovieDetailsFragment","downloaded poster");
                }
            } catch (MalformedURLException e){
                e.printStackTrace();
                //filepath = null;
            } catch (IOException e){
                e.printStackTrace();
                //filepath = null;
            }
            return null;
        }
    }

    public class FetchMovieDetailsTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();
        ProgressDialog progressDialog;
        String pageNumber;
        Toast error;
        String type;
        MovieReviewsParser reviewsParser = new MovieReviewsParser();
        MovieTrailersParser trailersParser = new MovieTrailersParser();

        @Override
        protected  void onPreExecute(){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Fetching the reviews and trailers...");
            } else {
                error = Toast.makeText(getActivity(),
                        "Internet permission not granted",
                        Toast.LENGTH_LONG);
                error.show();
            }
        }

        @Override
        protected void onPostExecute(String movieJsonStr) {
            if(movieJsonStr == null){
                Toast.makeText(getActivity(),
                        "Unable to fetch "+type+"!",
                        Toast.LENGTH_LONG).show();
                if(type.equals("reviews")){
                    TextView mReview = (TextView) getActivity().findViewById(R.id.review);
                    mReview.setVisibility(View.GONE);
                    reviewsListView.setVisibility(View.GONE);
                } else {
                    TextView mTrailer = (TextView) getActivity().findViewById(R.id.trailer);
                    mTrailer.setVisibility(View.GONE);
                    trailersListView.setVisibility(View.GONE);
                }
            } else if(type.equals("reviews")){
                try {
                    /*reviewList.addAll(reviewsParser.getReviews(movieJsonStr));
                    reviewAdapter.notifyDataSetChanged();*/

                    reviewList = reviewsParser.getReviews(movieJsonStr);
                    if(reviewList.size() == 0){
                        TextView mReview = (TextView) getActivity().findViewById(R.id.review);
                        mReview.setVisibility(View.GONE);
                        reviewsListView.setVisibility(View.GONE);
                    }else {
                        reviewAdapter = new ReviewAdapter(
                                getActivity(),
                                reviewList, // list of data to show
                                R.layout.movie_review, // the layout of a single item
                                new String[]{Constants.REVIEW_AUTHOR, Constants.REVIEW_CONTENT},
                                new int[]{R.id.reviewHeading, R.id.reviewDesc});

                        reviewsListView.setAdapter(reviewAdapter);
                        ListUtils.setDynamicHeight(reviewsListView);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSON Error ", e);
                }
            } else {
                try {
                    trailerList = trailersParser.getTrailers(movieJsonStr);
                    if(trailerList.size() == 0){
                        TextView mTrailer = (TextView) getActivity().findViewById(R.id.trailer);
                        mTrailer.setVisibility(View.GONE);
                        trailersListView.setVisibility(View.GONE);

                    } else {
                        trailer = trailerList.get(0).get(Constants.SOURCE);
                        trailerAdapter = new TrailerAdapter(
                                getActivity(),
                                trailerList, // list of data to show
                                R.layout.movie_trailer, // the layout of a single item
                                new String[]{Constants.NAME},
                                new int[]{R.id.list_item_trailer_title});
                        if (mShareActionProvider != null) {
                            mShareActionProvider.setShareIntent(createShareForecastIntent());
                        } else {
                            Log.d(LOG_TAG, "Share action provider is null!!!");
                        }
                        trailersListView.setAdapter(trailerAdapter);
                        ListUtils.setDynamicHeight(trailersListView);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSON Error ", e);
                }
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        protected String doInBackground(String... params) {
            type = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String format = "json";
            String movieDetailsJsonStr;

            try {
                final String MOVIE_BASE_URL = getResources().getString(R.string.moviedb_base_url)+"movie/"+params[1]+"/"+params[0];
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendQueryParameter(API_KEY,getResources().getString(R.string.API_KEY)).build();

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
                movieDetailsJsonStr = buffer.toString();

                return movieDetailsJsonStr;



            } catch (IOException e) {
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


    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            Log.d("ListUtils",mListAdapter.getCount()+"");
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            Log.d("ListUtils","height :: "+height);
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            if(height > 300){
                height = 300;
            }
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }

    }
}




