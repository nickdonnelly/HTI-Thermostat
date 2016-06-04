package com.group09.hti_thermostat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // This can be requested to get *all* of the information stored in the web API.
    public static final String API_BASE_URL = "http://wwwis.win.tue.nl/2id40-ws/9";
    public static final String API_BACKUP_BASE_URL = "http://pcwin889.win.tue.nl/2id40-ws/9";

    public static double CurrentTemperature;
    public static double TargetTemperature;
    public static double DayTemperature;
    public static double NightTemperature;
    public static ArrayList WeekProgram;
    public static RequestQueue reqQueue;


    // UI ELEMENTS
    public static TextView tvTemperature;
    public static TextView tvCurrentTemperature;
    public static Button btnMinus;
    public static Button btnPlus;
    public static Button btnWeeklyProgram;
    public static Switch switchWeeklyProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvCurrentTemperature = (TextView) findViewById(R.id.tvCurrentTemperature);
        btnMinus = (Button) findViewById(R.id.btnSub);
        btnPlus = (Button) findViewById(R.id.btnAdd);
        btnWeeklyProgram = (Button) findViewById(R.id.btnEditWeeklyProgram);
        switchWeeklyProgram = (Switch) findViewById(R.id.switchWeeklyEnabled);
        switchWeeklyProgram.setChecked(ThermostatData.week_program_state);
        if(switchWeeklyProgram.isChecked()) {
            btnWeeklyProgram.setEnabled(true);
            btnWeeklyProgram.setAlpha(1.0f);
        }else {
            btnWeeklyProgram.setEnabled(false);
            btnWeeklyProgram.setAlpha(0.3f);
        }
        btnMinus.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        switchWeeklyProgram.setOnClickListener(this);
        // This queue makes the request
        reqQueue = Volley.newRequestQueue(this);
        // StringRequest holds the initial request data
        this.RefreshData();
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
            case R.id.switchWeeklyEnabled:
                if(switchWeeklyProgram.isChecked()) {
                    btnWeeklyProgram.setEnabled(true);
                    btnWeeklyProgram.setAlpha(1.0f);
                }else {
                    btnWeeklyProgram.setEnabled(false);
                    btnWeeklyProgram.setAlpha(0.3f);
                }
                break;
        }
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
                tvTemperature.setText(TargetTemperature + "C");
                tvCurrentTemperature.setText("Current Temperature: " + CurrentTemperature + "C");
                switchWeeklyProgram.setChecked(ThermostatData.week_program_state);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                buildAlert("Volley Error", "There was an error retrieving data from the web API. Please ensure the API is online and try again.");
            }
        });
        reqQueue.add(strReq);
    }

}
