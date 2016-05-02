package data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.upadhyb1.popularmovies.data.MovieContract;
import com.example.upadhyb1.popularmovies.data.MovieDBHelper;

import java.util.HashSet;
import java.util.Set;

import utils.TestUtils;

/**
 * Created by UpadhyB1 on 5/1/2016.
 */
public class TestDB extends AndroidTestCase{
    private MovieDBHelper helper;

    void dropDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    @Override
    public void setUp() throws Exception {
        dropDatabase();
        helper = new MovieDBHelper(mContext);
    }

    public void testTableCreation(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Set<String> tableNames = new HashSet<>();
        tableNames.add(MovieContract.MovieEntry.TABLE_NAME); //the table to check if created

        dropDatabase();

        assertTrue("Database not opened at all!", db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
        assertTrue("Database not created correctly", cursor.moveToFirst());

        do {
            tableNames.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        //all removed from tableNames
        assertTrue("Some tables not created!", tableNames.isEmpty());

        cursor.close();
        db.close();
    }

    public void testMovieTableColumns() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query the database for table information.", cursor.moveToFirst());

        Set<String> movieTableCols = new HashSet<>();
        movieTableCols.add(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        movieTableCols.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieTableCols.add(MovieContract.MovieEntry.COLUMN_RATING);
        movieTableCols.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);
        movieTableCols.add(MovieContract.MovieEntry.COLUMN_DESCRIPTION);
        movieTableCols.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieTableCols.add(MovieContract.MovieEntry.COLUMN_POPULARITY);

        final int COL_NAME_INDEX = cursor.getColumnIndex("name");
        do {
            String colName = cursor.getString(COL_NAME_INDEX);
            movieTableCols.remove(colName);
        } while (cursor.moveToNext());

        assertTrue("Some columns not created on " + MovieContract.MovieEntry.TABLE_NAME + " table", movieTableCols.isEmpty());
        cursor.close();
    }

    public void testMovieInsertion() {
        SQLiteDatabase db = helper.getWritableDatabase();

        //insert into the table
        ContentValues insertedValues = TestUtils.createStubMovie();
        long insertedId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, insertedValues);
        assertTrue("Values not inserted in the table", insertedId != -1);

        //read the same data
        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null, // columns to query
                null, // columns to test (where)
                null, // values to test
                null, // group by
                null, // columns to filter by row group
                null // sort by rule
        );

        assertTrue("No data returned by the query", cursor.moveToFirst());

        TestUtils.validateInsertedData("Data inserted and data read are not the same",
                cursor, insertedValues);

        cursor.close();
        db.close();
    }

}
