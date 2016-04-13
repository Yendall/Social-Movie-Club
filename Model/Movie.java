/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Model;

import android.graphics.Bitmap;

import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

public class Movie {
    private String  mId;
    private String  mTitle;
    private String  mYear;
    private String  mLongPlot;
    private String  mShortPlot;
    private float   mRating;
    private String  mPoster;
    private String  mColorHex;
    private boolean mHasParty;


    public Movie(String mId, String mTitle, String mYear,
                 String mPoster, String mSPlot, String mLPlot,float mRating) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mYear = mYear;
        this.mPoster = mPoster;
        this.mShortPlot = mSPlot;
        this.mLongPlot = mLPlot;
        this.mRating = mRating;
        this.mColorHex = "FFF";
        this.mHasParty = false;

    }

    public void setmId(String setId) {this.mId = setId;}
    public void setmTitle(String setTitle) {this.mTitle = setTitle;}
    public void setmYear(String setYear){this.mYear = setYear;}
    public void setmPoster(String setPoster) {this.mPoster = setPoster;}
    public void setmLongPlot(String setLongPlot) {this.mLongPlot = setLongPlot;}
    public void setmShortPlot(String setShortPlot) {this.mShortPlot = setShortPlot;}
    public void setmRating(float setRating) {this.mRating = setRating;}
    public void setmColorHex(String setColorHex) {this.mColorHex = setColorHex;}
    public void setmHasParty(boolean setHasParty) {this.mHasParty = setHasParty; }

    public String getmId() {return this.mId;}
    public String getmTitle() {return this.mTitle;}
    public String getmYear() {return this.mYear;}
    public String getmPoster() {return this.mPoster;}
    public String getmLongPlot() {return this.mLongPlot;}
    public String getmShortPlot() {return this.mShortPlot;}
    public String getmColorHex() {return this.mColorHex;}
    public float getmRating() {return this.mRating;}
    public boolean getmHasParty() { return this.mHasParty; }


    public int describeContents() {
        return 0;
    }
}
