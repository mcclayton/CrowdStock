package com.crowdstock.app.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Michael Clayton on 9/28/14.
 *
 * Utility class for asynchronous HTTP requests.
 * Invoke a request with 'new AsyncHttp().execute("http://date.jsontest.com/");'
 */
public class HttpRequest {
    private static final String ERROR_TAG = "HttpRequest";

    /*
	 * Send a GET request to {@endpoint}
	 *
	 * @param endpoint The endpoint to send the GET request to.
	 */
    public static String doGetRequest(String endpoint) {
        String result;
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            // Send a GET request
            try {
                // Send data
                URL url = new URL(endpoint);
                URLConnection conn = url.openConnection();

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null)
                {
                    sb.append(line);
                }
                rd.close();
                result = sb.toString();
            } catch (Exception e) {
                // Log the error and return null
                Log.e(ERROR_TAG, "Error occurred when performing asynchronous HTTP request.", e);
                return null;
            }
        } else {
            // Log the error and return null
            Log.e(ERROR_TAG, "Request endpoint does not start with http:// or https://.");
            return null;
        }
        return result;
    }
}