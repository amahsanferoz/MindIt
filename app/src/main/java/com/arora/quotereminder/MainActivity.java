package com.arora.quotereminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.arora.quotereminder.db.DBHelper;
import com.arora.quotereminder.db.DbContract;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DBHelper helper;
    private ListView listview;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.list);
        updateUI();

        //Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(MainActivity.this, Schedule.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
        prefillDatabase();
        Log.d("Check it", "Loop is done ");
    }

    public void prefillDatabase() {

        String[] quotes = new String[]{
                "Be yourself; everyone else is already taken.",
                "A room without books is like a body without a soul.",
                "Be the change that you wish to see in the world.",
                "If you tell the truth, you don't have to remember anything.",
                "A friend is someone who knows all about you and still loves you.",
                "Live as if you were to die tomorrow. Learn as if you were to live forever.",
                "It is better to be hated for what you are than to be loved for what you are not.",
                "There are only two ways to live your life. One is as though nothing is a miracle. The other is as though everything is a miracle.",
                "Life is what happens to you while you're busy making other plans.",
                "It does not do to dwell on dreams and forget to live.",
        };

        DBHelper helper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i = 0; i < quotes.length; i++) {
            String resoruceName = "a" + i;
            String j = quotes[i];


            values.clear();
            values.put(DbContract.Columns.QUOTE, j);
            db.insertWithOnConflict(DbContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            updateUI();
        }
        /*String quote = "";
        Log.d("MainActivity", quote);

        DBHelper helper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.clear();
        values.put(DbContract.Columns.QUOTE, quote);
        db.insertWithOnConflict(DbContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        updateUI();*/
    }

    public void startS() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    private void startScheduler(int hours) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 60 * hours;

        //The alarm will start at 10:30
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);

        //Repeating
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
        Toast.makeText(this, "Intervel is set for " + hours + " hours", Toast.LENGTH_SHORT).show();
    }

    private void cancelScheduler() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "No more schedule!", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        helper = new DBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        Cursor cursor = sqlDB.query(DbContract.TABLE,
                new String[]{DbContract.Columns._ID, DbContract.Columns.QUOTE, DbContract.Columns.VALIDITY, DbContract.Columns.COUNTER},
                null, null, null, null, null);

        ListAdapter listAdapter = new SimpleCursorAdapter(
                this, R.layout.quote_view,
                cursor,
                new String[]{DbContract.Columns.QUOTE},
                new int[]{R.id.quote},
                0
        );

        listview.setAdapter(listAdapter);
        registerForContextMenu(listview);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select the Action");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //info.position gives the index of the selected item.
        final int indexSelected = info.position;
        final String indexOfListItemSelected = String.valueOf(info.id);
        String itemTitle = (String) item.getTitle();

        helper = new DBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();

        switch (itemTitle) {
            case "Edit":
                String str = "";
                Log.d("ContextMenu", "item " + info.toString() + " item is edited");

                String sqlSelect = String.format("SELECT * FROM %s WHERE %s = %s",
                        DbContract.TABLE,
                        DbContract.Columns._ID,
                        indexOfListItemSelected);

                Cursor cursor = sqlDB.rawQuery(sqlSelect, null);
                if (cursor.moveToFirst()) {
                    str = cursor.getString(1);
                    Log.d("EDIT quote", str + " is the value");
                }

                Log.d("Edit", "item " + str);
                //String bah = cursor.getString();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.addQuoteTitle);
                builder.setMessage(R.string.quoteMessage);
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                inputField.append(str);

                //Positive response
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String quote = inputField.getText().toString();
                        Log.d("Edit the Quote", quote);

                        String sqlUpdate = String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'",
                                DbContract.TABLE,
                                DbContract.Columns.QUOTE,
                                quote,
                                DbContract.Columns._ID,
                                indexOfListItemSelected);

                        DBHelper helper = new DBHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        /*ContentValues values = new ContentValues();

                        values.clear();
                        values.put(DbContract.Columns.QUOTE, quote);
                        db.insertWithOnConflict(DbContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);*/
                        db.execSQL(sqlUpdate);
                        updateUI();
                    }
                });

                //Negative response
                builder.setNegativeButton("Cancel", null);

                builder.create().show();

                return true;

            case "Delete":
                Log.d("ContextMenu", "item " + indexOfListItemSelected + " item is deleted");
                String sqlDelete = String.format("DELETE FROM %s where %s = %s",
                        DbContract.TABLE,
                        DbContract.Columns._ID,
                        indexOfListItemSelected);


                sqlDB.execSQL(sqlDelete);
                updateUI();


                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.addQuote:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.addQuoteTitle);
                builder.setMessage(R.string.quoteMessage);
                final EditText inputField = new EditText(this);
                builder.setView(inputField);

                //Positive response
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String quote = inputField.getText().toString();
                        Log.d("MainActivity", quote);

                        DBHelper helper = new DBHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(DbContract.Columns.QUOTE, quote);
                        db.insertWithOnConflict(DbContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                        updateUI();
                    }
                });

                //Negative response
                builder.setNegativeButton("Later", null);

                //create the builder
                builder.create().show();
                Log.d("MainActivity", "New Quoted Added");
                return true;
            case R.id.startScheduler:
                startScheduler(3);
                return true;
            case R.id.stopScheduler:
                cancelScheduler();
                return true;
            case R.id.setInterval:
                AlertDialog.Builder builderSetIntervel = new AlertDialog.Builder(this);
                builderSetIntervel.setTitle(R.string.setIntervelTitle);
                builderSetIntervel.setMessage(R.string.setInterval);
                final EditText intervalHour = new EditText(this);

                intervalHour.setInputType(InputType.TYPE_CLASS_NUMBER);
                intervalHour.setHint("Select a Value: 1 - 24");

                builderSetIntervel.setView(intervalHour);


                //Positive response
                builderSetIntervel.setPositiveButton("Set Interval", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int interval = 0;
                        try {
                            interval = Integer.parseInt(intervalHour.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }


                        try {
                            String checkInputIntervalHours = intervalHour.getText().toString();
                            if (checkInputIntervalHours.length() == 0) {
                                return;
                            }
                        } catch (NumberFormatException e) {
                            Log.d("Intervel Input", "Empty Intervel Input");
                        }
                        Log.d("MainActivity", "Interval: " + interval);

                        startScheduler(interval);
                    }
                });

                //Negative response
                builderSetIntervel.setNegativeButton("Later", null);

                //create the builder
                builderSetIntervel.create().show();
                Log.d("MainActivity", "New Quoted Added");
                return true;
            default:
                return false;
        }
    }
}
