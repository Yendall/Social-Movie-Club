/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.app.Activity;

import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

/**
 * JSON Request parser class, running in a worker thread to fetch information from OMDB
 *
 */
public class JSONAdapter extends AsyncTask<String,Context,String> {
    HttpURLConnection urlConnection;
    URL mUrl;
    Context mContext;
    Boolean singleQuery;
    Activity actRef;
    ArrayList<JSONObject> jObjs = MovieAdapter.requestObjs();
    MovieSQLAdapter mSQLAdapter;

    public JSONAdapter(Context c, String url, Activity act, Boolean search) throws MalformedURLException {
        mContext = c;
        mSQLAdapter = new MovieSQLAdapter(mContext);
        mUrl = new URL(url);
        singleQuery = search;
        actRef = act;
    }
    /**
     * doInBackground task to fetch data from the OMDB api URL with given search queries
     * @param uri   The URL to create the URL connection and fetch the data
     */
    @Override
    protected String doInBackground(String... uri) {
        // Create a new stringbuilder for the result
        StringBuilder result = new StringBuilder();
        // Test the URL and connect to OMDB for fetching
        try {
            // Set the url to a URL data type
            URL url = mUrl;
            // Assign a HTTP connection and open it
            urlConnection = (HttpURLConnection) url.openConnection();
            // Create new input stream to fetch data from the URL
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            // Read the stream from OMDB
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            // Create a new string and append each character as the stream is read in
            String line;
            // Append the result with the data from the OMDB call
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        // Disconnect from the OMDB server if there is a problem or the result is finished
        finally {
            urlConnection.disconnect();
        }
        // Return the JSON result
        return result.toString();
    }
    /**
     * onPostExecute task to turn into JSON objects and arrays for processing
     * @param result   The result from the OMDB fetch
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Begin processing the response by trimming it and create JSON Arrays/Objects

        // If this queries contains multiple movies/objects, create an array and parse it
        if(!singleQuery) {
            try {
                // Create trimmed result to design JSON Array from JSON result
                String trimmedResult = result.substring(result.indexOf(":") + 1);
                JSONArray JArray = new JSONArray(trimmedResult);
                // Clear current objects for replacement
                MovieAdapter.requestObjs().clear();
                MovieAdapter.parseJSONRequest(JArray, mContext, actRef);
                // Update search listview and set adapter
                ListView jListView = (ListView) actRef.findViewById(R.id.jList);
                SearchViewJSONAdapter searchAdapter =
                        new SearchViewJSONAdapter(actRef.getApplicationContext(), jObjs);
                jListView.setAdapter(searchAdapter);

            } catch (JSONException e) {
                Log.i("Error: ", "JSON array invalid");
            }
        }
        // If this query is only singular, directly create an object and parse it
        else
        {
            try {
                // Create JSONObject from singular query
                final JSONObject j = new JSONObject(result);
                MovieAdapter.requestObjs().add(j);
                // Update search listview and set adapter
                ListView jListView = (ListView) actRef.findViewById(R.id.jList);
                SearchViewJSONAdapter searchAdapter =
                        new SearchViewJSONAdapter(actRef.getApplicationContext(), jObjs);
                jListView.setAdapter(searchAdapter);

            } catch (JSONException e) {
                Log.i("Error: ", "JSON object invalid");
            }
        }
    }
}
