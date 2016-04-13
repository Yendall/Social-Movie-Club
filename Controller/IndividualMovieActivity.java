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

import java.io.InputStream;
import java.util.HashMap;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieSQLAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartyAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartyListAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.ModelSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

public class IndividualMovieActivity extends Activity {

    // Set your Image URL into a string
    String PosterURI;
    ImageView mPoster;
    ProgressDialog mProgressDialog;
    private ModelSingleton modelSingleton = ModelSingleton.getSingleton();
    private MovieSQLAdapter movieSQLAdapter;
    private HashMap< String, Party > partyList = new HashMap < String,Party > ();
    private ListView pListView;
    private PartyListAdapter partyAdapter;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        movieSQLAdapter = new MovieSQLAdapter(getApplicationContext());
        bundle = getIntent().getExtras();
        final String Id = bundle.getString("ID");
        final String Title = bundle.getString("title");
        final String Year = bundle.getString("year");
        final float Rating = bundle.getFloat("rating");
        final String Plot = bundle.getString("plot");
        boolean hasParty = bundle.getBoolean("hasParty");
        PosterURI = bundle.getString("posterURI");

        // Initialise views
        TextView mTitle = (TextView) findViewById(R.id.labelTitle);
        TextView mYear = (TextView) findViewById(R.id.labelYear);
        TextView mPlot = (TextView) findViewById(R.id.labelShortPlot);
        mPoster = (ImageView) findViewById(R.id.moviePoster);
        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);


        if(modelSingleton.getpListComplete(Id) != null) {
            if (modelSingleton.getpListComplete(Id).size() > 0) {
                hasParty = true;
            }
        }

        // Execute DownloadImage AsyncTask
        new DownloadImage().execute(PosterURI);
        mTitle.setText(Title);
        mYear.setText(Year);
        mPlot.setText(Plot);
        mRatingBar.setRating(Rating);
        addListenerOnRatingBar(Id);

        final Movie add_movie = new Movie(Id,Title,Year,PosterURI,Plot,Plot,Rating);
        add_movie.setmColorHex(getApplicationContext().getString(R.string.red));

        if(!hasParty) {
            PartyAdapter partyAdapter = new PartyAdapter();
            modelSingleton.pushMovieToMemoryModel(add_movie);
            partyAdapter.partyInit(add_movie, getApplicationContext());
        }
        if(hasParty) {
            partyList = modelSingleton.getpListComplete(Id);
            // Display Party information
            pListView = (ListView) findViewById(R.id.partyListView);
            partyAdapter = new PartyListAdapter(this, partyList,true);
            pListView.setAdapter(partyAdapter);
            modelSingleton.pushMovieToMemoryModel(add_movie);
        }


        Intent scheduleIntent = new Intent(getApplicationContext(), PartyController.class);
        scheduleIntent.putExtra("mID", Id);
        scheduleIntent.putExtra("mTitle",Title);
        scheduleIntent.putExtra("mYear",Year);
        scheduleIntent.putExtra("mRating",Rating);
        scheduleIntent.putExtra("mShortPlot",Plot);
        scheduleIntent.putExtra("mFullPlot",Plot);
        scheduleIntent.putExtra("mPoster",PosterURI);
        scheduleIntent.putExtra("Method", "Schedule");
        scheduleIntent.putExtra("Search",true);

        scheduleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scheduleButton.setOnClickListener(new ActivityJumper(scheduleIntent, getApplicationContext()));
        // Setup action bar
        setActionBar(R.string.app_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_individual_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(IndividualMovieActivity.this);
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

            jumper_intent = new Intent(getApplicationContext(), StartScreen.class);
            jumper_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(jumper_intent);
            return true;
        }
        return false;
    }
}
