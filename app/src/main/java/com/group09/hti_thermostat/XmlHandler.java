package com.group09.hti_thermostat;

import android.app.AlertDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nick on 03-Jun-16.
 */
public class XmlHandler {

    public static void parseEntireDataset(String response){
        // First we need an XML parser, android has these built in. We use
        // a try-catch because this way we don't have to throw exceptions at
        // the function level.
        try{
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactory.newPullParser();

            parser.setInput(GeneralHelper.stringToInputStream(response), null);
            // Now we loop until we reach the end of the XML
            try{
                parser.nextTag(); // This reads off the "thermostat" surrounding tag.
                while(parser.next() != XmlPullParser.END_TAG){
                    if(parser.getEventType() != XmlPullParser.START_TAG){
                        continue;
                    }
                    String tagName = parser.getName();
                    // Send it to ThermostatData for individual parsing.
                    if(tagName.contains("week_program") && !tagName.equals("week_program_state")){
                        ThermostatData.readWeekProgram(parser, tagName);
                        break;
                    }else if(tagName.equals("day")){
                        ThermostatData.readThermostatDay(parser, tagName);
                    }else{
                        ThermostatData.readThermostatData(parser, tagName);
                    }
                }
            }catch (IOException e){
                // TODO: Handle exception
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
            Log.e("Error", e.toString()); // log it
        }
        Log.i("XMLPARSE", "Done parsing!");
    }

    public static String getXMLString(String tagname, String value){
        return "<" + tagname + ">" + value + "</" + tagname + ">";
    }

    public static void putWeekProgram(){
        // TODO
    }

}
