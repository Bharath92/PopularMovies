package com.example.upadhyb1.popularmovies.utility;

import android.util.Log;

import com.example.upadhyb1.popularmovies.Constants;

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
 * Created by UpadhyB1 on 4/18/2016.
 */
public class MovieReviewsParser {
    private final String LOG_TAG = MovieReviewsParser.class.getSimpleName();

    public ArrayList<HashMap<String, String>> getReviews(String moveieJsonStr)
            throws JSONException {
        ArrayList<HashMap<String, String>> reviewKVpair = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.


        JSONObject movieJson = new JSONObject(moveieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(Constants.MOVIE_LIST);
        for(int i = 0; i < movieArray.length(); i++) {
            HashMap<String,String> movieDetails = new HashMap<String,String>();
            movieDetails.put(Constants.REVIEW_AUTHOR, movieArray.getJSONObject(i).get(Constants.REVIEW_AUTHOR).toString());
            movieDetails.put(Constants.REVIEW_CONTENT, movieArray.getJSONObject(i).get(Constants.REVIEW_CONTENT).toString());
            reviewKVpair.add(movieDetails);
        }

        return reviewKVpair;

    }

}
