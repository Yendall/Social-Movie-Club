/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ActivityJumper implements View.OnClickListener {

    // Listener Declarations
    private Intent passedIntent;
    private Context passedContext;

    /**
     * Sets Intents and Context for onClick action
     * @param intent        Data Intent
     * @param context       Application Context
     */
    public ActivityJumper(Intent intent, Context context) {
        // References for onClick logic
        passedIntent = intent;
        passedContext = context;
    }

    @Override public void onClick(View v) {
        passedContext.startActivity(passedIntent);
    }
}