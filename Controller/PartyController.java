package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.FirebaseAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.MovieSQLAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartyAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartyControllerAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters.PartySQLAdapter;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Contact;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.LocalDatabaseSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.ModelSingleton;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;
/*
Student Name:       Max Yendall
Student Number:     s3436993
Course:             Mobile Application Development
Version             2.0
Project:            Movie Social Club
 */
public class PartyController extends Activity {

    // Declare necessary references
    private static String mIDRef;
    private static String pIDRef;
    private static ModelSingleton singleton = ModelSingleton.getSingleton();
    private Calendar calendar = Calendar.getInstance();
    private PartyControllerAdapter contactList;
    private Bundle bundle;
    private boolean newParty;
    private boolean inviteNotEmpty = false;
    private boolean fieldsNotEmpty = false;
    private Button saveButton;
    private ArrayList < Contact > newPartyContacts = new ArrayList < Contact > ();
    public PartySQLAdapter partySQLAdapter;

    // Declare contact manager utilities
    private static final String LOG_TAG = ContactDataManager.class.getName();
    protected static final int PICK_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_controller);
        // Declare bundle and assign finals
        bundle = getIntent().getExtras();
        final String method = bundle.getString("Method");
        mIDRef = bundle.getString("mID");
        partySQLAdapter = new PartySQLAdapter(getApplicationContext());
        // Set the custom action bar
        setActionBar(R.string.title_activity_party_controller);
        // Check if the bundle has a party linked to it
        if (getIntent().hasExtra("pID")) {
            final String pId = bundle.getString("pID");
            pIDRef = pId;
        }

        // Determine which XML layout to display and handle internal procedures
        if (method != null) {
            if (method.equals("Invite")) {
                initialiseInviting();
            } else if (method.equals("Edit")) {
                initialiseEditing();
            } else if (method.equals("Schedule")) {
                initialiseScheduling();
            } else if (method.equals("Delete")) {
                initialiseDeleting();
            }
        }

    }
    /**
     * This method is used to delete a Party
     */
    private void initialiseDeleting() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PartyController.this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Delete...");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want delete this party?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                PartySQLAdapter partySQLAdapter = new PartySQLAdapter(getApplicationContext());
                singleton.deletePartyFromSingleton(mIDRef, pIDRef);
                partySQLAdapter.deletePartyFromDatabase(pIDRef);

                finish();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
    /**
     * This method is used to schedule a new Party
     */
    private void initialiseScheduling() {


        // Set new XML file for scheduling
        setContentView(R.layout.schedule_layout);

        // Set save button
        saveButton = (Button) findViewById(R.id.saveButton);

        // Declare necessary variables for editing
        final EditText addVenue = (EditText) findViewById(R.id.addVenue);
        final EditText addDate = (EditText) findViewById(R.id.addDate);
        final EditText addLocation = (EditText) findViewById(R.id.addLocation);
        ListView cListView = (ListView) findViewById(R.id.contactListView);
        contactList = new PartyControllerAdapter(this, newPartyContacts, null);
        cListView.setAdapter(contactList);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "d-M-yyyy hh:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                addDate.setText(sdf.format(calendar.getTime()));
            }
        };

        // Set on focus change listener so date picker shows immediately
        addDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(PartyController.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        // Set onclick listener for saving data
        Button scheduleInviteButton = (Button) findViewById(R.id.attendeeButton);

        // Set default save button
        saveButton.setEnabled(false);

        // Monitor third text field
        addLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() != 0 && !addDate.getText().toString().matches("")
                        && !addVenue.getText().toString().matches(""))
                {
                    fieldsNotEmpty = true;
                }
                if (s.toString().trim().length() != 0 && !addDate.getText().toString().matches("")
                        && !addVenue.getText().toString().matches("") && inviteNotEmpty) {
                    saveButton.setEnabled(true);
                } else {
                    saveButton.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (s.toString().trim().length() != 0 && !addDate.getText().toString().matches("")
                        && !addVenue.getText().toString().matches(""))
                {
                    fieldsNotEmpty = true;
                }
                if (s.toString().trim().length() != 0 && !addDate.getText().toString().matches("")
                        && !addVenue.getText().toString().matches("") && inviteNotEmpty) {
                    saveButton.setEnabled(true);
                } else {
                    saveButton.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() != 0 && !addDate.getText().toString().matches("")
                        && !addVenue.getText().toString().matches(""))
                {
                    fieldsNotEmpty = true;
                }
                if (s.toString().trim().length() != 0 && !addDate.getText().toString().matches("")
                        && !addVenue.getText().toString().matches("") && inviteNotEmpty) {
                    saveButton.setEnabled(true);
                } else {
                    saveButton.setEnabled(false);
                }

            }
        });


        scheduleInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newParty = true;
                initialiseInviting();
            }
        });
        // Set onclick listener and save the data/return to party details
        saveButton.setOnClickListener(new View.OnClickListener() {@Override
                                                                  public void onClick(View v) {
            FirebaseAdapter firebaseAdapter = new FirebaseAdapter(getApplicationContext());
            // Check for empty fields and display error message
            if (addDate.getText().toString().matches("")
                    || addVenue.getText().toString().matches("")
                    || addLocation.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(),
                        R.string.toastEmptyField,
                        Toast.LENGTH_SHORT).show();
            } else {
                // HashMap < String, Party > parties = singleton.getpListComplete(mIDRef);
                UUID newId = UUID.randomUUID();
                // Append data in the Singleton and return to party details
                Party new_party = new Party(mIDRef, newId.toString(), addDate.getText().toString(),
                        addVenue.getText().toString(),
                        addLocation.getText().toString(), newPartyContacts);

                newParty = false;

                // Add party to the database for lookup
                PartySQLAdapter partySQLAdapter = new PartySQLAdapter(getApplicationContext());
                partySQLAdapter.addPartyToDatabase(new_party);

                finish();

            }
        }
        });
    }
    /**
     * This method is used to initialise the invitation of a Contact
     */
    private void initialiseInviting() {

        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(contactPickerIntent, PICK_CONTACTS);
    }

    /**
     * This method is used to initialise the editing of a Party
     */
    private void initialiseEditing() {

        // Set new XML file for editing
        setContentView(R.layout.edit_layout);

        // Set save button
        saveButton = (Button) findViewById(R.id.saveButton);

        // Declare necessary variables for editing
        final EditText editVenue = (EditText) findViewById(R.id.editVenue);
        final EditText editDate = (EditText) findViewById(R.id.editDate);
        final EditText editLocation = (EditText) findViewById(R.id.editLocation);
        ListView cListView = (ListView) findViewById(R.id.contactListView);
        contactList = new PartyControllerAdapter(this,
                singleton.getPartyById(mIDRef, pIDRef).getpInvitees(),
                singleton.getPartyById(mIDRef, pIDRef));

        // Set default text values of the EditText fields
        editVenue.setText(singleton.getPartyById(mIDRef, pIDRef).getpVenue());
        editDate.setText(singleton.getPartyById(mIDRef, pIDRef).getpDate());
        editLocation.setText(singleton.getPartyById(mIDRef, pIDRef).getpLocation());

        // Set adapter for attendees list
        cListView.setAdapter(contactList);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "d-M-yyyy hh:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                editDate.setText(sdf.format(calendar.getTime()));
            }
        };

        // Set on focus change listener so date picker shows immediately
        editDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {@Override
                                                                            public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                new DatePickerDialog(PartyController.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }
        });

        // Set onclick listener for saving data
        Button inviteButton = (Button) findViewById(R.id.attendeeButton);

        inviteButton.setOnClickListener(new View.OnClickListener() {@Override
                                                                    public void onClick(View v) {
            newParty = false;
            initialiseInviting();
        }
        });
        saveButton.setEnabled(true);
        // Set onclick listener and save the data/return to party details
        saveButton.setOnClickListener(new View.OnClickListener() {@Override
                                                                  public void onClick(View v) {

            // Check for empty fields and display error message
            if (editDate.getText().toString().matches("") || editVenue.getText().toString().matches("") || editLocation.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(),
                        R.string.toastEmptyField,
                        Toast.LENGTH_SHORT).show();
            } else {

                // Append data in the Singleton and return to party details
                singleton.getPartyById(mIDRef, pIDRef).setpVenue(editVenue.getText().toString());
                singleton.getPartyById(mIDRef, pIDRef).setpDate(editDate.getText().toString());
                singleton.getPartyById(mIDRef, pIDRef).setpLocation(editLocation.getText().toString());

                partySQLAdapter.updatePartyModel(singleton.getPartyById(mIDRef, pIDRef));

                finish();
            }
        }
        });

    }

    /**
     * This method is used to delete a contact
     * @param position      This is the position of the contact in the Hash Map
     */
    public static void deleteContact(int position) {
        singleton.getPartyById(mIDRef, pIDRef).getpInvitees().remove(position);

    }


    /**
     * This method is used to set a new contact for a party (existing or new)
     * @param info          This is contact information
     * @param newContacts   This is the ArrayList of contact for a new party
     * @param newParty      This is a Boolean value to determine the status of the party
     */
    public void setNewContact(HashMap < String, String > info,
                              ArrayList < Contact > newContacts, boolean newParty) {

        String name = info.get("Name");
        String email = info.get("Email");
        String phone = info.get("Phone");
        Contact new_contact = new Contact(name, email, phone,pIDRef);
        if (!newParty) {
            FirebaseAdapter firebaseAdapter = new FirebaseAdapter(getApplicationContext());
            PartySQLAdapter partySQLAdapter = new PartySQLAdapter(getApplicationContext());

            singleton.getPartyById(mIDRef, pIDRef).addInvitee(new_contact);

        } else {
            newContacts.add(new_contact);
        }
    }

    /**
     * This method is used to set a new contact for a party (existing or new)
     * @param requestCode   This is the request code for the action invoked
     * @param resultCode    This is the result of the request
     * @param data          This is the data returned from the request
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Declare contact storage
        HashMap < String, String > contactInfo = new HashMap < String, String > ();
        ArrayList < Contact > contacts = new ArrayList < > ();

        // Assign contacts to existing data if party already exists
        if (!newParty) {
            contacts = singleton.getPartyById(mIDRef, pIDRef).getpInvitees();
        }

        // Assume contact does not exist
        boolean exists = false;

        // Begin contact picking
        if (requestCode == PICK_CONTACTS) {
            if (resultCode == RESULT_OK) {
                ContactDataManager contactsManager = new ContactDataManager(this, data);
                String name ;
                String email;
                try {
                    // Assign data to HashMap and set up new contact
                    name = contactsManager.getContactName();
                    email = contactsManager.getContactEmail();
                    contactInfo.put("Name", name);
                    contactInfo.put("Email", email);
                    contactInfo.put("Phone", "");

                    // Check contact existence
                    for (int i = 0; i < contacts.size(); i++) {
                        if (contactInfo.get("Name").equals(contacts.get(i).getDisplayName())) {
                            exists = true;
                        }
                    }

                    // If contact hasn't been added, decide whether to append or create
                    if (!exists) {
                        if (newParty) {
                            setNewContact(contactInfo, newPartyContacts, true);
                            inviteNotEmpty = true;
                            if(fieldsNotEmpty) {
                                saveButton.setEnabled(true);
                            }

                        } else {
                            setNewContact(contactInfo, newPartyContacts, false);
                        }
                    } else {

                        // Contact exists, display error message using Toast
                        Toast.makeText(getApplicationContext(), R.string.toastExistingContact,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (ContactDataManager.ContactQueryException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            } else {
                // Contact wasn't chosen, return to movie details
                //finish();
            }
        }
    }

    // Set up the action bar
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
     * This method is used to notify when data has been changed. It is part of the lifecycle.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (contactList != null) {
            contactList.notifyDataSetChanged();
        }
    }

}