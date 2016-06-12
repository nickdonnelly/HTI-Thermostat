package com.group09.hti_thermostat;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

public class WeekProgramActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener {

    private int DAY_ID = 0; // Start it on monday
    private String DAY_NAME = "Monday";

    // UI Elements
    public static ScrollView svSingleDay;
    public static TabLayout tlDays;
    public static ViewPager vpSchedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_program);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        svSingleDay = (ScrollView) findViewById(R.id.svSingleDaySchedule);
        vpSchedule = (ViewPager) findViewById(R.id.vpSchedule);
        vpSchedule.setAdapter(new DayPagerFragmentAdapter(getSupportFragmentManager(), WeekProgramActivity.this));
        tlDays = (TabLayout) findViewById(R.id.tabLayoutWeek);
        tlDays.setupWithViewPager(vpSchedule);
//        tlDays.setOnTabSelectedListener(this);
        tlDays.setTabTextColors(Color.LTGRAY, Color.WHITE);

//        tlDays.setupWithViewPager();
        fab.setOnClickListener(this);
        //@Override
        //public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
        //  }
        //});
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Week Program: " + DAY_NAME);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        this.DAY_NAME = String.valueOf(tab.getText());
        this.DAY_ID = GeneralHelper.getDayIdFromString(this.DAY_NAME);
        getSupportActionBar().setTitle("Week Program: " + this.DAY_NAME); // update title
        Log.d("TAB SELECTED", this.DAY_NAME);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                Log.d("CLICK EVENT", "Floating action button");
                break;
        }
    }
}
