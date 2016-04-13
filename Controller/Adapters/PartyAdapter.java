package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.Context;
import java.util.HashMap;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;
/*
Student Name:       Max Yendall
Student Number:     s3436993
Course:             Mobile Application Development
Version:            2.0
Project:            Movie Social Club
 */
public class PartyAdapter {
    // Use for initialisation
    private static HashMap < String, HashMap < String, Party >> pList = new HashMap < > ();

    // Obtain complete ArrayList for Singleton instantiation
    public static HashMap < String, HashMap < String, Party >> pList() {
        return pList;
    }

    public void partyInit(Movie movie, Context c) {
        // Declare necessary variables for date and attendee creation
        HashMap < String, Party > newPartyHash = new HashMap < > ();
        pList.put(movie.getmId(), newPartyHash);
    }
}