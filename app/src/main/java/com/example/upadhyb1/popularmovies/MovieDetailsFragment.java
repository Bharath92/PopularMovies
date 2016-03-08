package com.example.upadhyb1.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetailsFragment extends Fragment {
    final private String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_movie_details, container, false);
        TextView detailsTitle = (TextView) rootView.findViewById(R.id.movie_title);
        ImageView detailsPoster = (ImageView) rootView.findViewById(R.id.details_movie_poster);
        TextView detailsRating = (TextView) rootView.findViewById(R.id.movie_rating);
        TextView detailsReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        TextView detailsOverview = (TextView) rootView.findViewById(R.id.movie_description);
        String imgUrl = "http://image.tmdb.org/t/p/";

        detailsPoster.setScaleType(ImageView.ScaleType.FIT_XY);
        Uri posterUri = Uri.parse(imgUrl).buildUpon()
                .appendPath("w185")
                .appendPath(getActivity().getIntent().getStringExtra(Constants.MOVIE_POSTER).substring(1))
                .build();

        Picasso.with(getActivity()).load(posterUri).into(detailsPoster);
        detailsOverview.setText(getActivity().getIntent().getStringExtra(Constants.MOVIE_OVERVIEW));
        detailsTitle.setText(getActivity().getIntent().getStringExtra(Constants.MOVIE_ORIGINAL_TITLE));
        detailsRating.setText(getActivity().getIntent().getStringExtra(Constants.MOVIE_VOTE_AVERAGE)+"/10 ("+getActivity().getIntent().getStringExtra(Constants.MOVIE_VOTE_COUNT)+" votes)");
        detailsReleaseDate.setText("Release date : "+getActivity().getIntent().getStringExtra(Constants.MOVIE_RELEASE_DATE));
        return rootView;
    }

}
