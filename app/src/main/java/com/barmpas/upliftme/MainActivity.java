package com.barmpas.upliftme;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.barmpas.upliftme.UserActionData.UserContract;
import com.barmpas.upliftme.PopUpWindow.SharePopup;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.barmpas.upliftme.UserActionData.UserProvider.getDate;
import static com.barmpas.upliftme.UserActionData.UserProvider.getPointsToday;

/**
 * The MainActivity shows today's points and the user can access the 4 categories.
 * @author Konstantinos Barmpas.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    protected static String category_chosen_points;
    private TextView textOverall;
    private static float percentage;
    private Button button_do,button_feel,button_nutrition,button_connect;
    public static int goal,today;
    private int  series1Index,backIndex;
    private DecoView decoView;
    private String formattedDate;
    private TextView textPercentage;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textOverall= (TextView) findViewById(R.id.overall_Points);
        button_do=(Button) findViewById(R.id.button_Do);
        button_feel=(Button) findViewById(R.id.button_Feel);
        button_connect=(Button) findViewById(R.id.button_Connect);
        button_nutrition=(Button) findViewById(R.id.button_Nutrition);

        button_do.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                category_chosen_points="Do";
                Intent intent = new Intent(view.getContext(), PointsActivity.class);
                startActivity(intent);
            }
        });

        button_feel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                category_chosen_points="Feel";
                Intent intent = new Intent(view.getContext(), PointsActivity.class);
                startActivity(intent);
            }
        });

        button_connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                category_chosen_points="Connect";
                Intent intent = new Intent(view.getContext(), PointsActivity.class);
                startActivity(intent);
            }
        });

        button_nutrition.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                category_chosen_points="Nutrition";
                Intent intent = new Intent(view.getContext(), PointsActivity.class);
                startActivity(intent);
            }
        });

        textPercentage = (TextView) findViewById(R.id.textPercentage);
        percentage=0;
        textPercentage.setText("0 %");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());
        //Get api_key from preferences
        prefs = this.getSharedPreferences(
                "com.barmpas.upliftme", MODE_PRIVATE);
        goal = prefs.getInt("Goal", 50);
        updateGoal();
        createNewField();
    }

    /**
     * Store in the SQLite Database
     */
    public void updateGoal() {
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_GOAL, goal);
        getContentResolver().update(UserContract.UserEntry.CONTENT_URI, values, "date" + " = '" + formattedDate + "'", null);
    }

    /**
     * Store in the SQLite Database
     */
    public void createNewField() {
        String date=getDate(formattedDate);
        if (date.equals("")) {
            //Prepare values for SQL Database
            ContentValues values = new ContentValues();
            values.put(UserContract.UserEntry.COLUMN_DATE, formattedDate);
            values.put(UserContract.UserEntry.COLUMN_ACTIONS, "");
            values.put(UserContract.UserEntry.COLUMN_GOAL, 50);
            values.put(UserContract.UserEntry.COLUMN_POINTS, 0);
            //Add to SQL Database
            Uri newUri = getContentResolver().insert(UserContract.UserEntry.CONTENT_URI, values);
        }
        today = getPointsToday(formattedDate);
        Circle();
    }

    @Override
    public void onResume (){
        super.onResume();
        textPercentage.setText("0 %");
        //Get api_key from preferences
        prefs = this.getSharedPreferences(
                "com.barmpas.upliftme", MODE_PRIVATE);
        goal = prefs.getInt("Goal", 50);
        updateGoal();
        Circle();
    }



    public void Circle(){
        decoView = (DecoView) findViewById(R.id.dynamicArcView);

        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, 50, 0)
                .build();

        backIndex = decoView.addSeries(seriesItem1);

        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FDD835"))
                .setRange(0, 50, 0)
                .build();

        series1Index = decoView.addSeries(seriesItem);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {

            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        percentage=(today * 50)/goal;


        decoView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .build());

        decoView.addEvent(new DecoEvent.Builder(percentage)
                .setIndex(series1Index)
                .setDelay(3000)
                .build());
        textOverall.setText(getResources().getString(R.string.points)+" "+ today+"/"+goal);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.action_goal){
            new SharePopup.Builder(MainActivity.this)
                    .setContentView(R.layout.layout_bottom_popup).bindClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, R.id.cancel_action_goal).bindClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    prefs.edit().putInt("Goal", 10).apply();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, R.id.goal_10).bindClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    prefs.edit().putInt("Goal", 30).apply();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, R.id.goal_30).bindClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    prefs.edit().putInt("Goal", 50).apply();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, R.id.goal_50).bindClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    prefs.edit().putInt("Goal", 80).apply();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, R.id.goal_80).bindClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    prefs.edit().putInt("Goal", 100).apply();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, R.id.goal_100).setGravity(Gravity.BOTTOM).build().show();
        }
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today) {
            // Handle the camera action
        } else if (id == R.id.nav_previous_days) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_info) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themouseteam.com")));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
