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
}
