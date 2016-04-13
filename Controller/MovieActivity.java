package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieListAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieSQLAdapter;
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
public class MovieActivity extends Activity {

    LocalDatabaseSingleton mLocalDatabaseSingleton;
    // Activity declarations
    private MovieListAdapter partyAdapter;
    private ModelSingleton modelSingleton;
    private ListView mListView;
    private boolean mInit = false;

    /**
     * Default creation method
     * @param savedInstanceState    Collated state information for recreation
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        mLocalDatabaseSingleton = LocalDatabaseSingleton.getInstance(getApplicationContext());
        // Instantiate Singleton
        modelSingleton = ModelSingleton.getSingleton();
        // Display information
        mListView = (ListView) findViewById(R.id.movieListView);
        partyAdapter = new MovieListAdapter(this, modelSingleton.getmListComplete());
        mListView.setAdapter(partyAdapter);

        // Setup action bar
        setActionBar(R.string.app_name);
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
         * */
        @Override
        protected void onRestart() {
        // Refresh activity on restart
        super.onRestart(); // Always call the superclass method first
        partyAdapter.notifyDataSetChanged();
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