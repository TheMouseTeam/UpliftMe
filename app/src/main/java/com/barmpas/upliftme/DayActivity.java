package com.barmpas.upliftme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

/**
 * The DayActivity to show the actions taken in a specific day
 * @author Konstantinos Barmpas.
 */
public class DayActivity extends AppCompatActivity implements ActionAdapter.ActionAdapterOnClickHandler {

    /**
     * RecyclerView
     */
    private RecyclerView mRecyclerView;
    /**
     * Date
     */
    private String formattedDate;
    /**
     * Integers for circular UI
     */
    private int  series1Index,backIndex;
    /**
     * DecoView for circular UI
     */
    private DecoView decoView;
    /**
     * textViews to display day and points collected that day
     */
    private TextView mPoints,mDay;
    /**
     * Actions string
     */
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savedday_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        //Set up UI
        mPoints=(TextView)findViewById(R.id.saved_day_points);
        data = intent.getStringExtra("actions");
        mDay=(TextView)findViewById(R.id.saved_day_day);
        formattedDate=intent.getStringExtra("date");
        mDay.setText(formattedDate);

        Circle(intent.getIntExtra("points",0),intent.getIntExtra("goal",50));

        mRecyclerView=(RecyclerView) findViewById(R.id.savedday_actions_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        ActionAdapter mActionAdapter = new ActionAdapter(this);

        mRecyclerView.setAdapter(mActionAdapter);

        String[] actions = data.split("#");
        if (!actions[0].equals("")) {
            mActionAdapter.setData(actions);
        }
    }

    public void Circle(int today,int goal){
        decoView = (DecoView) findViewById(R.id.dynamicArcView);
        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, 50, 0)
                .build();

        backIndex = decoView.addSeries(seriesItem1);
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FDD835"))
                .setRange(0, 50, 0)
                .build();
        series1Index = decoView.addSeries(seriesItem);
        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
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
        int percentage=(today * 50)/goal;
        decoView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .build());
        decoView.addEvent(new DecoEvent.Builder(percentage)
                .setIndex(series1Index)
                .setDelay(3000)
                .build());
        mPoints.setText(getResources().getString(R.string.points)+" "+ today +" / "+ goal);
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

    @Override
    public void onClick(String dataForThis) {

    }
}
