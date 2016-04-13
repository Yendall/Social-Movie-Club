/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.PartyController;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Contact;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Party;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

public class PartyControllerAdapter extends ArrayAdapter<Contact> {
    // Declare variables for manipulation
    private ArrayList<Contact> cData;
    private Context context;
    private Party party;

    // Reference context and data files for use
    public PartyControllerAdapter(Context context, ArrayList<Contact> data, Party party)
    {
        super(context,0,data);
        this.cData = data;
        this.context = context;
        this.party = party;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Fetch object for manipulation
        final Contact cObj = getItem(position);
        final View view = convertView;
        final LinearLayout layout = new LinearLayout(context);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate
                    (R.layout.contact_object, parent, false);
        }

        // Find necessary fields for manipulation
        TextView attendee = (TextView) convertView.findViewById(R.id.attendee);
        Button removeButton = (Button) convertView.findViewById(R.id.removeButton);

        // Set text and define onClick listener for contact removal
        attendee.setText(cObj.getDisplayName());
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PartyController.deleteContact(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}