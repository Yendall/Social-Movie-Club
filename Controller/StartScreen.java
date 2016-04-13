package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.firebase.client.Firebase;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.FirebaseAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieSQLAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartySQLAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.ModelSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

public class StartScreen extends Activity {

    public boolean mInit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Button movieButton = (Button) findViewById(R.id.movieButton);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        // Intialise Firebase
        Firebase.setAndroidContext(this);
        FirebaseAdapter firebaseAdapter = new FirebaseAdapter(getApplicationContext());

        // Begin by populating the parties and invitees from the database. The parties will
        // only display if the corresponding movie is viewed. Although, they will be in the
        // party singleton, ready for use.
        firebaseAdapter.syncFirebase();

        // Set up the starting screen
        Intent movieIntent = new Intent(getApplicationContext(), MovieActivity.class);
        Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
        movieIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        movieButton.setOnClickListener(new ActivityJumper(movieIntent, getApplicationContext()));
        searchButton.setOnClickListener(new ActivityJumper(searchIntent,getApplicationContext()));
    }
}
