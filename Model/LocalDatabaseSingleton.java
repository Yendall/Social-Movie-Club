/*
* Name:			Max Yendall
* Version:		2.0
* Project:		Movie Social Club
*/

package au.com.airmaxstudios.maxyendall.assignmenttwo.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDatabaseSingleton extends SQLiteOpenHelper {

    protected static LocalDatabaseSingleton mInstance;
    private static SQLiteDatabase myWritableDb;
    protected Context mContext;

    // Declare the table constants
    public static final String TABLE_PARTIES = "parties";
    public static final String TABLE_MOVIES = "movies";
    public static final String TABLE_CONTACTS = "contacts";
    // Declare the party constants
    public static final String PARTIES_PARTY_ID = "pId";
    public static final String PARTIES_PARTYLOCATION = "pLocation";
    public static final String PARTIES_PARTYVENUE = "pVenue";
    public static final String PARTIES_PARTYDATE = "pDate";
    public static final String PARTIES_PARTYMOVIEID = "mId";
    public static final String PARTIES_PARTYLASTUPDATED = "lastUpdated";

    // Declare the movie constants
    public static final String MOVIES_MOVIE_ID = "movieId";
    public static final String MOVIES_TITLE = "movieTitle";
    public static final String MOVIES_YEAR = "movieYear";
    public static final String MOVIES_FULLPLOT = "movieFullPlot";
    public static final String MOVIES_SHORTPLOT = "movieShortPlot";
    public static final String MOVIES_POSTER = "moviePoster";
    public static final String MOVIES_RATING = "movieRating";
    public static final String MOVIES_MOVIE_LAST_UPDATED = "lastUpdated";
    // Declare the invitee constants
    public static final String INVITEES_INVITEESPARTYID = "pId";
    public static final String INVITEES_EMAIL = "email";
    public static final String INVITEES_DISPLAYNAME = "displayName";
    public static final String INVITEES_NUMBER = "phoneNo";
    public static final String INVITEES_INVITEESLASTUPDATED = "lastUpdated";

    // Declare the database constants
    protected static final String DATABASE_NAME = "socialMovieClub";
    protected static final int DATABASE_VERSION = 1;
    private static final String TAG = "DBBaseAdapter";

    // Declare the movie creation SQL query
    private static final String TABLE_CREATE_MOVIE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_MOVIES + "("
            + MOVIES_MOVIE_LAST_UPDATED + " TEXT NOT NULL,"
            + MOVIES_MOVIE_ID + " CHAR(50) PRIMARY KEY NOT NULL,"
            + MOVIES_TITLE + " TEXT NOT NULL,"
            + MOVIES_YEAR + " TEXT NOT NULL,"
            + MOVIES_SHORTPLOT + " TEXT NOT NULL,"
            + MOVIES_FULLPLOT + " TEXT NOT NULL,"
            + MOVIES_POSTER + " TEXT NOT NULL,"
            + MOVIES_RATING + " REAL NOT NULL);";

    // Declare the party creation SQL query
    private static final String TABLE_CREATE_PARTY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PARTIES+ "("
            + PARTIES_PARTYLASTUPDATED + " TEXT NOT NULL,"
            + PARTIES_PARTY_ID + " CHAR(50) PRIMARY KEY NOT NULL,"
            + PARTIES_PARTYLOCATION + " TEXT NOT NULL,"
            + PARTIES_PARTYVENUE + " TEXT NOT NULL,"
            + PARTIES_PARTYDATE + " TEXT NOT NULL,"
            + PARTIES_PARTYMOVIEID + " TEXT NOT NULL);";

    // Declare the invitee creation SQL query
    private static final String TABLE_CREATE_CONTACT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONTACTS+ "("
            + INVITEES_INVITEESLASTUPDATED + " TEXT NOT NULL,"
            + INVITEES_INVITEESPARTYID + " TEXT NOT NULL,"
            + INVITEES_DISPLAYNAME + " TEXT NOT NULL,"
            + INVITEES_NUMBER + " TEXT NOT NULL,"
            + INVITEES_EMAIL + " TEXT);";

    /**
     * Declare the LocalDatabaseSingleton contructor
     * @param context           The current context
     */
    private LocalDatabaseSingleton(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }
    /**
     * Fetch an instance of the database
     * @param context           The current context
     * @return Instance         The instance of the database
     */
    public static synchronized LocalDatabaseSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocalDatabaseSingleton(context);
        }
        return mInstance;
    }
    /**
     * Fetch a writable database for reading and writing
     * @return Writable Database    Writable database created from the instance
     */
    public synchronized SQLiteDatabase getMyWritableDatabase() {
        if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
            myWritableDb = this.getWritableDatabase();
        }
        return myWritableDb;
    }
    /**
     * Closes the writable database. This is thread safe
     */
    @Override
    public synchronized void close() {
        super.close();
        if (myWritableDb != null) {
            myWritableDb.close();
            myWritableDb = null;
        }
    }
    /**
     * Creates the tables in the database
     * @param db    The current database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_PARTY);
        db.execSQL(TABLE_CREATE_MOVIE);
        db.execSQL(TABLE_CREATE_CONTACT);
    }
    /**
     * Upgrades the database
     * @param db            The current database
     * @param oldVersion    The older version of the database
     * @param newVersion    The new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ", which will destroy all old data");
        onCreate(db);
    }
}