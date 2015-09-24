package com.arora.quotereminder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ahsanferoz on 23/09/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DbContract.DB_NAME, null, DbContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery =
                String.format("CREATE TABLE %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT," +
                                "%s TEXT," +
                                "%s TEXT)" , DbContract.TABLE,
                                             DbContract.Columns.QUOTE,
                                             DbContract.Columns.VALIDITY,
                                             DbContract.Columns.COUNTER);
        Log.d("DBHelper", "Create the table: " + sqlQuery);
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DbContract.TABLE);
        onCreate(db);
    }
}
