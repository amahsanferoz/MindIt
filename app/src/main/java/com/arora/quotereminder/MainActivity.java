package com.arora.quotereminder;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.arora.quotereminder.db.DBHelper;
import com.arora.quotereminder.db.DbContract;

public class MainActivity extends AppCompatActivity {

    private DBHelper helper;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.list);
        /*SQLiteDatabase sqlDB = new DBHelper(this).getWritableDatabase();
        Cursor cursor = sqlDB.query(DbContract.TABLE,
                new String[]{DbContract.Columns._ID, DbContract.Columns.QUOTE, DbContract.Columns.VALIDITY, DbContract.Columns.COUNTER},
                null, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.moveToNext()) {
            Log.d("MainActivity Cursor",
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Columns.QUOTE)));
        }*/

        updateUI();
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
                new int[] {R.id.quote},
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
                Log.d("ContextMenu", "item "+ info.toString() + " item is edited");

                String sqlSelect = String.format("SELECT * FROM %s WHERE %s = %s",
                        DbContract.TABLE,
                        DbContract.Columns._ID,
                        indexOfListItemSelected);

                Cursor cursor = sqlDB.rawQuery(sqlSelect, null);
                if(cursor.moveToFirst()) {
                    str = cursor.getString(1);
                    Log.d("EDIT quote", str + " is the value");
                }

                Log.d("Edit", "item "+ str);
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
                Log.d("ContextMenu", "item "+ indexOfListItemSelected + " item is deleted");
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
            case R.id.action_settings:
                return true;
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
            default:
                return false;
        }
    }
}
