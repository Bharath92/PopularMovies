package com.example.upadhyb1.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by UpadhyB1 on 3/3/2016.
 */
public class MovieAdapter extends SimpleAdapter {

    private Context appContext;
    private LayoutInflater inflater;
    final private String imageUrl = "http://image.tmdb.org/t/p/";
    final private String imageSize = "w185";

    public MovieAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.appContext = context;
        this.inflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.poster, null);
        }

        HashMap<String, String> data = (HashMap<String, String>) getItem(position);

        ImageView posterImageView = (ImageView) convertView.findViewById(R.id.poster);

        String moviePoster = data.get("poster_path");
        Uri imageUri;
        if(data.get(Constants.FAVORITE).equals("1")){
            File file = new File(convertView.getContext().getFilesDir(), moviePoster.substring(1));
            Log.d("MovieAdapter",file.getPath());
            imageUri = Uri.fromFile(file);
            Log.d("MovieAdapter",imageUri.toString());
        } else {
            imageUri = Uri.parse(imageUrl).buildUpon()
                    .appendPath(imageSize)
                    .appendPath(moviePoster.substring(1))
                    .build();
        }


        posterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(appContext).load(imageUri).into(posterImageView);

        return convertView;
    }
}
