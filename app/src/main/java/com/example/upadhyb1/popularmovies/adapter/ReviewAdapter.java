package com.example.upadhyb1.popularmovies.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.upadhyb1.popularmovies.Constants;
import com.example.upadhyb1.popularmovies.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by UpadhyB1 on 4/18/2016.
 */
public class ReviewAdapter extends SimpleAdapter {

    private Context appContext;
    private LayoutInflater inflater;

    public ReviewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.appContext = context;
        this.inflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.movie_review, null);
        }

        HashMap<String, String> data = (HashMap<String, String>) getItem(position);
        TextView reviewHeading = (TextView) convertView.findViewById(R.id.reviewHeading);
        reviewHeading.setText(data.get(Constants.REVIEW_AUTHOR));
        TextView reviewDesc = (TextView) convertView.findViewById(R.id.reviewDesc);
        reviewDesc.setText(data.get(Constants.REVIEW_CONTENT));

        return convertView;
    }
}
