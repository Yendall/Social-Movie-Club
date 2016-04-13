package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.ChainOfResponsibilityAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.JSONAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.SearchViewJSONAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

public class SearchActivity extends Activity {
    SearchView search;
    private ListView jListView;
    public static SearchViewJSONAdapter searchAdapter;
    RelativeLayout rl;
    ArrayList<JSONObject> jObjs = MovieAdapter.requestObjs();
    JSONAdapter j;
    private static boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setActionBar(R.string.search_title);

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Focus on search view
        search = (SearchView) findViewById(R.id.searchBar);
        search.onActionViewExpanded();

        //Set on query text listener
        search.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Unused but compulsory
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Declare the chain of responsibility adapter with the search query
                ChainOfResponsibilityAdapter chain_adapter =
                        new ChainOfResponsibilityAdapter(newText,
                                getApplicationContext(),SearchActivity.this,isConnected);
                // Begin the chain of responsibility
                chain_adapter.ChainOfResponsibility();

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    public static class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            isNetworkConnected(context);
        }

        private Boolean isNetworkConnected(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (!isConnected) {
                isConnected = false;
                return false;
            } else {
                isConnected = true;
                return true;
            }
        }
    }

}
