package ai.tomorrow.todolist1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TodoDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "TodoDbHelper";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "ToDoList.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ToDoListContract.ToDoListEntry.TABLE_NAME + " (" +
                    ToDoListContract.ToDoListEntry._ID + " INTEGER PRIMARY KEY," +
                    ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE + " TEXT," +
                    ToDoListContract.ToDoListEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ToDoListContract.ToDoListEntry.TABLE_NAME;



    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + "oldVersion=" + oldVersion + "  newVersion = " + newVersion);
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}