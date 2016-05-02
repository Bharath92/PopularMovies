package com.example.upadhyb1.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by UpadhyB1 on 4/10/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "in.upadhyb1.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // Constant strings to tell the difference between a list of items (CONTENT_TYPE)
        // and a singe item (CONTENT_ITEM_TYPE)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_DESCRIPTION = "movie_description";
        public static final String COLUMN_POPULARITY = "movie_popularity";
        public static final String COLUMN_RATING = "movie_rating";
        public static final String COLUMN_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_VOTE_COUNT = "movie_vote_count";

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        public static Uri buildMovieWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
