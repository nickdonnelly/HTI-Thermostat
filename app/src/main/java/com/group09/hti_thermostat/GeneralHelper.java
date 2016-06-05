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
}
