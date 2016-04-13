/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartyAdapter;

public class ModelSingleton {

    // Adapter hash map with ID linking
    private HashMap<String,Movie> mMap;
    private HashMap<String,HashMap<String,Party>> pMap;

    // Movie Singleton object
    private static ModelSingleton modelSingleton =
            new ModelSingleton(MovieAdapter.mList(), PartyAdapter.pList());

    // Singleton Getter
    public static ModelSingleton getSingleton()
    {
        return modelSingleton;
    }

    // Adapter Constructor
    public ModelSingleton(ArrayList<Movie> movieList,
                          HashMap<String,HashMap<String,Party>> partyMap)
    {

        this.mMap = new HashMap<String, Movie>();
        this.pMap = new HashMap<String,HashMap<String,Party>>();

        HashMap<String,Party> insertHash = new HashMap<>();
        for (Movie movie : movieList)
        {
            this.mMap.put(movie.getmId(), movie);
        }
        pMap = partyMap;
    }

    /**
     * Obtains a movie from the singleton
     * @param mId       The movie ID for reference
     * @return Movie    Returns a movie object if found
     */
    public Movie getMovieById(String mId)
    {
        return mMap.get(mId);
    }
    /**
     * Pushes a new movie to the memory model
     * @param new_movie      The movie object we wish to push to the memory model
     */
    public void pushMovieToMemoryModel(Movie new_movie)
    {
        mMap.put(new_movie.getmId(),new_movie);
    }

    /**
     * Pushes a new party to the memory model
     * @param new_party      The party object we wish to push to the memory model
     */
    public void pushPartyToMemoryModel(Party new_party)
    {
        HashMap<String,Party> new_hash = new HashMap<>();
        new_hash.put(new_party.getpId(),new_party);
        pMap.put(new_party.getmId(),new_hash);
    }


    /**
     * Obtains a complete list of movies from the singleton
     * @return ArrayList<Movie>   Returns an array list of movies
     */
    public ArrayList<Movie> getmListComplete()
    {
        return new ArrayList(mMap.values());
    }
    /**
     * Obtains a party from a certain movie.
     * @param mId       The movie ID for movie referencing
     * @param pId       The party ID for party referencing
     * @return Party    Returns the party object being searched for
     */
    public Party getPartyById(String mId, String pId)
    {
        return pMap.get(mId).get(pId);
    }

    /**
     * Obtain complete list of parties relative to the movie
     * @param mId       Movie ID for reference
     * @return HashMap  HashMap of parties for a single movie
     */
    public HashMap<String,Party> getpListComplete(String mId)
    {
        return pMap.get(mId);
    }

    /**
     * Delete party from hash map
     * @param mId   Movie ID for reference
     * @param pId   Party ID for reference
     */
    public void deletePartyFromSingleton(String mId, String pId)
    {
        pMap.get(mId).remove(pId);
    }
}