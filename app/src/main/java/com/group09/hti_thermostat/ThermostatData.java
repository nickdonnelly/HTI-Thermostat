package com.group09.hti_thermostat;

import android.app.AlertDialog;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static boolean week_program_state = false;

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
