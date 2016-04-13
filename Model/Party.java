/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Model;

import java.util.ArrayList;

public class Party {

    private String mId;
    private String pId;
    private String pDate;
    private String pVenue;
    private String pLocation;
    private ArrayList<Contact> pInvitees;

    public Party(String mId, String pId, String pDate, String pVenue,
                 String pLocation, ArrayList<Contact> pInvitees) {
        this.mId = mId;
        this.pId = pId;
        this.pDate = pDate;
        this.pVenue = pVenue;
        this.pLocation = pLocation;
        this.pInvitees = pInvitees;
    }
    public void setmId(String setmId) {this.mId = setmId;}
    public void setpId(String setId) {this.pId = setId;}
    public void setpDate(String setDate) {this.pDate = setDate;}
    public void setpVenue(String setVenue) {this.pVenue = setVenue;}
    public void setpLocation(String setLocation) {this.pLocation=setLocation;}
    public void addInvitee(Contact setInvitee) {this.pInvitees.add(setInvitee);}

    public String getmId() { return this.mId; }
    public String getpId() {return this.pId;}
    public String getpDate() {return this.pDate;}
    public String getpVenue() {return this.pVenue;}
    public String getpLocation() {return this.pLocation;}
    public ArrayList<Contact> getpInvitees() {return this.pInvitees;}

    public void deleteContact(int position)
    {
        this.pInvitees.remove(position);
    }

    public int describeContents() {
        return 0;
    }
}
