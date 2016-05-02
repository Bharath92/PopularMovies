package utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.upadhyb1.popularmovies.data.MovieContract;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by UpadhyB1 on 5/1/2016.
 */
public class TestUtils extends AndroidTestCase{
    public static final String MOVIE_TITLE = "Interstellar";
    public static final int MOVIE_VOTE_COUNT = 1234;
    private static final double MOVIE_POPULARITY = 12.3;

    public static ContentValues createStubMovie() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 1);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, MOVIE_TITLE);
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                "2014-10-26");
        cv.put(MovieContract.MovieEntry.COLUMN_RATING, 8.8);
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, MOVIE_VOTE_COUNT);
        cv.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, "lorem ipsum dolor");
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/img_of_interstellar.jpg");
        cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, MOVIE_POPULARITY);

        return cv;
    }

    public static void validateInsertedData(String errorMessage, Cursor cursor, ContentValues insertedValues) {
        Set<Map.Entry<String, Object>> valueSet = insertedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String colName = entry.getKey();
            int idx = cursor.getColumnIndex(colName);
            assertFalse("Column '" + colName + "' not found. " + errorMessage, idx == -1);

            String expectedValue = entry.getValue().toString();
            String value = cursor.getString(idx);

            assertEquals(errorMessage + ": Value " + colName + " read doesn't match the expected value. ", expectedValue, value);
        }

    }

    public static ContentValues[] createStubMovieList() {
        ArrayList<ContentValues> contentValues = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, i);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, MOVIE_TITLE + i);
            cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                    "2014-10-26");
            cv.put(MovieContract.MovieEntry.COLUMN_RATING, 6.8 + i);
            cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, MOVIE_VOTE_COUNT + i * 10);
            cv.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, "lorem ipsum dolor");
            cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/img_of_interstellar.jpg");
            cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, MOVIE_POPULARITY + i);

            contentValues.add(cv);
        }

        if (!contentValues.isEmpty()) {
            ContentValues[] returnValues = new ContentValues[contentValues.size()];
            contentValues.toArray(returnValues);

            return returnValues;
        } else {
            return null;
        }

    }
}
