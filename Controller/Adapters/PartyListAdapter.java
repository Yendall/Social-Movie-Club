package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.ActivityJumper;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.PartyController;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Contact;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;
/*
Student Name:       Max Yendall
Student Number:     s3436993
Course:             Mobile Application Development
Version:            2.0
Project:            Movie Social Club
 */
public class PartyListAdapter extends BaseAdapter {

    private HashMap<String,Party> pData;
    private Context context;
    private ArrayList<String> keyset = new ArrayList<>();
    private boolean from_search;
    public PartyListAdapter(Context context, HashMap<String,Party> data,boolean search)
    {
        //super(context,0,data);
        this.pData = data;
        this.context = context;
        keyset.addAll(data.keySet());
        this.from_search = search;
    }

    @Override
    public int getCount() {
        return pData.size();

    }
    @Override
    public Party getItem(int position)
    {
        String key = keyset.get(position);
        return pData.get(key);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = convertView;
        final LinearLayout layout = new LinearLayout(context);

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate
                    (R.layout.party_object, parent, false);
        }
        TextView venueLabel = (TextView) convertView.findViewById(R.id.labelVenue);
        TextView dateLabel = (TextView) convertView.findViewById(R.id.labelDate);
        TextView locationLabel = (TextView) convertView.findViewById(R.id.labelLocation);
        TextView invitees = (TextView) convertView.findViewById(R.id.labelInviteeDetails);
        Button editButton = (Button) convertView.findViewById(R.id.editButton);
        Button inviteButton = (Button) convertView.findViewById(R.id.attendeeButton);
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteButton);

        // Hash map item
        Party pObj = getItem(position);

        // Fetch contact list and display accordingly
        ArrayList<Contact> inviteeList = pObj.getpInvitees();
        String inviteeDisplay = "";
        for(int i=0; i<inviteeList.size();i++)
        {
            String displayName = inviteeList.get(i).getDisplayName();

            if(i == inviteeList.size()-1) {
                inviteeDisplay += (displayName);
            }
            else
            {
                inviteeDisplay += (displayName + ", ");
            }
        }

        // Set text for displaying
        venueLabel.setText(pObj.getpVenue());
        dateLabel.setText(pObj.getpDate());
        locationLabel.setText(pObj.getpLocation());
        if(inviteeDisplay != "") {
            invitees.setText(inviteeDisplay);
        }

        // Set Intents and Listeners
        Intent editIntent = new Intent(context, PartyController.class);
        editIntent.putExtra("pID", pObj.getpId());
        editIntent.putExtra("mID", pObj.getmId());
        editIntent.putExtra("Method", "Edit");
        editIntent.putExtra("Uninitialised", "false");
        editButton.setOnClickListener(new ActivityJumper(editIntent, context));

        Intent attendeeIntent = new Intent(context, PartyController.class);
        attendeeIntent.putExtra("pID", pObj.getpId());
        attendeeIntent.putExtra("mID", pObj.getmId());
        attendeeIntent.putExtra("Method","Invite");
        attendeeIntent.putExtra("Uninitialised", "false");
        inviteButton.setOnClickListener(new ActivityJumper(attendeeIntent, context));

        Intent deleteIntent = new Intent(context, PartyController.class);
        deleteIntent.putExtra("pID", pObj.getpId());
        deleteIntent.putExtra("mID", pObj.getmId());
        deleteIntent.putExtra("Method","Delete");
        deleteIntent.putExtra("Uninitialised", "false");
        deleteButton.setOnClickListener(new ActivityJumper(deleteIntent, context));
        notifyDataSetChanged();
        if(from_search)
        {
            editButton.setVisibility(View.GONE);
            inviteButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
        return convertView;
    }
}