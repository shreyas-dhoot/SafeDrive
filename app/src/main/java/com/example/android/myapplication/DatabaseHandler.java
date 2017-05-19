package com.example.android.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Shreyas_Dhoot on 5/18/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "venueManager";

    // Venues table name
    private static final String TABLE_VENUES = "venues";

    // Venues Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CITY = "city";
    private static final String KEY_VENUEID = "venueid";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_STAT = "stat";
    private static final String KEY_RATING = "rating";
    private static final String KEY_PHOTO = "photoJSON";
    private static final String KEY_LIKES = "Likes";
    Context context;

    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VENUES_TABLE = "CREATE TABLE " + TABLE_VENUES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_CITY + " TEXT," + KEY_VENUEID + " TEXT," + KEY_CATEGORY + " TEXT," + KEY_ADDRESS + " TEXT,"
                + KEY_DISTANCE + " FLOAT," + KEY_STAT + " TEXT," + KEY_RATING + " FLOAT," + KEY_PHOTO + " TEXT," + KEY_LIKES + " INTEGER" + ")";
        db.execSQL(CREATE_VENUES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUES);

        // Create tables again
        onCreate(db);
    }

    // Adding new Venue
    public void addVenue(VenueObject venue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, venue.getName()); // Venue Name
        values.put(KEY_CITY, venue.getCity()); // City Name
        values.put(KEY_VENUEID, venue.getvenueID()); // Venue ID
        values.put(KEY_CATEGORY, venue.getCategory()); // Category
        values.put(KEY_ADDRESS, venue.getAddress()); // Address
        values.put(KEY_DISTANCE, venue.getDistance()); // Distance
        values.put(KEY_STAT, venue.getStat()); // Stats
        values.put(KEY_RATING, venue.getRating()); // Rating
        values.put(KEY_PHOTO, venue.getphotJSON()); // PhotoJSON
        values.put(KEY_LIKES, venue.getLikes()); // Likes

        // Inserting Row
        long rowInserted = db.insert(TABLE_VENUES, null, values);
        /*if(rowInserted != -1)
            Toast.makeText(context, "New row added, row id: " + rowInserted, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();*/
        db.close(); // Closing database connection
    }

    // Getting All Venues
    public ArrayList<VenueObject> getAllVenues() {
        ArrayList<VenueObject> venueList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VENUES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                VenueObject venue = new VenueObject();
                //venue.setID(Integer.parseInt(cursor.getString(0)));
                String str = cursor.getString(1);
                venue.setName(str);
                venue.setCity(cursor.getString(2));
                venue.setvenueID(cursor.getString(3));
                venue.setCategory(cursor.getString(4));
                venue.setAddress(cursor.getString(5));
                venue.setDistance(Float.parseFloat(cursor.getString(6)));
                venue.setStat(cursor.getString(7));
                venue.setRating(Float.parseFloat(cursor.getString(8)));
                venue.setphotoJSON(cursor.getString(9));
                venue.setLikes(Integer.parseInt(cursor.getString(10)));
                // Adding Venue to list
                venueList.add(venue);
            } while (cursor.moveToNext());
        }
        db.close();
        // return Venue list
        return venueList;
    }

    // Getting Venues Count
    public int getVenuesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_VENUES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return count;
    }

    // Delete all Venues
    public void deleteAllVenues() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from "+ TABLE_VENUES);
        db.close();
    }
}
