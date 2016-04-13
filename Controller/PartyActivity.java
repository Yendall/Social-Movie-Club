/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartyListAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.ModelSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

import java.io.InputStream;
import java.util.HashMap;

public class PartyActivity extends Activity {
    private ModelSingleton modelSingleton = ModelSingleton.getSingleton();
    private PartyListAdapter partyAdapter;
    private String mIDRef;
    private HashMap < String, Party > partyList = new HashMap < String,Party > ();
    private ListView pListView;
    boolean search_activity;
    String PosterURI;
    ImageView mPoster;
    ProgressDialog mProgressDialog;

    /**
     * Default creation method
     * @param savedInstanceState    Collated state information for recreation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        // Get movie ID for reference
        Bundle bundle = getIntent().getExtras();
        final String mId = bundle.getString("ID");
        search_activity = bundle.getBoolean("Search");

        mIDRef = mId;
        // Initialise views
        TextView mTitle = (TextView) findViewById(R.id.labelTitle);
        TextView mYear = (TextView) findViewById(R.id.labelYear);
        TextView mPlot = (TextView) findViewById(R.id.labelShortPlot);
        mPoster = (ImageView) findViewById(R.id.moviePoster);
        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);

        // Set values from Singleton
        mTitle.setText(modelSingleton.getMovieById(mId).getmTitle());
        mYear.setText(modelSingleton.getMovieById(mId).getmYear());
        mPlot.setText(modelSingleton.getMovieById(mId).getmShortPlot());
        //mPoster.setImageResource(modelSingleton.getMovieById(mId).getmPoster());
        mRatingBar.setRating(modelSingleton.getMovieById(mId).getmRating());
        PosterURI = (modelSingleton.getMovieById(mId).getmPoster());
        addListenerOnRatingBar(mId);
        new DownloadImage().execute(PosterURI);
        // Obtain party list
        partyList = modelSingleton.getpListComplete(mId);

        // Display Party information
        pListView = (ListView) findViewById(R.id.partyListView);
        partyAdapter = new PartyListAdapter(this, partyList,false);
        pListView.setAdapter(partyAdapter);

        // Set action bar
        setActionBar(R.string.title_party_activity);

        // Set schedule Intent for adding parties
        Intent scheduleIntent = new Intent(getApplicationContext(), PartyController.class);
        scheduleIntent.putExtra("mID", mId);
        scheduleIntent.putExtra("Method", "Schedule");
        scheduleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scheduleButton.setOnClickListener(new ActivityJumper(scheduleIntent, getApplicationContext()));
    }

    /**
     * This method adds a listener so the rating bar can be updated dynamically
     * @param id    ID of the Movie object for dynamic referencing
     */
    public void addListenerOnRatingBar(final String id) {

        // Ensure rating bar is dynamically updated on touch or click
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratingBar.setRating(rating);
                modelSingleton.getMovieById(id).setmRating(rating);
            }
        });
    }

    private void setActionBar(int titleTextRef)
    {
        // Set up action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater actionInflater = LayoutInflater.from(this);

        View actionView = actionInflater.inflate(R.layout.action_bar, null);
        TextView actionTitle = (TextView) actionView.findViewById(R.id.action_bar_text);
        actionTitle.setText(titleTextRef);
        actionBar.setCustomView(actionView);
        actionBar.setDisplayShowCustomEnabled(true);
    }
    /**
     * Default restart method in the lifecycle. Notifies data changes.
     */
    @Override
    protected void onRestart() {
        // Refresh the activity on restart
        super.onRestart(); // Always call the superclass method first
        partyAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain party list
        partyList = modelSingleton.getpListComplete(mIDRef);

        // Display Party information
        pListView = (ListView) findViewById(R.id.partyListView);
        partyAdapter = new PartyListAdapter(this, partyList,false);
        pListView.setAdapter(partyAdapter);


    }
    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(PartyActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Downloading Image");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            mPoster.setImageBitmap(result);
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent jumper_intent;
            if(search_activity)
            {
                jumper_intent = new Intent(getApplicationContext(), SearchActivity.class);
            }
            else
            {
                jumper_intent = new Intent(getApplicationContext(), MovieActivity.class);
            }

            jumper_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(jumper_intent);
            return true;
        }
        return false;
    }
}