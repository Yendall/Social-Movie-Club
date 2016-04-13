package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.ActivityJumper;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.IndividualMovieActivity;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.PartyActivity;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.SearchActivity;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

/**
 * Created by Max Yendall on 10/15/2015.
 */
public class SearchViewObjectAdapter extends ArrayAdapter<Movie> {

    // Declare class data
    private ArrayList< Movie> mData;
    private Context context;
    public SearchViewObjectAdapter(Context context, ArrayList<Movie> data) {

        // Set globals
        super(context, 0, data);
        this.mData = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Declare immutable json object for assignment
        final Movie mObj = getItem(position);
        // Inflate layout and assign current view
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_object, parent, false);
        }

        // Fetch all necessary fields for setting
        TextView titleLabel = (TextView) convertView.findViewById(R.id.movieTitle);
        Button viewButton = (Button) convertView.findViewById(R.id.viewButton);

        // Set values
        titleLabel.setText(mObj.getmTitle());
        titleLabel.setTextColor(Color.parseColor(mObj.getmColorHex()));

        // Set Intent and Listener
        Intent existingIntent = new Intent(context, PartyActivity.class);
        Intent individualIntent = new Intent(context, IndividualMovieActivity.class);

        existingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        existingIntent.putExtra("ID", mObj.getmId());
        existingIntent.putExtra("Init",true);

        individualIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        individualIntent.putExtra("ID", mObj.getmId());
        individualIntent.putExtra("title", mObj.getmTitle());
        individualIntent.putExtra("rating",mObj.getmRating());
        individualIntent.putExtra("year",mObj.getmYear());
        individualIntent.putExtra("plot",mObj.getmShortPlot());
        individualIntent.putExtra("posterURI", mObj.getmPoster());
        individualIntent.putExtra("hasParty",mObj.getmHasParty());
        individualIntent.putExtra("Init", false);
        individualIntent.putExtra("Search",true);

        if(mObj.getmHasParty()) {
            viewButton.setOnClickListener(new ActivityJumper(existingIntent, context));
        }
        else
        {
            viewButton.setOnClickListener(new ActivityJumper(individualIntent, context));
        }

        return convertView;
    }
}
