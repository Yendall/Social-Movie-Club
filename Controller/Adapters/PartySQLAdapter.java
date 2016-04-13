/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Contact;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.LocalDatabaseSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.ModelSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;

public class PartySQLAdapter {

    Context mContext;
    LocalDatabaseSingleton mLocalDatabaseSingleton;
    ModelSingleton mModelSingleton;
    String pId;

    /**
     * Constructor for PartySQLAdapter. Fetches singleton instances for use
     * @param c    Context of the current activity
     */
    public PartySQLAdapter(Context c)
    {
        this.mContext = c;
        mLocalDatabaseSingleton = LocalDatabaseSingleton.getInstance(c);
        mModelSingleton = ModelSingleton.getSingleton();
    }
    /**
     * This method adds a movie to the writable singleton database
     * @param new_party    Party object passed in for adding
     */
    public void addPartyToDatabase(Party new_party)
    {
        // Retrieve an instance of the current database
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Insert the values from the movie object into the database
        // Begin inserting values into the database
        ContentValues content = new ContentValues();
        // Insert values from the movie object
        if(!partyExists(new_party.getpId()))
        {
            content.put(LocalDatabaseSingleton.PARTIES_PARTY_ID,new_party.getpId());
            content.put(LocalDatabaseSingleton.PARTIES_PARTYDATE,new_party.getpDate());
            content.put(LocalDatabaseSingleton.PARTIES_PARTYLOCATION,new_party.getpLocation());
            content.put(LocalDatabaseSingleton.PARTIES_PARTYVENUE,new_party.getpVenue());
            content.put(LocalDatabaseSingleton.PARTIES_PARTYMOVIEID,new_party.getmId());
            content.put(LocalDatabaseSingleton.PARTIES_PARTYLASTUPDATED, System.currentTimeMillis());
            // Insert new content into the database
            database.insert(LocalDatabaseSingleton.TABLE_PARTIES, null, content);
            addInviteesToDatabase(new_party);
            mModelSingleton.getpListComplete(new_party.getmId()).put(new_party.getpId(),new_party);
        }
    }
    /**
     * This method adds invitees to the writable database
     * @param new_party    Party object passed in for reference
     */
    public void addInviteesToDatabase(Party new_party)
    {
        // Retrieve an instance of the current database
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Loop through the invitees for this party
        for(Contact c : new_party.getpInvitees())
        {
            ContentValues content = new ContentValues();
            content.put(LocalDatabaseSingleton.INVITEES_DISPLAYNAME,c.getDisplayName());
            content.put(LocalDatabaseSingleton.INVITEES_EMAIL,c.getEmail());
            content.put(LocalDatabaseSingleton.INVITEES_INVITEESPARTYID,new_party.getpId());
            content.put(LocalDatabaseSingleton.INVITEES_NUMBER,c.getPhoneNo());
            content.put(LocalDatabaseSingleton.INVITEES_INVITEESLASTUPDATED,
                    System.currentTimeMillis());
            // Insert new content into the database
            database.insert(LocalDatabaseSingleton.TABLE_CONTACTS,null,content);
        }
    }
    /**
     * This method deletes a party from the singleton writable database
     * @param pId    Party ID passed in for reference
     */
    public void deletePartyFromDatabase(String pId){
        SQLiteDatabase database = mLocalDatabaseSingleton.getMyWritableDatabase();
        database.delete(LocalDatabaseSingleton.TABLE_PARTIES,
                LocalDatabaseSingleton.PARTIES_PARTY_ID + " = ?", new String[]{pId});
    }
    /**
     * This method populates the memory model with parties from the local database
     */
    public void populateParties()
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Declare array list for the invitees of each party
        ArrayList<Contact> fetched_invitees = new ArrayList<>();
        // Declare new cursor for fetching all current movies from the database

