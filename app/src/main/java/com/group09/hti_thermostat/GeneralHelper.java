package com.group09.hti_thermostat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by nick on 03-Jun-16.
 */
public class GeneralHelper {

    // Simple method to simplify converting the string returned in HTTP responses
    // to InputStreams for use in XML parsing.
    public static InputStream stringToInputStream(String str){
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        return stream;
    }

    public static boolean onOffTextToBool(String text){
        text = text.trim();
        return text.equals("on");
    }

    public static String boolToOnOffText(boolean state){
        return state ? "on" : "off";
    }

    public static int getDayIdFromString(String dayName) {
        if(dayName == null) return 0;
        switch (dayName) {
            case "Monday":
                return 0;
            case "Tuesday":
                return 1;
            case "Wednesday":
                return 2;
            case "Thursday":
                return 3;
            case "Friday":
                return 4;
            case "Saturday":
                return 5;
            case "Sunday":
                return 6;
        }
        return 0; // this shouldn't ever be hit.
    }

    public static String getSubUrlName(String attribute){
        switch(attribute){
            case "current_day":
                return "currentDay";
            case "target_temperature":
                return "targetTemperature";
            case "day_temperature":
                return "dayTemperature";
            case "night_temperature":
                return "nightTemperature";
            case "week_program_state":
                return "weekProgramState";
            default:
                return "";
        }
    }

    public static String correctTime(String time){
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        String strHour = String.valueOf(hour);
        String strMin = String.valueOf(minute);
        if(hour < 10){
            strHour = "0" + String.valueOf(hour);
        }
        if(minute < 10){
            strMin = "0" + String.valueOf(minute);
        }
        return strHour + ":" + strMin;
    }
}
