package com.group09.hti_thermostat;

import android.app.AlertDialog;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by nick on 01-Jun-16.
 */
public class ThermostatData {
    public static String current_day;
    public static String time;
    public static String current_temperature;
    public static String target_temperature;
    public static String day_temperature;
    public static String night_temperature;


    // Messy, but oh well.
    public static final String days[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public static String[][] switches;
    public static String[][] switch_types;
    public static boolean[][] switch_states;
    public static boolean week_program_state = false;

    public static void readWeekProgram(XmlPullParser parser, String type) throws IOException, XmlPullParserException{
        /*
        Not sure if this will actually need to be used or not.
         */
//        String text = "";
//        parser.require(XmlPullParser.START_TAG, null, type);
//        Log.d("Parser text", String.valueOf(parser.getName()));
//        Log.d("attribute state", String.valueOf(parser.getAttributeValue(null, "state")));
//        if(parser.next() == XmlPullParser.TEXT){
//            parser.nextTag();
//        }
    }

    public static void readThermostatDay(XmlPullParser parser, String type) throws IOException, XmlPullParserException{
        Log.d("READTHERM", "readThermostatDay Called");
        switches = new String[7][10];
        switch_types = new String[7][10];
        for(String[] row : switches){
            Arrays.fill(row, "00:00"); // fill everything with default "00:00"
        }
        switch_states = new boolean[7][10]; // these automatically default to false.
        for(int k = 0; k < 7; k++){
            int day_id = GeneralHelper.getDayIdFromString(parser.getAttributeValue(null, "name"));
            parser.nextTag(); // move to first switch.
            for(int i = 0; i < 10; i++){
                boolean switch_state = GeneralHelper.onOffTextToBool(String.valueOf(parser.getAttributeValue(null, "state")));
                String switch_type = parser.getAttributeValue(null, "type");
                parser.next(); // Move to the text.
                String switch_time = parser.getText();
                while(parser.getEventType() != XmlPullParser.START_TAG){
                    if(parser.getEventType() == XmlPullParser.END_DOCUMENT){
                        break;
                    }
                    parser.next();
                }
                // Save this data
                switches[day_id][i] = switch_time;
                switch_states[day_id][i] = switch_state;
                switch_types[day_id][i] = switch_type;
            }
        }
        Log.d("READTHERM", "WE DONE");
    }

    public static void readThermostatData(XmlPullParser parser, String type) throws IOException, XmlPullParserException{
        String text = "";
        parser.require(XmlPullParser.START_TAG, null, type);
        if(parser.next() == XmlPullParser.TEXT){
            text = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, type);
        switch(type){
            case "current_day":
                current_day = text;
                break;
            case "time":
                time = text;
                break;
            case "current_temperature":
                current_temperature = text;
                break;
            case "target_temperature":
                target_temperature = text;
                break;
            case "day_temperature":
                day_temperature = text;
                break;
            case "night_temperature":
                night_temperature = text;
                break;
            case "week_program_state":
                week_program_state = GeneralHelper.onOffTextToBool(text);
                break;
        }
    }

    public static void putData(final String attribute, final String data){
        new Thread(new Runnable(){
            public void run(){
                try{
                    String xmlStr = XmlHandler.getXMLString(attribute, data);
                    URL link = new URL(MainActivity.API_BASE_URL + "/" + GeneralHelper.getSubUrlName(attribute));
                    HttpURLConnection request = (HttpURLConnection) link.openConnection();
                    request.setReadTimeout(10000); // 10 seconds of timeout.
                    request.setConnectTimeout(10000);
                    request.setRequestProperty("Content-Type", "application/xml"); // you get HTTP 414 if you don't do this.
                    request.setRequestMethod("PUT"); // PUTting data, not asking for response
                    request.setDoInput(false);
                    request.setDoOutput(true);
                    request.connect();
                    DataOutputStream putData = new DataOutputStream(request.getOutputStream());
                    putData.writeBytes(xmlStr);
                    putData.flush();
                    int code = request.getResponseCode();
                    Log.i("API Responded", Integer.toString(code));
                }catch(Exception e){ // MalformedURLException
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
