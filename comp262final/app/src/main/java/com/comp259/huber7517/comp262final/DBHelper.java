package com.comp259.huber7517.comp262final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    //DATABASE INFO
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationManager";
    //DATABASE NAME
    private static final String TABLE_NAME = "locations";
    //USER TABLE COLUMNS
    private static final String KEY_ID = "_id";
    private static final String KEY_LATITUDE = "lat";
    private static final String KEY_LONGITUDE = "lon";
    private static final String KEY_DATE = "date";
    private static final String KEY_MARKER = "marker";

    public DBHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //CALLED WHEN THE DATABASE IS CREATED FOR THE FIRST TIME
    //THIS METHOD WILL NOT BE CALLED IF THE DATABASE ALREADY EXISTS (MUST HAVE THE SAME DATABASE_NAME)
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_MARKER + " TEXT)" );
    }

    //CALLED WHEN THE DATABASE NEEDS TO BE UPGRADED.
    //THIS METHOD WILL ONLY BE CALLED IF A DATABASE ALREADY EXISTS ON DISK WITH THE SAME DATABASE_NAME,
    //BUT THE DATABASE_VERSION IS DIFFERENT THAN THE VERSION THAT EXISTS ON DISK.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    //THIS METHOD ADDS A CONTACT INTO THE DATABASE THAT IS PASSED THROUGH THE PARAMETERS
    public void addLocation(Location loc){
        //SET UP CONNECTION
        SQLiteDatabase db = getWritableDatabase();

        //CREATE THE INSERT STATEMENT
        String insert = "INSERT or replace INTO " + TABLE_NAME +  "("
                + KEY_LATITUDE +", "
                + KEY_LONGITUDE + ", "
                + KEY_DATE + ", "
                + KEY_MARKER + ") " +
                "VALUES('"
                + loc.getLatitude() + "','"
                + loc.getLongitude() + "','"
                + loc.getDate() + "','"
                + loc.getMarker()+"')" ;
        //EXECUTE THE INSERT STATEMENT ON THE DATABASE
        db.execSQL(insert);
        //CLOSE CONNECTION
        db.close();
    }



    //THIS METHOD WILL RETURN A CONTACT THAT MATCHES THE ID PASSED THROUGH THE PARAMETERS
    public Location getLocation(int id){
        //SET UP THE DATABASE CONNECTION
        SQLiteDatabase db = getReadableDatabase();

        //CREATE A CURSOR INTERFACE.
        //STORE THE DATABASE QUERY IN THE CURSOR OBJECT AND RETURN THE REFERENCE
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_DATE, KEY_MARKER}, KEY_ID + "=?", new String[]{String.valueOf(id)},null,null,null,null);

        //IF THE CURSOR HAS DATA, THEN PROCEED
        if(cursor!=null){
            //SINCE THE POINTER OF THE RETURNED REFERENCE IS POINTED TO THE 0TH LOCATION INITIALLY,
            //MOVE TO THE FIRST RECORD (WITH THE MOVE TO FIRST METHOD).
            //OTHERWISE NO DATA WILL BE RETURNED.
            cursor.moveToFirst();
        }
        //STORE THE RETURNED DATA IN A PET OBJECT
        Location loc = new Location(Integer.parseInt(cursor.getString(0)), Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)), cursor.getString(3), cursor.getString(4));
        //CLOSE THE DATABASE CONNECTION
        db.close();
        //CLOSE THE CURSOR INTERFACE
        cursor.close();

        //RETURN THE PET OBJECT
        return loc;
    }


    //THIS METHOD WILL RETURN A COUNT OF THE RECORDS IN THE DATABASE
    public int getLocationsCount(){
        //SET UP DATABASE CONNECTION
        SQLiteDatabase db = getReadableDatabase();
        //CREATE A CURSOR INTERFACE
        //WILL RETURN A REFERENCE OF ALL DATA IN TABLE_NAME FROM THIS SELECT COMMAND
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        //GET THE COUNT OF THE DATA RETURNED IN THE CURSOR AND STORE INTO THE COUNT VARIABLE
        int count = cursor.getCount();
        //CLOSE DATABASE CONNECTION
        db.close();
        //CLOSE THE CURSOR INTERFACE
        cursor.close();

        //RETURN THE COUNT
        return count;
    }


    //THIS METHOD GETS ALL THE CONTACTS IN THE DATABASE AND STORES IT IN A GENERIC LIST ARRAY OF TYPE PET
    public List<Location> getAllLocations(){
        //CREATE AN INSTANCE OF LIST<> OF TYPE PET (OBJECT)
        List<Location> allLocations = new ArrayList<Location>();

        //OPEN THE DATABASE CONNECTION
        SQLiteDatabase db = getWritableDatabase();
        //STORE THE DATA RETURNED FROM THE SELECT * STATEMENT INTO A CURSOR
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);

        //MOVE THE CURSOR TO THE 1ST SPOT
        if(cursor.moveToFirst()){

            //THIS LOOP WILL KEEP EXECUTING UNTIL THE CURSOR CAN'T MOVE TO THE NEXT - NEXT=NULL
            do{
                //ADD THE PET DATA TO THE ALL PETS LIST ARRAY
                allLocations.add(new Location(Integer.parseInt(cursor.getString(0)), Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)), cursor.getString(3), cursor.getString(4)));
            }
            //MOVE TO THE NEXT ROW OF DATA IN THE CURSOR
            while(cursor.moveToNext());
        }
        //CLOSE THE CURSOR
        cursor.close();
        //CLOSE THE DATABASE CONNECTION
        db.close();

        //RETURN THE LIST ARRAY OF ALL PET OBJECTS
        return allLocations;
    }

}
