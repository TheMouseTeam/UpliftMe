package com.barmpas.upliftme.UserActionData;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The contract class for the local SQLite Database class where the user stores the action.
 * @author Konstantinos Barmpas.
 */
public final class UserContract {

    /**
     * The app's authority
     */
    public static final String CONTENT_AUTHORITY = "com.barmpas.upliftme";
    /**
     * The uri for the provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * The title of the table in the SQL Database
     */
    public static final String PATH_USER = "user";

    /**
     * The constructor of the contract
     */
    private UserContract() {}
    /**
     * Connects contract to the SQL Database columns
     */
    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USER);
        public final static String TABLE_NAME = "users";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_GOAL = "goal";
        public final static String COLUMN_ACTIONS = "actions";
        public final static String COLUMN_POINTS = "points";
    }
}