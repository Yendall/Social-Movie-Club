/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.ActivityJumper;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.IndividualMovieActivity;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.PartyActivity;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

/**
 * Created by Max Yendall on 10/3/2015.
 */
public class SearchViewJSONAdapter extends ArrayAdapter<JSONObject> {

    // Declare class data
    private ArrayList< JSONObject> jData;
    private Context context;
    MovieSQLAdapter mSQLAdapter;
    public SearchViewJSONAdapter(Context context, ArrayList<JSONObject> data) {

        // Set globals
        super(context, 0, data);
        this.jData = data;
        this.context = context;
        mSQLAdapter = new MovieSQLAdapter(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Declare immutable json object for assignment
        final JSONObject jObj = getItem(position);
        // Inflate layout and assign current view
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_object, parent, false);
        }

        // Fetch all necessary fields for setting
        TextView titleLabel = (TextView) convertView.findViewById(R.id.movieTitle);
        titleLabel.setTextColor(Color.parseColor(context.getString(R.string.yellow)));
        Button viewButton = (Button) convertView.findViewById(R.id.viewButton);

        // Set values
        try {
            titleLabel.setText(jObj.get("Title").toString());
            titleLabel.setTextColor(Color.parseColor("#FFFF00"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set Intent and Listener
        Intent activityIntent = new Intent(context, IndividualMovieActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            float mRating = 0;
            if(jObj.get("imdbRating").toString().equals("N/A"))
            {
                mRating = 0;
            }
            else
            {
                mRating = Float.parseFloat(jObj.get("imdbRating").toString())/2;
            }
            activityIntent.putExtra("ID", jObj.get("imdbID").toString());
            activityIntent.putExtra("title", jObj.get("Title").toString());
            activityIntent.putExtra("rating",mRating);
            activityIntent.putExtra("year",jObj.get("Year").toString());
            activityIntent.putExtra("plot",jObj.get("Plot").toString());
            activityIntent.putExtra("posterURI", jObj.get("Poster").toString());
            activityIntent.putExtra("Init","false");
            activityIntent.putExtra("hasParty",false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    float mRating = 0;
                    if(jObj.get("imdbRating").toString().equals("N/A"))
                    {
                        mRating = 0;
                    }
                    else
                    {
                        mRating = Float.parseFloat(jObj.get("imdbRating").toString())/2;
                    }
                    Movie new_movie = new Movie(
                            jObj.get("imdbID").toString(),
                            jObj.get("Title").toString(),
                            jObj.get("Year").toString(),
                            jObj.get("Poster").toString(),
                            jObj.get("Plot").toString(),
                            jObj.get("Plot").toString(),
                            mRating
                    );

                    mSQLAdapter.addMovieToDatabase(new_movie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t.run();
        t.interrupt();
        viewButton.setOnClickListener(new ActivityJumper(activityIntent, context));
        return convertView;
    }
}
