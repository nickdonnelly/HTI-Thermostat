package com.group09.hti_thermostat;

import android.app.AlertDialog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

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

    // "on" or "off". Why they didn't just use a bool in the xml? Who fucking knows.
    public static String week_program_state;

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
            case "time":
                time = text;
            case "current_temperature":
                current_temperature = text;
            case "target_temperature":
                target_temperature = text;
            case "day_temperature":
                day_temperature = text;
            case "night_temperature":
                night_temperature = text;
            case "week_program_state":
                week_program_state = text;
        }
    }
}
