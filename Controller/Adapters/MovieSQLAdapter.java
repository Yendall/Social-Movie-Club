package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.LocalDatabaseSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.ModelSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

/*
Student Name:       Max Yendall
Student Number:     s3436993
Course:             Mobile Application Development
Version:            2.0
Project:            Movie Social Club
 */
public class MovieSQLAdapter {

    Context mContext;
    LocalDatabaseSingleton mLocalDatabaseSingleton;
    ModelSingleton mModelSingleton;
    String mId;

    /**
     * Constructor for MovieSQLAdapter. Fetches singleton instances for use
     * @param c    Context of the current activity
     */
    public MovieSQLAdapter(Context c)
    {
        this.mContext = c;
        mLocalDatabaseSingleton = LocalDatabaseSingleton.getInstance(c);
        mModelSingleton = ModelSingleton.getSingleton();
    }
    /**
     * This method removes a movie from the writable database
     * @param mId    ID of the Movie object for dynamic referencing
     */
    public void removeMovieFromDatabase(String mId)
    {
        // Retrieve an instance of the current database
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Delete the movie at the index denoted by the movie id
        database.delete(LocalDatabaseSingleton.TABLE_MOVIES,
                LocalDatabaseSingleton.MOVIES_MOVIE_ID + " = ?", new String[]{mId});
    }
    /**
     * This method returns all movies that match a partial query for displaying
     * @param query    String query for searching
     */
    public ArrayList<Movie> partialQuery(String query,Set<Movie> movies_list)
    {
        // Retrieve an instance of the current database
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Declare an array list for storing the movies in
        ArrayList<Movie> movies_fetched = new ArrayList<>();
        // Declare the cursor to search the database with
        Cursor new_cursor = database.rawQuery
                (" SELECT * FROM "
                        +  LocalDatabaseSingleton.TABLE_MOVIES + " WHERE "
                        + LocalDatabaseSingleton.MOVIES_TITLE + " LIKE "
                        + "'%" + query + "%';", null);
        if(new_cursor != null && new_cursor.getCount() > 0)
        {
            while (new_cursor.moveToNext())
            {
                    // Create a new movie and push it to the singleton
                    Movie new_movie = new Movie(
                            new_cursor.getString(new_cursor.getColumnIndex
                                    (LocalDatabaseSingleton.MOVIES_MOVIE_ID)),
                            new_cursor.getString(new_cursor.getColumnIndex
                                    (LocalDatabaseSingleton.MOVIES_TITLE)),
                            new_cursor.getString(new_cursor.getColumnIndex
                                    (LocalDatabaseSingleton.MOVIES_YEAR)),
                            new_cursor.getString(new_cursor.getColumnIndex
                                    (LocalDatabaseSingleton.MOVIES_POSTER)),
                            new_cursor.getString(new_cursor.getColumnIndex
                                    (LocalDatabaseSingleton.MOVIES_SHORTPLOT)),
                            new_cursor.getString(new_cursor.getColumnIndex
                                    (LocalDatabaseSingleton.MOVIES_FULLPLOT)),
                            new_cursor.getFloat(new_cursor.getColumnIndex
                                    (LocalDatabaseSingleton.MOVIES_RATING)));
                    new_movie.setmColorHex(mContext.getString(R.string.blue));
                    new_movie.setmHasParty(false);
                    movies_list.add(new_movie);
            }
            new_cursor.close();
        }

        return movies_fetched;
    }
    /**
     * This method adds a movie to the writable singleton database
     * @param new_movie    Movie object passed in for adding
     */
    public void addMovieToDatabase(Movie new_movie)
    {
        // Retrieve an instance of the current database
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Insert the values from the movie object into the database
        // Begin inserting values into the database
        ContentValues content = new ContentValues();
        // Insert values from the movie object
        if(!movieExists(new_movie.getmId()))
        {
            content.put(LocalDatabaseSingleton.MOVIES_MOVIE_ID,new_movie.getmId());
            content.put(LocalDatabaseSingleton.MOVIES_TITLE,new_movie.getmTitle());
            content.put(LocalDatabaseSingleton.MOVIES_FULLPLOT,new_movie.getmLongPlot());
            content.put(LocalDatabaseSingleton.MOVIES_YEAR,new_movie.getmYear());
            content.put(LocalDatabaseSingleton.MOVIES_SHORTPLOT,new_movie.getmShortPlot());
            content.put(LocalDatabaseSingleton.MOVIES_RATING,new_movie.getmRating());
            content.put(LocalDatabaseSingleton.MOVIES_POSTER,new_movie.getmPoster());
            content.put(LocalDatabaseSingleton.MOVIES_MOVIE_LAST_UPDATED, System.currentTimeMillis());
            // Insert new content into the database
            database.insert(LocalDatabaseSingleton.TABLE_MOVIES, null, content);

        }
    }
    /**
     * This method returns the time in milliseconds that the database was last updated
     * @param mId    Movie object passed in for adding
     * @return lastUpdatedQuery  Returns the time in milliseconds from the database
     */
    public long getMostRecentUpdate(String mId)
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Formulate the argument for the cursor
        String[] argument = { mId};
        // Formulate the columns we wish to fetch
        String[] columns = { LocalDatabaseSingleton.MOVIES_MOVIE_LAST_UPDATED };
        // Formulate the result we wish to use
        String fetchedResult = LocalDatabaseSingleton.MOVIES_MOVIE_ID + " =?";
        // Declare a new cursor and query the SQL with the parameters created
        Cursor new_cursor = database.query(LocalDatabaseSingleton.TABLE_MOVIES, columns,
                fetchedResult, argument, null, null, null);
        // If the cursor is not null and returns at least one result
        if(new_cursor != null && new_cursor.getCount()>0)
        {
            // Move to the first row
            new_cursor.moveToFirst();
            // Create the query return
            Long lastUpdatedQuery = Long.parseLong
                    (new_cursor.getString(new_cursor.getColumnIndex
                            (LocalDatabaseSingleton.MOVIES_MOVIE_LAST_UPDATED)));
            // Close the active cursor
            new_cursor.close();
            // Return the result
            return lastUpdatedQuery;
        }
        else {
            // No results
            return 0;
        }
    }
    /**
     * This method populates the memory model with movies from the local database
     * This is mainly to test that the database works, the memory model will only
     * have movies that have parties, not all movies in the database.
     */
    public void populateMovies()
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Declare new cursor for fetching all current movies from the database
        Cursor new_cursor = database.rawQuery
                ("SELECT * FROM " + LocalDatabaseSingleton.TABLE_MOVIES + ";", null);
        // If the new cursor is not empty and it returns at least 1 movie
        if(new_cursor != null && new_cursor.getCount() > 0)
        {
            // Iterate through the rows
            while(new_cursor.moveToNext())
            {
                // Create a new movie and push it to the singleton
                Movie new_movie = new Movie(
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.MOVIES_MOVIE_ID)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.MOVIES_TITLE)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.MOVIES_YEAR)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.MOVIES_POSTER)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.MOVIES_SHORTPLOT)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.MOVIES_FULLPLOT)),
                        new_cursor.getFloat(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.MOVIES_RATING)));
                mModelSingleton.pushMovieToMemoryModel(new_movie);
            }
            // Close the cursor
            new_cursor.close();
        }
    }
    /**
     * This method updates a movies rating. This is the only field that really requires
     * updating in the movie model. It is changed using the rating bar listener.
     * @param mId      Movie ID passed in for reference
     * @param mRating  New rating passed in for updating
     * @return int     Returns an int determining whether the query was a success or failure
     */
    public int updateMovieRating(String mId, float mRating)
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Declare success value for update query
        int return_value;
        // Create new content values for the rating
        ContentValues rating_value = new ContentValues();
        // Push the rating value to the content values struct
        rating_value.put(LocalDatabaseSingleton.MOVIES_RATING,mRating);
        // Run the database query
        return_value = database.update(LocalDatabaseSingleton.TABLE_MOVIES,rating_value,
                LocalDatabaseSingleton.MOVIES_MOVIE_ID + " = '"
                        + mId + "';",null);
        // If the database update didn't fail
        if(return_value != 1)
        {
            return -1;
        }
        // Success
        return 1;
    }
    /**
     * This method checks if a movie exists in the local database. This is so we don't add
     * multiple copies of the same movie.
     * @param mId      Movie ID passed in for reference
     * @return bool    Returns a boolean showing whether the movie exists or not
     */
    public boolean movieExists(String mId)
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Formulate the argument for the cursor
        String[] argument = { mId };
        // Formulate the columns we wish to fetch
        String[] columns = { LocalDatabaseSingleton.MOVIES_MOVIE_ID };
        // Formulate the result we wish to use
        String fetchedResult = LocalDatabaseSingleton.MOVIES_MOVIE_ID + " =?";
        // Declare a new cursor and query the SQL with the parameters created
        Cursor new_cursor = database.query(LocalDatabaseSingleton.TABLE_MOVIES,columns,
                fetchedResult,argument,null,null,null);
        if(new_cursor.moveToFirst())
        {
            // Close the cursor
            new_cursor.close();
            // Return
            return true;
        }
        else
        {
            return false;
        }
    }

}
