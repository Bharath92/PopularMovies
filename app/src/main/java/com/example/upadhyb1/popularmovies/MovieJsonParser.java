package com.example.upadhyb1.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;

import com.example.upadhyb1.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by UpadhyB1 on 3/3/2016.
 */
public class MovieJsonParser {
    private final String LOG_TAG = MovieJsonParser.class.getSimpleName();
    FragmentActivity activity;
    public MovieJsonParser(FragmentActivity activity){
        this.activity = activity;
    }

    private String formatDateString(String dateStr){
        try {
            DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat("dd/MM/yyyy");
            toFormat.setLenient(false);
            Date date = fromFormat.parse(dateStr);
            dateStr = toFormat.format(date);
        } catch (ParseException e){
            Log.e(LOG_TAG,e.toString());
        }
        return dateStr;
    }

    protected ArrayList<HashMap<String, String>> getMovieList(String moveieJsonStr)
            throws JSONException {
        ArrayList<HashMap<String, String>> m_kvpair = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.


        JSONObject movieJson = new JSONObject(moveieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(Constants.MOVIE_LIST);
        for(int i = 0; i < movieArray.length(); i++) {
            HashMap<String,String> movieDetails = new HashMap<String,String>();
            movieDetails.put(Constants.MOVIE_ID, movieArray.getJSONObject(i).get(Constants.MOVIE_ID).toString());
            movieDetails.put(Constants.MOVIE_POSTER, movieArray.getJSONObject(i).get(Constants.MOVIE_POSTER).toString());
            movieDetails.put(Constants.MOVIE_OVERVIEW, movieArray.getJSONObject(i).get(Constants.MOVIE_OVERVIEW).toString());
            movieDetails.put(Constants.MOVIE_ORIGINAL_TITLE, movieArray.getJSONObject(i).get(Constants.MOVIE_ORIGINAL_TITLE).toString());
            movieDetails.put(Constants.MOVIE_ORIGINAL_LANGUAGE, movieArray.getJSONObject(i).get(Constants.MOVIE_ORIGINAL_LANGUAGE).toString());
            movieDetails.put(Constants.MOVIE_TITLE, movieArray.getJSONObject(i).get(Constants.MOVIE_TITLE).toString());
            movieDetails.put(Constants.MOVIE_VOTE_COUNT, movieArray.getJSONObject(i).get(Constants.MOVIE_VOTE_COUNT).toString());
            movieDetails.put(Constants.MOVIE_VOTE_AVERAGE, movieArray.getJSONObject(i).get(Constants.MOVIE_VOTE_AVERAGE).toString());
            movieDetails.put(Constants.MOVIE_POPULARITY, movieArray.getJSONObject(i).get(Constants.MOVIE_POPULARITY).toString());
            movieDetails.put(Constants.MOVIE_BACKDROP_PATH, movieArray.getJSONObject(i).get(Constants.MOVIE_BACKDROP_PATH).toString());
            movieDetails.put(Constants.MOVIE_RELEASE_DATE, formatDateString(movieArray.getJSONObject(i).get(Constants.MOVIE_RELEASE_DATE).toString()));
            movieDetails.put(Constants.FAVORITE, ""+updateMovieDetails(movieArray.getJSONObject(i)));
            m_kvpair.add(movieDetails);
        }

        return m_kvpair;

    }

    protected boolean hasMorePages(String moveieJsonStr) throws JSONException {
        JSONObject movieJson = new JSONObject(moveieJsonStr);
        if(movieJson.get("page").toString().equals(movieJson.get("total_pages").toString())){
            return false;
        }
        return true;
    }

    private int updateMovieDetails(JSONObject json) throws JSONException{
        String movieId = json.get(Constants.MOVIE_ID).toString();
        ContentValues fav = new ContentValues();
        fav.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, json.get(Constants.MOVIE_TITLE).toString());
        fav.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, formatDateString(json.get(Constants.MOVIE_RELEASE_DATE).toString()));
        fav.put(MovieContract.MovieEntry.COLUMN_RATING, json.get(Constants.MOVIE_VOTE_AVERAGE).toString());
        fav.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, json.get(Constants.MOVIE_OVERVIEW).toString());
        fav.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, json.get(Constants.MOVIE_VOTE_COUNT).toString());
        fav.put(MovieContract.MovieEntry.COLUMN_POPULARITY, json.get(Constants.MOVIE_POPULARITY).toString());
        int updated = activity.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                fav,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movieId}
        );
        Log.d(LOG_TAG,"updated rows : "+updated);
        return updated;
    }
}

