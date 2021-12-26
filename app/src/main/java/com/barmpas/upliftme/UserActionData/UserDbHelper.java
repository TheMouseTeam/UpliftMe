package com.barmpas.upliftme.UserActionData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The helper function class for the local SQLite Database class where the user stores the action.
 * @author Konstantinos Barmpas.
 */
public class UserDbHelper extends SQLiteOpenHelper {

    /**
     * The database's name
     */
    private static final String DATABASE_NAME = "upliftme.db";
    /**
     * The version of the SQL Database
     */
    private static final int DATABASE_VERSION = 1;


    /**
     * The Helper of the SQL Database
     */
    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * onCreate method of the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " ("
                + UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UserContract.UserEntry.COLUMN_DATE + " TEXT NOT NULL , "
                + UserContract.UserEntry.COLUMN_GOAL + " INTEGER NOT NULL , "
                + UserContract.UserEntry.COLUMN_ACTIONS + " TEXT NOT NULL , "
                + UserContract.UserEntry.COLUMN_POINTS + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Upgrade versions of the databse
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Still at version 1, no upgrade required
    }
}
