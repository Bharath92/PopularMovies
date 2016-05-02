package com.example.upadhyb1.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class TrailerAdapter extends SimpleAdapter{
    private Context appContext;
    private LayoutInflater inflater;

    public TrailerAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.appContext = context;
        this.inflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.movie_trailer, null);
        }
        final Context context = parent.getContext();
        final HashMap<String, String> data = (HashMap<String, String>) getItem(position);

        Log.d("TrailerAdapter", "movie:" + data.get(Constants.NAME));

        TextView name = (TextView) convertView.findViewById(R.id.list_item_trailer_title);
        name.setText(data.get(Constants.NAME));

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_arrow_play);

        convertView.setOnTouchListener(new View.OnTouchListener() {

            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event){
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
                v.getParent().requestDisallowInterceptTouchEvent(true); //specific to my project
                return false;
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri videoUri = Uri.parse(Constants.YOUTUBE_URL + data.get(Constants.SOURCE));

                Intent playTrailer = new Intent(Intent.ACTION_VIEW, videoUri);
                context.startActivity(playTrailer);
            }
        });

        return convertView;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > 5/*C.CLICK_ACTION_THRESHHOLD*/ || differenceY > 5) {
            return false;
        }
        return true;
    }
}
