package data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.upadhyb1.popularmovies.data.MovieContract;
import com.example.upadhyb1.popularmovies.data.MovieDBHelper;
import com.example.upadhyb1.popularmovies.data.MovieProvider;

import java.util.ArrayList;

import utils.TestUtils;

/**
 * Created by UpadhyB1 on 5/1/2016.
 */
public class TestProvider extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    private void deleteAllRecords() {
        SQLiteDatabase db = new MovieDBHelper(mContext).getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);

        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null, null, null, null, null, null);

        assertEquals("Some records not deleted in " + MovieContract.MovieEntry.TABLE_NAME, cursor.getCount(), 0);

        cursor.close();
        db.close();
    }

    public void testUriMatcher() {
        final String testPoster = "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg";

        UriMatcher matcher = MovieProvider.createUriMatcher();

        assertEquals("CONTENT_URI of MovieEntry should be a Dir",
                matcher.match(MovieContract.MovieEntry.CONTENT_URI),
                MovieProvider.MOVIE);

    }

    public void testInsertionAndQuerying() {
        SQLiteDatabase db = new MovieDBHelper(mContext).getWritableDatabase();
        UriMatcher matcher = MovieProvider.createUriMatcher();

        //insert into the table using the content provider
        ContentValues insertedValues = TestUtils.createStubMovie();
        Uri insertedUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, insertedValues);

        long insertedId = MovieContract.MovieEntry.getIdFromUri(insertedUri);
        Log.d("TestProvider", "Inserted "+insertedId);
        assertTrue("Values not inserted in the table", insertedId > 0);

        assertEquals("insertedUri should have an ID",
                MovieProvider.MOVIE_WITH_ID, matcher.match(insertedUri));

        //read the same data
        Cursor cursor = mContext.getContentResolver().query(
                insertedUri, null, null, null, null);

        assertTrue("No data returned by the query", cursor.moveToFirst());

        TestUtils.validateInsertedData("Data inserted and data read are not the same",
                cursor, insertedValues);

        cursor.close();

        ContentValues[] contentValues = TestUtils.createStubMovieList();
        for(int i = 0; i<contentValues.length; i++){
            mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues[i]);
        }
        cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            String[] strArr = cursor.getColumnNames();
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < strArr.length; i++) {
                strBuilder.append(strArr[i]);
            }
            String newString = strBuilder.toString();
            Log.d("TestProvider",newString);
        }
        Log.d("TestProvider", "Items in db :: "+cursor.getCount());
        assertEquals("failed to fetch all records",
                4, cursor.getCount());
        db.close();
    }

    public void testUpdate() {
        SQLiteDatabase db = new MovieDBHelper(mContext).getWritableDatabase();
        boolean votesNotNull;

        //insert a stub movie into SQLite
        ContentValues values = TestUtils.createStubMovie();
        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

        //change the value
        Integer votes = values.getAsInteger(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);
        if (votes != null) {
            votesNotNull = true;
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, votes + 10);
        } else {
            votesNotNull = false;
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, TestUtils.MOVIE_VOTE_COUNT); //put a dummy
        }

        int updatedRows = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                values,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " = ? ",
                new String[]{TestUtils.MOVIE_TITLE}
        );

        Log.d("TestProvider","updatedRows : "+updatedRows);

        assertEquals(updatedRows + " row(s) updated instead of only 1", 1, updatedRows);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // get all columns
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " = ? ",
                new String[]{TestUtils.MOVIE_TITLE},
                null);

        assertTrue("No data queried", cursor.moveToFirst());

        int votesColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

        if (votesNotNull) {
            assertEquals("Updated rows are not actually updated",
                    TestUtils.MOVIE_VOTE_COUNT + 10, cursor.getInt(votesColumnIndex));
        } else {
            assertEquals("Updated rows are not actually updated",
                    TestUtils.MOVIE_VOTE_COUNT, cursor.getInt(votesColumnIndex));
        }

        cursor.close();
        db.close();
    }

    public void testDelete() {
        SQLiteDatabase db = new MovieDBHelper(mContext).getWritableDatabase();

        //insert into the table using the content provider
        ContentValues insertedValues = TestUtils.createStubMovie();
        Uri insertedUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, insertedValues);




        //delete a single item
        int deleteOneRow = mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " = ?",
                new String[]{TestUtils.MOVIE_TITLE}
        );

        Log.d("TestProvider","deletedRow :: "+deleteOneRow);

        assertEquals("Didn't delete a single row", 1, deleteOneRow);


        db.close();
    }

}
