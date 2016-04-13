/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Model;

public class Contact {

    private String displayName;
    private String email;
    private String phoneNo;
    private String pID;

    public Contact(String displayName, String email, String phoneNo,String partyID)
    {
        this.displayName = displayName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.pID = partyID;

    }

    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhoneNo() { return phoneNo; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNo(String phoneNo ) { this.phoneNo = phoneNo; }
}
