package com.barmpas.upliftme;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.barmpas.upliftme.UserActionData.UserContract;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.barmpas.upliftme.UserActionData.UserProvider.getActions;
import static com.barmpas.upliftme.UserActionData.UserProvider.getDate;
import static com.barmpas.upliftme.MainActivity.category_chosen_points;
import static com.barmpas.upliftme.MainActivity.goal;
import static com.barmpas.upliftme.MainActivity.today;

/**
 * The PointsActivity displays the actions for a selected category and the user can add one action.
 * @author Konstantinos Barmpas.
 */
public class PointsActivity extends AppCompatActivity {

    private static TextView pointsTextView;
    private static TextView categoryTextView;
    private static int percentage;
    private DatabaseReference mReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private static DecoView decoView;
    private static int series1Index;
    private static Context mContext;
    private static Activity activity;
    private static ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.points_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;

        contentResolver = getContentResolver();

        mRecyclerView = (RecyclerView) findViewById(R.id.actions_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        pointsTextView=(TextView) findViewById(R.id.textPointsToday);
        categoryTextView=(TextView)findViewById(R.id.pointsCategory);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (Locale.getDefault().getDisplayLanguage().equals("Ελληνικά")) {
            mReference = database.getInstance().getReference(category_chosen_points+"-GR");
        }else{
            mReference = database.getInstance().getReference(category_chosen_points);
        }

        if (today<goal) {
            pointsTextView.setText(getResources().getString(R.string.todays_points)+" "+"\n"+Integer.toString(today));
            categoryTextView.setText(getResources().getString(R.string.remaining)+" "+Integer.toString(goal-today));
        }else{
            pointsTextView.setText(getResources().getString(R.string.reached_goal));
            categoryTextView.setText(getResources().getString(R.string.todays_points)+" "+"\n"+Integer.toString(today)+"/"+Integer.toString(goal));
        }

        decoView = (DecoView) findViewById(R.id.dynamicArcViewPoints);

        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, 50, 0)
                .build();

        int backIndex = decoView.addSeries(seriesItem1);

        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFAB00"))
                .setRange(0, 50, 0)
                .build();

        series1Index = decoView.addSeries(seriesItem);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {

            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        if (goal==0){
            percentage=0;
        }else{
            percentage= today* 50/goal;
        }

        decoView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .build());

        decoView.addEvent(new DecoEvent.Builder(percentage)
                .setIndex(series1Index)
                .setDelay(2000)
                .build());

        FirebasePointsViewHolder.createVector();
        setUpFirebaseAdapter();
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, FirebasePointsViewHolder>
                (String.class, R.layout.action_card, FirebasePointsViewHolder.class,
                        mReference) {
            @Override
            protected void populateViewHolder(FirebasePointsViewHolder viewHolder,
                                              String model, int position) {
                viewHolder.bindData(model);
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    static public void onClickItem(final String dataForThis, View v) {
        new BlurPopupWindow.Builder(v.getContext())
                .setContentView(R.layout.layout_accept)
                .bindClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Check(dataForThis,v);
                        if (today<goal){
                            Intent intent = new Intent(mContext, PointsActivity.class);
                            mContext.startActivity(intent);
                            activity.finish();
                        }
                    }
                }, R.id.dialog_like_bt).bindClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PointsActivity.class);
                mContext.startActivity(intent);
                activity.finish();
            }
        }, R.id.dialog_like_bt_delete)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build()
                .show();
    }

    public static void Check(final String dataForThis, View v){
        String[] seperated = dataForThis.split("#");
        int a= Integer.parseInt(seperated[0]);
        today = today + a;

        if (today<goal) {
            pointsTextView.setText(v.getResources().getString(R.string.todays_points)+" "+"\n"+Integer.toString(today));
            categoryTextView.setText(v.getResources().getString(R.string.remaining)+" "+Integer.toString(goal-today));
        }else{
            pointsTextView.setText(v.getResources().getString(R.string.reached_goal));
            categoryTextView.setText(v.getResources().getString(R.string.todays_points)+" "+"\n"+Integer.toString(today)+"/"+Integer.toString(goal));
        }

        ContentValues values = new ContentValues();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        String date=getDate(formattedDate);
        String actions = getActions(date);
        if (actions.equals("")){
            actions = seperated[1];
        }else{
            actions = actions + "#" + seperated[1];
        }

        if (date.equals("")) {
            values.put(UserContract.UserEntry.COLUMN_GOAL, goal);
            values.put(UserContract.UserEntry.COLUMN_POINTS, today);
            values.put(UserContract.UserEntry.COLUMN_ACTIONS, actions);
            values.put(UserContract.UserEntry.COLUMN_DATE, formattedDate);
            Uri newUri = contentResolver.insert(UserContract.UserEntry.CONTENT_URI, values);
        }else{
            values.put(UserContract.UserEntry.COLUMN_POINTS, today);
            values.put(UserContract.UserEntry.COLUMN_ACTIONS, actions);
            contentResolver.update(UserContract.UserEntry.CONTENT_URI, values, "date" + " = '" + date + "'", null);
        }
        percentage= (today* 50)/goal;
        decoView.addEvent(new DecoEvent.Builder(percentage)
                .setIndex(series1Index)
                .setDelay(0)
                .build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}

