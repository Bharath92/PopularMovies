package com.example.upadhyb1.popularmovies.utility;

import com.example.upadhyb1.popularmovies.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UpadhyB1 on 5/2/2016.
 */
public class MovieTrailersParser {
    private final String LOG_TAG = MovieTrailersParser.class.getSimpleName();

    public ArrayList<HashMap<String, String>> getTrailers(String moveieJsonStr)
            throws JSONException {
        ArrayList<HashMap<String, String>> reviewKVpair = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.


        JSONObject movieJson = new JSONObject(moveieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(Constants.YOUTUBE);
        for(int i = 0; i < movieArray.length(); i++) {
            HashMap<String,String> movieDetails = new HashMap<String,String>();
            movieDetails.put(Constants.NAME, movieArray.getJSONObject(i).get(Constants.NAME).toString());
            movieDetails.put(Constants.SOURCE, movieArray.getJSONObject(i).get(Constants.SOURCE).toString());
            reviewKVpair.add(movieDetails);
        }

        return reviewKVpair;

    }
}
