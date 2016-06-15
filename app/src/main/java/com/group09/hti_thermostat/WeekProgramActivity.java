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

import java.util.Arrays;

public class WeekProgramActivity extends AppCompatActivity {

    private int DAY_ID = 0; // Start it on monday

    // UI Elements
    public static ScrollView svSingleDay;
    public static TabLayout tlDays;
    public static ViewPager vpSchedule;

    public static String[][] cp_switches;
    public static String[][] cp_switch_types;
    public static boolean[][] cp_switch_states;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_program);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        vpSchedule = (ViewPager) findViewById(R.id.vpSchedule);
        vpSchedule.setAdapter(new DayPagerFragmentAdapter(getSupportFragmentManager(), WeekProgramActivity.this));
        tlDays = (TabLayout) findViewById(R.id.tabLayoutWeek);
        tlDays.setupWithViewPager(vpSchedule);
        tlDays.setTabTextColors(Color.LTGRAY, Color.WHITE);
//                  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

        cp_switches = new String[7][10];
        cp_switch_types = new String[7][10];
        cp_switch_states = new boolean[7][10];

        for(int i = 0; i < 7; i++){

            // Copy these from the data so they don't get overwritten every time the API refreshes.
            cp_switches[i] = Arrays.copyOf(ThermostatData.switches[i], 10);
            cp_switch_types[i] = Arrays.copyOf(ThermostatData.switch_types[i], 10);
            cp_switch_states[i] = Arrays.copyOf(ThermostatData.switch_states[i], 10);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Week Program");
    }

}
