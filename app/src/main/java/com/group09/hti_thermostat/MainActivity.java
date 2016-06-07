package com.group09.hti_thermostat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.SeekBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, NumberPicker.OnValueChangeListener {

    // This can be requested to get *all* of the information stored in the web API.
    public static final String API_BASE_URL = "http://wwwis.win.tue.nl/2id40-ws/9";
    public static final String API_BACKUP_BASE_URL = "http://pcwin889.win.tue.nl/2id40-ws/9";
    public static final int REFRESH_DELAY = 1000; // 5000 milliseconds, refresh the data from api every 5 seconds.
    public static final double MIN_TEMPERATURE = 5.0;
    public static final double MAX_TEMPERATURE = 30.0;
    public static final double STEP = 0.5;
    public static double SeekBarValue;


    public static double CurrentTemperature;
    public static double TargetTemperature;
    public static double DayTemperature;
    public static double NightTemperature;
    public static ArrayList WeekProgram;
    public static RequestQueue reqQueue;


    // UI ELEMENTS
    public static TextView tvTemperature;
    public static TextView tvCurrentTemperature;
    public static TextView tvTimeDate;
    public static TextView tvDayTemperature;
    public static TextView tvNightTemperature;
    public static Button btnMinus;
    public static Button btnPlus;
    public static Button btnWeeklyProgram;
    public static Button btnEditDay;
    public static Button btnEditNight;
    public static Switch switchWeeklyProgram;
    public static SeekBar sbTemperature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvTimeDate = (TextView) findViewById(R.id.tvTimeDate);
        tvDayTemperature = (TextView) findViewById(R.id.tvDayTemp);
        tvNightTemperature = (TextView) findViewById(R.id.tvNightTemp);
        tvCurrentTemperature = (TextView) findViewById(R.id.tvCurrentTemperature);
        btnMinus = (Button) findViewById(R.id.btnSub);
        btnPlus = (Button) findViewById(R.id.btnAdd);
        btnWeeklyProgram = (Button) findViewById(R.id.btnEditWeeklyProgram);
        btnEditDay = (Button) findViewById(R.id.btnEditDayTemp);
        btnEditNight = (Button) findViewById(R.id.btnEditNightTemp);
        switchWeeklyProgram = (Switch) findViewById(R.id.switchWeeklyEnabled);

        switchWeeklyProgram.setChecked(ThermostatData.week_program_state);
        sbTemperature = (SeekBar) findViewById(R.id.sbTemperature);
        sbTemperature.setMax((int)((MAX_TEMPERATURE - MIN_TEMPERATURE) / STEP)); // 50. This is because, for whatever reason, you can't set the min.
        sbTemperature.setOnSeekBarChangeListener(this);
        if(switchWeeklyProgram.isChecked()) {
            btnWeeklyProgram.setEnabled(true);
            btnWeeklyProgram.setAlpha(1.0f);
        }else {
            btnWeeklyProgram.setEnabled(false);
            btnWeeklyProgram.setAlpha(0.3f);
        }
        btnMinus.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnEditDay.setOnClickListener(this);
        btnEditNight.setOnClickListener(this);
        switchWeeklyProgram.setOnClickListener(this);
        Log.d("test", "test123");
        // This queue makes the request
        reqQueue = Volley.newRequestQueue(this);
        // StringRequest holds the initial request data
        this.RefreshData();

        final Handler refreshHandler = new Handler();
        refreshHandler.postDelayed(new Runnable(){
            public void run(){
                RefreshData();
                refreshHandler.postDelayed(this, REFRESH_DELAY); // run forever every 5 seconds.
            }
        }, REFRESH_DELAY);
    }


    // This can only be used in non-static contexts. It will crash the app if you manage to get a static-context call to this to compile.
    // Pretty much only use this for debugging, it doesn't create confirm/exit buttons.
    private void buildAlert(String title, String msg){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .show();
    }

    private void buildAlertWithOK(String title, String msg){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setNeutralButton("OK", null)
                .show();
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.btnAdd:
                incrementTemperature();
                break;
            case R.id.btnSub:
                decrementTemperature();
                break;
            case R.id.btnEditDayTemp:
                showDayDialog();
                break;
            case R.id.btnEditNightTemp:
                showNightDialog();
                break;
            case R.id.switchWeeklyEnabled:
                if(switchWeeklyProgram.isChecked()) {
                    btnWeeklyProgram.setEnabled(true);
                    btnWeeklyProgram.setAlpha(1.0f);
                    ThermostatData.putData("week_program_state", "on");
                }else {
                    btnWeeklyProgram.setEnabled(false);
                    btnWeeklyProgram.setAlpha(0.3f);
                    ThermostatData.putData("week_program_state", "off");
                }
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int prevVal, int newVal){
        switch(picker.getId()){
            case R.id.npPrimary:
                if(newVal == 30){

                }else if(prevVal == 30){

                }
                break;
            case R.id.npDecimal:
                break;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.TargetTemperature = SeekBarValue;
        ThermostatData.putData("target_temperature", String.valueOf(TargetTemperature));
        this.RefreshData();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        SeekBarValue = MIN_TEMPERATURE + (progress * STEP);
        tvTemperature.setText(Double.toString(SeekBarValue) + "C");
    }


    private void incrementTemperature(){
        if(TargetTemperature < 30.0){
            TargetTemperature += 0.1;
            ThermostatData.putData("target_temperature", String.valueOf(TargetTemperature)); // the valueof bit finalizes the variable so it can be used from a separate thread.
            this.RefreshData();
        }else{
            buildAlertWithOK("Temperature Error", "You can't increase the temperature past 30.0C.");
        }
    }

    private void decrementTemperature(){
        if(TargetTemperature > 5.0){
            TargetTemperature -= 0.1;
            ThermostatData.putData("target_temperature", String.valueOf(TargetTemperature));
            this.RefreshData();
        }else{
            buildAlertWithOK("Temperature Error", "You can't decrease the temperature below 5.0C.");
        }
    }

    private void RefreshData(){
        StringRequest strReq = new StringRequest(Request.Method.GET, API_BASE_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String httpResponse){
                XmlHandler.parseEntireDataset(httpResponse);
                CurrentTemperature = Double.parseDouble(ThermostatData.current_temperature);
                TargetTemperature = Double.parseDouble(ThermostatData.target_temperature);
                DayTemperature = Double.parseDouble(ThermostatData.day_temperature);
                NightTemperature = Double.parseDouble(ThermostatData.night_temperature);
                tvTemperature.setText(TargetTemperature + "C");
                tvCurrentTemperature.setText("Current Temperature: " + CurrentTemperature + "C");
                switchWeeklyProgram.setChecked(ThermostatData.week_program_state);
                tvTimeDate.setText(ThermostatData.current_day + " " + ThermostatData.time);
                tvDayTemperature.setText(ThermostatData.day_temperature + "C");
                tvNightTemperature.setText(ThermostatData.night_temperature + "C");
                int correctedTemperature = (int)((TargetTemperature - MIN_TEMPERATURE)/STEP);
                if(TargetTemperature % 0.5 == 0){
                    sbTemperature.setProgress(correctedTemperature); // convert back.
                }
                if(switchWeeklyProgram.isChecked()) {
                    btnWeeklyProgram.setEnabled(true);
                    btnWeeklyProgram.setAlpha(1.0f);
                }else {
                    btnWeeklyProgram.setEnabled(false);
                    btnWeeklyProgram.setAlpha(0.3f);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                buildAlert("Volley Error", "There was an error retrieving data from the web API. Please ensure the API is online and try again.");
            }
        });
        reqQueue.add(strReq);
    }


    private void showDayDialog(){
        final Dialog d = new Dialog(this);
        d.setTitle("Select Day Temperature");
        d.setContentView(R.layout.npdialog);
        Button okButton = (Button) d.findViewById(R.id.btnTempSelectOK);
        final NumberPicker npPrimary = (NumberPicker) d.findViewById(R.id.npPrimary);
        final NumberPicker npDecimal = (NumberPicker) d.findViewById(R.id.npDecimal);
        npPrimary.setMinValue(5);
        npPrimary.setMaxValue(30);
        npDecimal.setMinValue(0);
        npDecimal.setMaxValue(9);

        npPrimary.setValue((int)Math.floor(this.DayTemperature));
        int decVal = (int) ((DayTemperature - Math.floor(DayTemperature)) * 10);
        npDecimal.setValue(decVal);

        npPrimary.setOnValueChangedListener(this);
        npDecimal.setOnValueChangedListener(this);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Can't go above 30.0C
                if(npPrimary.getValue() == 30){
                    ThermostatData.putData("day_temperature", String.valueOf(npPrimary.getValue()) + ".0");
                }else{
                    ThermostatData.putData("day_temperature", String.valueOf(npPrimary.getValue()) + "." + String.valueOf(npDecimal.getValue()));
                }
                d.dismiss();
            }
        });
        d.show();
    }

    private void showNightDialog(){
        final Dialog d = new Dialog(this);
        d.setTitle("Select Night Temperature");
        d.setContentView(R.layout.npdialog);
        Button okButton = (Button) d.findViewById(R.id.btnTempSelectOK);
        final NumberPicker npPrimary = (NumberPicker) d.findViewById(R.id.npPrimary);
        final NumberPicker npDecimal = (NumberPicker) d.findViewById(R.id.npDecimal);
        npPrimary.setMinValue(5);
        npPrimary.setMaxValue(30);
        npDecimal.setMinValue(0);
        npDecimal.setMaxValue(9);

        npPrimary.setOnValueChangedListener(this);
        npDecimal.setOnValueChangedListener(this);

        npPrimary.setValue((int)Math.floor(NightTemperature));
        int decVal = (int) ((NightTemperature - Math.floor(NightTemperature)) * 10);
        npDecimal.setValue(decVal);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(npPrimary.getValue() == 30){
                    ThermostatData.putData("night_temperature", String.valueOf(npPrimary.getValue()) + ".0");
                }else{
                    ThermostatData.putData("night_temperature", String.valueOf(npPrimary.getValue()) + "." + String.valueOf(npDecimal.getValue()));
                }
                d.dismiss();
            }
        });
        d.show();
    }

}
