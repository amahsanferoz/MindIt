package com.arora.quotereminder;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arora.quotereminder.db.DBHelper;
import com.arora.quotereminder.db.DbContract;

import java.util.List;
import java.util.Random;

/**
 * Created by ahsanferoz on 29/09/15.
 */
public class Schedule extends BroadcastReceiver{

    public static final String myPackage = "com.arora.quotereminder";
    public static final String DB_NAME = "com.arora.quotereminder.db.quotes";
    private DBHelper helper;
    String quote = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Database Initialized
        SQLiteDatabase sqlDB = context.openOrCreateDatabase(DB_NAME, 0, null);

        //Generates random number for accessing the db ID randomly
        Random random = new Random();

        int quoteCount = 0;

        String totalQuote = String.format("SELECT COUNT(%s) FROM %s",
                DbContract.Columns.QUOTE,
                DbContract.TABLE);


        Cursor cursorRowNumber = sqlDB.rawQuery(totalQuote, null);
        if(cursorRowNumber.moveToFirst()) {
            quoteCount = Integer.parseInt(cursorRowNumber.getString(0));
            Log.d("Schedule", quoteCount + " is the value");
        }

        int randomNumber = random.nextInt(quoteCount) + 1;
        Log.d("Random Number", "Random Number: " + randomNumber);

        String sqlSelect = String.format("SELECT * FROM %s WHERE %s = %s",
                DbContract.TABLE,
                DbContract.Columns._ID,
                randomNumber);

        Cursor cursorSelect = sqlDB.rawQuery(sqlSelect, null);
        if(cursorSelect.moveToFirst()) {
            quote = cursorSelect.getString(1);
            Log.d("Schedule", quote + " is the value");
        }

        //Check the most foreground Activity
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if(componentInfo.getPackageName().equals(myPackage)) {
            Log.d("My Package", "Package Name: " + componentInfo.getPackageName());
            return;
        } else {
            Log.d("My Package", "Package Name: " + componentInfo.getPackageName());

            Intent i = new Intent(context, Quote.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("quote", quote);
            context.startActivity(i);
            //Toast.makeText(context, quote, Toast.LENGTH_SHORT).show();
        }
    }

    private String readRandomQuote() {

        helper = new DBHelper(null);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Random random = new Random();

        int quoteCount = 0;

        String totalQuote = String.format("SELECT COUNT(*) FROM %s",
                DbContract.TABLE);

        Cursor cursorRowNumber = sqlDB.rawQuery(totalQuote, null);
        if(cursorRowNumber.moveToFirst()) {
            quoteCount = Integer.parseInt(cursorRowNumber.getString(1));
            Log.d("Schedule", quoteCount + " is the value");
        }

        int randomNumber = random.nextInt(quoteCount) + 1;

        String sqlSelect = String.format("SELECT * FROM %s WHERE %s = %s",
                DbContract.TABLE,
                DbContract.Columns._ID,
                randomNumber);

        Cursor cursorSelect = sqlDB.rawQuery(sqlSelect, null);
        if(cursorSelect.moveToFirst()) {
            quote = cursorSelect.getString(1);
            Log.d("Schedule", quote + " is the value");
        }

        return quote;
    }
}
