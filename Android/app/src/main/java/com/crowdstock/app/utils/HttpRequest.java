package com.crowdstock.app.utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Michael Clayton on 9/28/14.
 *
 * Utility class for HTTP requests.
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
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                result = sb.toString();
            } catch (Exception e) {
                // Log the error and return null
                Log.e(ERROR_TAG, "Error occurred when performing HTTP request.", e);
                return null;
            }
        } else {
            // Log the error and return null
            Log.e(ERROR_TAG, "Request endpoint does not start with http:// or https://.");
            return null;
        }
        return result;
    }


    /*
 * Send a POST request to {@endpoint}
 *
 * @param endpoint The endpoint to send the POST request to.
 * @param nameValuePairs The name and value pairs to post to the endpoint
 */
    public static String doPostData(String endpoint, ArrayList<BasicNameValuePair> nameValuePairs) {
        HttpClient httpclient = new DefaultHttpClient();
        // Specify the URL you want to post to
        HttpPost httppost = new HttpPost(endpoint);

        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Send the variable and value, in other words POST, to the URL
                HttpResponse response = httpclient.execute(httppost);

                return EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                // Log the error and return null
                Log.e(ERROR_TAG, "ClientProtocolException occurred when performing HTTP POST request.", e);
                return null;
            } catch (IOException ie) {
                // Log the error and return null
                Log.e(ERROR_TAG, "IOException occurred when performing HTTP POST request.", ie);
                return null;
            }
        } else {
            // Log the error and return null
            Log.e(ERROR_TAG, "Request endpoint does not start with http:// or https://.");
            return null;
        }
    }
}