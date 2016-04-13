/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class ContactDataManager {

    // Declare contact picker utilities and contexts
    private static final String LOG_TAG = ContactDataManager.class.getName();
    private Context context;
    private Intent intent;

    // Define exception handling class
    class ContactQueryException extends Exception {
        private static final long serialVersionUID = 1L;

        public ContactQueryException(String message) {
            super(message);
        }
    }

    /**
     * Sets context and intent for access
     * @param aContext      Application Context
     * @param anIntent      Data Intent for Contact retrieval
     */
    public ContactDataManager(Context aContext, Intent anIntent) {
        this.context = aContext;
        this.intent = anIntent;
    }

    /**
     * Return a Contacts name to the main Party activity
     */
    public String getContactName() throws ContactQueryException {
        Cursor cursor = null;
        String name = null;
        try {
            cursor = context.getContentResolver().query(intent.getData(), null,
                    null, null, null);
            if (cursor.moveToFirst()) name = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME_PRIMARY));

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            throw new ContactQueryException(e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return name;
    }

    /**
     * Return a Contacts email to the main Party activity
     */
    public String getContactEmail() throws ContactQueryException {
        Cursor cursor = null;
        String email = null;
        try {

            cursor = context.getContentResolver().query(Email.CONTENT_URI,
                    null, Email.CONTACT_ID + "=?",
                    new String[] {
                            intent.getData().getLastPathSegment()
                    },
                    null);

            if (cursor.moveToFirst()) email = cursor.getString(cursor.getColumnIndex(Email.DATA));

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            throw new ContactQueryException(e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return email;
    }

}