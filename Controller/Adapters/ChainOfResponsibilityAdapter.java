package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.SearchActivity;
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
public class ChainOfResponsibilityAdapter {

    String searchQuery;
    ModelSingleton mSingleton = ModelSingleton.getSingleton();
    MovieSQLAdapter movieSQLAdapter;
    Context mContext;
    Activity actRef;
    Set<Movie> movies_list;
    JSONAdapter j;
    boolean isConnected;
    public ChainOfResponsibilityAdapter(String query, Context context, Activity ref,
                                        boolean isConnected)
    {
        searchQuery = query;
        mContext = context;
        actRef = ref;
        movieSQLAdapter = new MovieSQLAdapter(context);
        movies_list = new HashSet<>();
        this.isConnected = isConnected;
    }
    /**
     * This method initialises the chain of responsibility
     * @return Boolean      Returns the status of the chain
     */
    public boolean ChainOfResponsibility()
    {
        if(!searchQuery.isEmpty()) {
            // First chain is searching the memory model
            memoryChain();
            // Second chain is searching the database model
            localDatabaseChain();
            // Search OMDB for the keywords as they were not found in the memory or
            // database model. Because the database stores all searches, we know that if
            // it reaches here, we need to search OMDB.

            // If the internet is connected
            if(isConnected) {
                searchOMDB();
            }
        }
        else
        {
            // Display nothing in the list view because the search query is empty
            ListView jListView = (ListView) actRef.findViewById(R.id.jList);
            jListView.setAdapter(null);
        }
        return false;
    }
    /**
     * This method initialises the memory chain
     * @return Boolean      Returns the status of the chain
     */
    public boolean memoryChain()
    {
        // Loop through all the movies in the singleton
        for(Movie movie : mSingleton.getmListComplete())
        {
            // If any of the movies contain the search query
            if(movie.getmTitle().toLowerCase().contains(searchQuery.toLowerCase()))
            {
                // Add the movie to the array list for displaying
                movies_list.add(movie);
            }
        }

        return true;
    }
    /**
     * This method initialises the local database chain
     * @return Boolean      Returns the status of the chain
     */
    public boolean localDatabaseChain()
    {
        ArrayList<Movie> insert = new ArrayList<>();
        // Declare an array list of movies returned
        movieSQLAdapter.partialQuery(searchQuery,movies_list);

        // Check if the movie list already has movies in it
        if(movies_list.size() > 0)
        {
            insert.addAll(movies_list);
            // Find the list view and set the object adapter for displaying
            ListView jListView = (ListView) actRef.findViewById(R.id.jList);
            SearchViewObjectAdapter mObjectAdapter =
                    new SearchViewObjectAdapter(mContext,insert);
            jListView.setAdapter(mObjectAdapter);
            return true;
        }
        else
        {
            return false;
        }
    }
    /**
     * This method initialises the OMDB chain
     * @return Boolean      Returns the status of the chain
     */
    public boolean searchOMDB()
    {
        // If the chain reaches here, we search OMDB with JSON requests to the OMDB REST service
        if(j != null)
        {
            j.cancel(true);
        }
        // Initialise the string to empty for exception handling
        String newQuery = "";
        // Create the URL for querying
        String url = "http://www.omdbapi.com";
        // Trim the search query to fit into the URL properly
        if (searchQuery.length() > 0 && searchQuery.charAt(searchQuery.length() - 1)==' ')
        {
            newQuery = searchQuery.substring(0, searchQuery.length()-1);
        }
        // Create the URL
        url += ("/?s=" + newQuery + "&y=&r=json&type=movie");
        // Replace all blank spaces with addition signs
        String uri = url.replaceAll("\\s", "+");

        // Execute JSON on a worker thread as an Asynctask in tje JSONAdapter class
        try {
            if (!uri.isEmpty()) {
                j = new JSONAdapter(mContext, uri,
                        actRef, false);
                j.execute("");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
