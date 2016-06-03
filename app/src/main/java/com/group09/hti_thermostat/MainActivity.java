package com.group09.hti_thermostat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // This can be requested to get *all* of the information stored in the web API.
    public static final String API_BASE_URL = "http://wwwis.win.tue.nl/2id40-ws/9";
    public static final String API_BACKUP_BASE_URL = "http://pcwin889.win.tue.nl/2id40-ws/9";

    public static double CurrentTemperature; // These 4 may not be used, not sure yet.
    public static double DayTemperature;
    public static double NightTemperature;
    public static ArrayList WeekProgram;
    public static RequestQueue reqQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This queue makes the request
        reqQueue = Volley.newRequestQueue(this);
        // StringRequest holds the initial request data
        StringRequest strReq = new StringRequest(Request.Method.GET, API_BASE_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String httpResponse){
                XmlHandler.parseEntireDataset(httpResponse);
                buildAlert(ThermostatData.time, ThermostatData.current_day);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                buildAlert("Volley Error", "There was an error retrieving data from the web API. Please ensure the API is online and try again.");
            }
        });
        reqQueue.add(strReq); // queue the request to be made.
    }


    // This can only be used in non-static contexts. It will crash the app if you manage to get a static-context call to this to compile.
    private void buildAlert(String title, String msg){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .show();
    }

}
