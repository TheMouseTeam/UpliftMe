package com.barmpas.upliftme;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.barmpas.upliftme.UserActionData.UserContract;

/**
 * The HistoryActivity to display the days that actions have been taken.
 * @author Konstantinos Barmpas.
 */
public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * URL loader
     */
    private static final int URL_LOADER = 0;
    /**
     * SimpleCursor Adapter
     */
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //The displayed views
        String[] columns = {
                UserContract.UserEntry.COLUMN_DATE
        };

        int[] views = {
                R.id.action_saved
        };

        //Setting the adapter
        mAdapter = new SimpleCursorAdapter(this, R.layout.savedaction_card, null, columns, views, 0) {

            @Override
            public void bindView(View view, Context context, final Cursor cursor) {
                super.bindView(view, context, cursor);

                //Setting each individual view of the adapter.
                TextView date_txt = (TextView) view.findViewById(R.id.action_saved);
                final String date = cursor.getString(cursor.getColumnIndex("date"));
                date_txt.setText(date);
                final String day_actions = cursor.getString(cursor.getColumnIndex("actions"));
                final int day_goal = cursor.getInt(cursor.getColumnIndex("goal"));
                final int day_points = cursor.getInt(cursor.getColumnIndex("points"));

                //On click pass data to the Graph activity
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), DayActivity.class);
                        intent.putExtra("date",date);
                        intent.putExtra("points",day_points);
                        intent.putExtra("goal",day_goal);
                        intent.putExtra("actions",day_actions);
                        startActivity(intent);
                    }
                });
            }
        };

        //Activating the cursor and the adapter
        ListView favListView = (ListView) findViewById(R.id.saved_recycler_view);
        favListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }


    //Menus to allow delete the SQlite database.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.delete))
                        .setMessage(getResources().getString(R.string.delete_all_days))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                getContentResolver().delete(UserContract.UserEntry.CONTENT_URI, null, null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Creating the Cursor
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {

        switch (loaderID) {
            case URL_LOADER:
                String[] projection = {
                        UserContract.UserEntry._ID,
                        UserContract.UserEntry.COLUMN_DATE,
                        UserContract.UserEntry.COLUMN_ACTIONS,
                        UserContract.UserEntry.COLUMN_GOAL,
                        UserContract.UserEntry.COLUMN_POINTS
                };
                return new CursorLoader(
                        this,
                        UserContract.UserEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
