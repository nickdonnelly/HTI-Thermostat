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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This queue makes the request
        RequestQueue initialQueue = Volley.newRequestQueue(this);
        // stringrequest holds the initial request data
        StringRequest strReq = new StringRequest(Request.Method.GET, API_BACKUP_BASE_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String httpResponse){
                parseEntireDataset(httpResponse);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){
                //TODO: Make an error popup
            }
        });
        initialQueue.add(strReq); // queue the request to be made.
    }

    private void parseEntireDataset(String response){
        //TODO: Parse the XML response, check for error, then change the
        //      view to reflect whats on the server.

        // First we need an XML parser, android has these built in. We use
        // a try-catch because this way we don't have to throw exceptions at
        // the function level.
        try{
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactory.newPullParser();

            parser.setInput(stringToInputStream(response), null);
            WeekProgram = new ArrayList();
            // Now we loop until we reach the end of the XML
            try{
                parser.nextTag(); // This reads off the "thermostat" surrounding tag.
                while(parser.next() != XmlPullParser.END_TAG){
                    if(parser.getEventType() != XmlPullParser.START_TAG){
                        continue;
                    }
                    String tagName = parser.getName();
                    // Send it to ThermostatData if it doesn't have a week program related tag
                    if(!tagName.equals("week_program") && !tagName.equals("day")){
                        ThermostatData.readThermostatData(parser, tagName);
                    }
                }
            }catch (IOException e){
                // TODO: Handle exception
            }


        }catch (XmlPullParserException e){
            // TODO: Handle PullParserException
            Log.e("Error: ", e.toString()); // log it
        }
        Log.i("XMLPARSE", "Done parsing!");
        new AlertDialog.Builder(this)
                .setTitle(ThermostatData.time)
                .setMessage(ThermostatData.current_day)
                .show();

    }



    // Simple method to simplify converting the string returned in HTTP responses
    // to InputStreams for use in XML parsing.
    public static InputStream stringToInputStream(String str){
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        return stream;
    }
}
