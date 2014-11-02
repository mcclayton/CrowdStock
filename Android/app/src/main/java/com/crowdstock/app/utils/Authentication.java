package com.crowdstock.app.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Michael Clayton on 9/28/14.
 *
 * Utility class for client authentication with the server and session management.
 */
public class Authentication {
    private static final String AUTH_API_URL = "https://server.billking.io/CrowdStock/api/Authenticate";

    /*
	 * Authenticates the Android client with the server over HTTPS
	 */
    public static void authenticateWithServer(final Context c, final String username, final String password) {
        if (!Connectivity.isConnected(c)) {
            Toast.makeText(c, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("Name", username));
                    nameValuePairs.add(new BasicNameValuePair("Password", password));
                    final String response = HttpRequest.doPostData(AUTH_API_URL, nameValuePairs);

                    // If the data was retrieved successfully, parse and place the data into the UI
                    Handler handler = new Handler(Looper.getMainLooper());
                    // Handler is necessary to gain reference to UI thread.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (response != null) {
                                    // Handle data
                                    Toast.makeText(c, "AUTHENTICATION RESPONSE: "+response.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // Unable to retrieve data
                                }
                            } catch (Exception e) {
                                // Unable to retrieve data
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }).start();
        }
    }
}