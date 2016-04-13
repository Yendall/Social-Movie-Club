package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

/*
Student Name:       Max Yendall
Student Number:     s3436993
Course:             Mobile Application Development
Version:            2.0
Project:            Movie Social Club
 */
public class MovieAdapter {

    // Use for initialisation
    private static ArrayList < Movie > mList = new ArrayList < Movie > ();
    private static ArrayList < JSONObject> requestObjs = new ArrayList<>();
    // Obtain complete ArrayList for Singleton instantiation
    public static ArrayList < Movie > mList() {
        return mList;
    }
    public static ArrayList<JSONObject> requestObjs() {
        return requestObjs;
    }
    /**
     * This method creates a movie array list from JSON objects
     * @param jsonFiles   An arraylist of JSON objects for manipulation
     * @param c           The current context
     * @return mObjs      Returns an arraylist of movie objects
     */
    public static ArrayList < Movie > createMovieArray(ArrayList < JSONObject > jsonFiles, Context c) {
        ArrayList < Movie > mObjs = new ArrayList < > ();
        String packageName = c.getPackageName();
        int j = 0;
        int size = jsonFiles.size();
        for (j = 0; j < size; j++) {
            JSONObject mJson = jsonFiles.get(j);
            try {
                String posterURI = mJson.getString("Poster");
                int mPoster = c.getResources().getIdentifier(packageName + ":drawable/" + posterURI, null, null);
                float mRating = Float.parseFloat(mJson.getString("imdbRating"));
                mRating /= 2;
                Movie new_movie = new
                        Movie(mJson.getString("imdbID"), mJson.getString("Title"),
                        mJson.getString("Year"), mJson.getString("Poster"), mJson.getString("Plot"),
                        mJson.getString("Plot"), mRating);

                mObjs.add(new_movie);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mObjs;
    }
    /**
     * This method parses a JSON request and constructs JSON Arrays
     * @param JArray      An array of JSON objects
     * @param c           The current context
     * @param act         The current calling activity for reference
     */
    public static void parseJSONRequest(JSONArray JArray, Context c, Activity act) {

        ArrayList<JSONObject> jObjs = new ArrayList<>();
        try {
            for(int i=0;i<JArray.length();i++)
            {
                JSONObject curr = JArray.getJSONObject(i);
                String url ="http://www.omdbapi.com";
                url += ("/?i="+curr.get("imdbID")+"&y=&r=json");
                String uri = url.replaceAll("\\s", "+");

                JSONAdapter j = new JSONAdapter(c,uri,act,true);
                j.execute("");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}