        Cursor new_cursor = database.rawQuery
                ("SELECT * FROM " + LocalDatabaseSingleton.TABLE_PARTIES + ";", null);
        // If the new cursor is not empty and it returns at least 1 party
        if(new_cursor != null && new_cursor.getCount() > 0)
        {
            // Iterate through the rows
            while(new_cursor.moveToNext())
            {
                // Grab the invitees for the party
                fetched_invitees = fetchPartyInvitees(
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.PARTIES_PARTY_ID)));

                // Create a new party and push it to the singleton
                Party new_party = new Party(
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.PARTIES_PARTYMOVIEID)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.PARTIES_PARTY_ID)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.PARTIES_PARTYDATE)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.PARTIES_PARTYVENUE)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.PARTIES_PARTYLOCATION)),
                        fetched_invitees);
                // Push the party to the memory model
                mModelSingleton.getpListComplete(new_party.getmId()).put(new_party.getpId(), new_party);
            }
            // Close the cursor
            new_cursor.close();
        }
    }
    /**
     * This method returns an array list of contacts based on the party id passed in
     * @param pID                   Party ID passed in for reference
     * @return ArrayList<Contact>   Returns an arraylist of valid contacts
     */
    public ArrayList<Contact> fetchPartyInvitees(String pID)
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Declare arraylist to return contacts
        ArrayList<Contact> fetched_contacts = new ArrayList<>();
        Cursor new_cursor = database.rawQuery
                ("SELECT * FROM " + LocalDatabaseSingleton.TABLE_CONTACTS
                        + " WHERE " + LocalDatabaseSingleton.INVITEES_INVITEESPARTYID
                        + " = '" + pID + "';", null);
        // If the new cursor is not empty and it returns at least 1 party
        if(new_cursor != null && new_cursor.getCount() > 0)
        {
            // Iterate through the rows
            while(new_cursor.moveToNext())
            {
                Contact new_contact = new Contact(
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.INVITEES_DISPLAYNAME)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.INVITEES_EMAIL)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.INVITEES_NUMBER)),
                        new_cursor.getString(new_cursor.getColumnIndex
                                (LocalDatabaseSingleton.INVITEES_INVITEESPARTYID)));
                fetched_contacts.add(new_contact);
            }
            new_cursor.close();
        }
        // Return the array list
        return fetched_contacts;
    }
    /**
     * This method returns the time in milliseconds that the database was last updated
     * @param pId    Party ID passed in for reference
     * @return lastUpdatedQuery  Returns the time in milliseconds from the database
     */
    public long getMostRecentUpdate(String pId)
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Formulate the argument for the cursor
        String[] argument = { pId};
        // Formulate the columns we wish to fetch
        String[] columns = { LocalDatabaseSingleton.PARTIES_PARTYLASTUPDATED};
        // Formulate the result we wish to use
        String fetchedResult = LocalDatabaseSingleton.PARTIES_PARTY_ID + " = ?";
        // Declare a new cursor and query the SQL with the parameters created
        Cursor new_cursor = database.query(LocalDatabaseSingleton.TABLE_PARTIES, columns,
                fetchedResult, argument, null, null, null);
        // If the cursor is not null and returns at least one result
        if(new_cursor != null && new_cursor.getCount()>0)
        {
            // Move to the first row
            new_cursor.moveToFirst();
            // Create the query return
            Long lastUpdatedQuery = Long.parseLong
                    (new_cursor.getString(new_cursor.getColumnIndex
                            (LocalDatabaseSingleton.PARTIES_PARTYLASTUPDATED)));
            // Close the active cursor
            new_cursor.close();
            // Return the result
            return lastUpdatedQuery;
        }
        else {
            // No results
            return 0;
        }
    }
    /**
     * This method returns a party from the database based on the party ID
     * @param pId    Party ID passed in for reference
     * @return fetched_party  Returns the Party object if found
     */
    public Party fetchParty(String pId)
    {
        Party fetched_party;
        ArrayList<Contact> fetched_invitees = new ArrayList<>();
        SQLiteDatabase database = mLocalDatabaseSingleton.getMyWritableDatabase();
        String[] columns= {
                LocalDatabaseSingleton.PARTIES_PARTY_ID,
                LocalDatabaseSingleton.PARTIES_PARTYLOCATION,
                LocalDatabaseSingleton.PARTIES_PARTYDATE,
                LocalDatabaseSingleton.PARTIES_PARTYVENUE,
                LocalDatabaseSingleton.PARTIES_PARTYMOVIEID};
        String selection = LocalDatabaseSingleton.PARTIES_PARTY_ID + " =?";
        String[] selectionArgs = { pId }; // matched to "?" in selection
        Cursor new_cursor = database.query(LocalDatabaseSingleton.TABLE_PARTIES,
                columns, selection, selectionArgs, null, null, null);

        if(new_cursor.moveToFirst()){
            // Grab the invitees for the party
            fetched_invitees = fetchPartyInvitees(
                    new_cursor.getString(new_cursor.getColumnIndex
                            (LocalDatabaseSingleton.PARTIES_PARTY_ID)));
            fetched_party= new Party(
                    new_cursor.getString(
                            new_cursor.getColumnIndex(LocalDatabaseSingleton.PARTIES_PARTYMOVIEID)),
                    new_cursor.getString(
                            new_cursor.getColumnIndex(LocalDatabaseSingleton.PARTIES_PARTY_ID)),
                    new_cursor.getString(
                            new_cursor.getColumnIndex(LocalDatabaseSingleton.PARTIES_PARTYDATE)),
                    new_cursor.getString(
                            new_cursor.getColumnIndex(LocalDatabaseSingleton.PARTIES_PARTYVENUE)),
                    new_cursor.getString(
                            new_cursor.getColumnIndex(LocalDatabaseSingleton.PARTIES_PARTYLOCATION)),
                    fetched_invitees
                    );

            // Close the curosr
            new_cursor.close();

            // Return the generated party
            return fetched_party;
        }

        // We couldn't find a party
        return null;
    }
    /**
     * This method checks if a party exists in the local database.
     * @param pId      Party ID passed in for reference
     * @return bool    Returns a boolean showing whether the movie exists or not
     */
    public boolean partyExists(String pId)
    {
        // Declare the database instance from the database singleton
        SQLiteDatabase database = mLocalDatabaseSingleton.getWritableDatabase();
        // Formulate the argument for the cursor
        String[] argument = { pId };
        // Formulate the columns we wish to fetch
        String[] columns = { LocalDatabaseSingleton.PARTIES_PARTY_ID };
        // Formulate the result we wish to use
        String fetchedResult = LocalDatabaseSingleton.PARTIES_PARTY_ID + " =?";
        // Declare a new cursor and query the SQL with the parameters created
        Cursor new_cursor = database.query(LocalDatabaseSingleton.TABLE_PARTIES,columns,
                fetchedResult,argument,null,null,null);
        new_cursor.moveToFirst();
        if(new_cursor != null && new_cursor.getCount()>0)
        {
            // Close the cursor
            new_cursor.close();
            // Return
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean updatePartyModel(Party new_party)
    {
        // Fetch an instance of the database
        SQLiteDatabase database = mLocalDatabaseSingleton.getMyWritableDatabase();
        int verify;

        // Create updated content set
        ContentValues content = new ContentValues();
        content.put(LocalDatabaseSingleton.PARTIES_PARTY_ID,new_party.getpId());
        content.put(LocalDatabaseSingleton.PARTIES_PARTYMOVIEID,new_party.getmId());
        content.put(LocalDatabaseSingleton.PARTIES_PARTYVENUE,new_party.getpVenue());
        content.put(LocalDatabaseSingleton.PARTIES_PARTYDATE,new_party.getpDate());
        content.put(LocalDatabaseSingleton.PARTIES_PARTYLOCATION,new_party.getpLocation());
        content.put(LocalDatabaseSingleton.PARTIES_PARTYLASTUPDATED,System.currentTimeMillis());
        verify = database.update(
                LocalDatabaseSingleton.TABLE_PARTIES, content,
                LocalDatabaseSingleton.PARTIES_PARTY_ID + " = '" + new_party.getpId().toString()
                        +"';", null);
        
        return verify > 0;
    }

}
