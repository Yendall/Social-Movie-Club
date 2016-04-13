package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Contact;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.LocalDatabaseSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.ModelSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;

/*
Student Name:       Max Yendall
Student Number:     s3436993
Course:             Mobile Application Development
Version:            2.0
Project:            Movie Social Club
 */
public class FirebaseAdapter {
    Context mContext;
    PartySQLAdapter partySQLAdapter;
    MovieSQLAdapter movieSQLAdapter;
    ModelSingleton modelSingleton = ModelSingleton.getSingleton();
    LocalDatabaseSingleton localDatabaseSingleton;
    Firebase data_reference;
    String partyID;
    /**
     * FirebaseAdapter constructor. Creates references for use in the class methods
     * @param c    Context passed in for reference
     */
    public FirebaseAdapter(Context c)
    {
        this.mContext = c;
        partySQLAdapter = new PartySQLAdapter(c);
        localDatabaseSingleton = LocalDatabaseSingleton.getInstance(c);
        movieSQLAdapter = new MovieSQLAdapter(c);
    }

    /**
     * Syncs with Firebase by pushing parties to the database if they are not already up to date
     * @return boolean      Returns the status of the sync
     */
    public boolean syncFirebase()
    {
        // Declare the local database and raw query for fetching
        SQLiteDatabase database = localDatabaseSingleton.getMyWritableDatabase();
        String sync_query = "SELECT * FROM "+ LocalDatabaseSingleton.TABLE_PARTIES+" ;";
        Cursor new_cursor = database.rawQuery(sync_query,null);
        data_reference = new Firebase("https://max-socialmovieclub.firebaseio.com/parties");

        // Loop through the current parties in the database
        if(new_cursor != null && new_cursor.getCount() > 0)
        {
            // Move to the first row in the parties table
            new_cursor.moveToFirst();
            while((!new_cursor.isAfterLast())
                    && new_cursor.getPosition() != new_cursor.getCount()) {

                // Declare the party ID for reference
                final String partyId = new_cursor.getString(
                        new_cursor.getColumnIndex(LocalDatabaseSingleton.PARTIES_PARTY_ID));

                // Start adding data to the Firebase data store
                data_reference.child(partyId).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                        {
                            Party sync_party = partySQLAdapter.fetchParty(partyId);
                            data_reference.child(partyId).setValue(sync_party);
                            data_reference.child(partyId).child("invitees").setValue
                                    (
                                            partySQLAdapter.fetchPartyInvitees(partyId)
                                    );
                            data_reference.child(partyId).child("lastUpdated").setValue
                                    (System.currentTimeMillis());
                        }
                    }
                    // Default Firebase error checking stub
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                // Iterate the cursor
                new_cursor.moveToNext();
            }
            // Close the cursor
            new_cursor.close();
        }
        data_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if(partySQLAdapter.partyExists(childSnapshot.getKey()))
                    {

                        partyID = childSnapshot.getKey();
                        ArrayList<Contact> invitees = new ArrayList<Contact>();
                        for(DataSnapshot contact : childSnapshot.child("pInvitees").getChildren())
                        {

                            Contact new_contact = new Contact (
                                    contact.child(LocalDatabaseSingleton.INVITEES_DISPLAYNAME)
                                        .getValue().toString(),
                                    "",
                                    contact.child(LocalDatabaseSingleton.INVITEES_NUMBER)
                                        .getValue().toString(),
                                    childSnapshot.getKey()
                            );
                            invitees.add(new_contact);
                        }
                        final Party firebase_party = new Party(
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYMOVIEID)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTY_ID)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYDATE)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYVENUE)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYLOCATION)
                                        .getValue().toString(),
                                invitees
                        );

                        partySQLAdapter.addPartyToDatabase(firebase_party);
                        modelSingleton.pushPartyToMemoryModel(firebase_party);

                        Long lastUpdatedFromLocal =
                                partySQLAdapter.getMostRecentUpdate(childSnapshot.getKey());
                        Long  lastUpdatedFromFirebase =
                                Long.parseLong(childSnapshot.child(
                                        LocalDatabaseSingleton.PARTIES_PARTYLASTUPDATED)
                                        .getValue().toString());
                        if(lastUpdatedFromLocal > lastUpdatedFromFirebase )
                        {
                            Party p = partySQLAdapter.fetchParty(childSnapshot.getKey());

                            data_reference.child(partyID).setValue(p);
                            data_reference.child(partyID).child("lastUpdated")
                                    .setValue(System.currentTimeMillis());
                        }
                        else if(lastUpdatedFromFirebase > lastUpdatedFromLocal)
                        {


                            ArrayList<Contact> invitees_local = new ArrayList<Contact>();

                            for(DataSnapshot d : childSnapshot.child("pInvitees").getChildren())
                            {
                                Contact new_contact = new Contact (
                                        d.child("displayName").getValue().toString(),
                                        "",
                                        d.child("phoneNo").getValue().toString(),
                                        childSnapshot.getKey()
                                );
                                invitees_local.add(new_contact);
                            }
                            Party firebase_party_local = new Party(
                                    childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYMOVIEID)
                                            .getValue().toString(),
                                    childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTY_ID)
                                            .getValue().toString(),
                                    childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYDATE)
                                            .getValue().toString(),
                                    childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYVENUE)
                                            .getValue().toString(),
                                    childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYLOCATION)
                                            .getValue().toString(),
                                    invitees_local
                            );
                            partySQLAdapter.updatePartyModel(firebase_party_local);
                        }
                        else
                        {

                            Party p = partySQLAdapter.fetchParty(partyID);
                            data_reference.child(partyID).setValue(p);
                            data_reference.child(partyID).child("lastUpdated")
                                    .setValue(System.currentTimeMillis());
                        }
                    }
                    else
                    {
                        // Push to local database and singleton
                        ArrayList<Contact> invitees = new ArrayList<Contact>();
                        for(DataSnapshot contact : childSnapshot.child("invitees").getChildren())
                        {

                            Contact new_contact = new Contact (
                                    contact.child(LocalDatabaseSingleton.INVITEES_DISPLAYNAME)
                                            .getValue().toString(),
                                    "",
                                    contact.child(LocalDatabaseSingleton.INVITEES_NUMBER)
                                            .getValue().toString(),
                                    childSnapshot.getKey()
                            );
                            invitees.add(new_contact);
                        }
                        final Party firebase_party = new Party(
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYMOVIEID)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTY_ID)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYDATE)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYVENUE)
                                        .getValue().toString(),
                                childSnapshot.child(LocalDatabaseSingleton.PARTIES_PARTYLOCATION)
                                        .getValue().toString(),
                                invitees
                        );

                        partySQLAdapter.addPartyToDatabase(firebase_party);
                    }

                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return false;
    }

}
