package com.crowdstock.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import com.crowdstock.app.main.LoginActivity;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Michael Clayton on 9/28/14.
 *
 * Utility class for client authentication with the server and session management.
 */
public class Authentication {
    private static final String PREFS_NAME = "AuthPrefsFile";

    private static final String AUTH_API_URL = "https://server.billking.io/CrowdStock/api/Authenticate";
    private static final String IS_AUTH_URL = "https://server.billking.io/CrowdStock/api/ValidateToken";

    private static final Pattern quotePattern = Pattern.compile("\"([^\"]*)\"");


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
                                    // Remove quotes from auth token
                                    Matcher m = quotePattern.matcher(response);
                                    String responseWithoutQuotes = null;
                                    if (m.find()) {
                                        responseWithoutQuotes = m.group(1);
                                    }

                                    // Update the saved auth token
                                    SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("userAuthToken", responseWithoutQuotes);
                                    editor.commit();
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

    /*
	 * Authenticates the Android client with the server over HTTPS and shows success/failure dialogs
	 */
    public static void authenticateWithServerAndShowDialogs(final Context c, final String username, final String password, final ImageView checkView) {
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
                                    // Remove quotes from auth token
                                    Matcher m = quotePattern.matcher(response);
                                    String responseWithoutQuotes = null;
                                    if (m.find()) {
                                        responseWithoutQuotes = m.group(1);
                                    }

                                    if (responseWithoutQuotes.equals("failed")) {
                                        new AlertDialog.Builder(c)
                                                .setTitle("Login Failure")
                                                .setMessage("Failed to login as: "+username+".\nPlease check username/password and try again.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Close Dialog
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    } else {

                                        // Update the saved auth token
                                        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("userAuthToken", responseWithoutQuotes);
                                        editor.putString("userName", username);
                                        editor.commit();


                                        // Auth was successful
                                        //ImageView image = new ImageView(c);
                                        //image.setImageResource(R.drawable.check);
                                        NavigationDrawer.initDrawerItems(c);
                                        new AlertDialog.Builder(c)
                                                .setTitle("Success")
                                                .setMessage("You have successfully logged in as: " + username)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Proceed with login (change activity)
                                                        final Intent mainIntent = new Intent(c, LoginActivity.class);
                                                        c.startActivity(mainIntent);
                                                        Activity act = (Activity) c;
                                                        act.finish();
                                                    }
                                                })
                                                .setView(checkView)
                                                .show();
                                    }
                                } else {
                                    // Unable to retrieve data
                                    new AlertDialog.Builder(c)
                                            .setTitle("Login Failure")
                                            .setMessage("Failed to login as: "+username+".\nPlease check username/password and try again.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Close Dialog
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
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
    /*
	 * Determines if the authentication token is
	 */
    public static boolean isAuthenticated(final Context c) {
        final String userAuthToken = getAuthToken(c);

        if (userAuthToken == null) {
            return false;
        }

        if (!Connectivity.isConnected(c)) {
            Toast.makeText(c, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            RunnableFuture<Boolean> f = new FutureTask<Boolean>(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    final String response = HttpRequest.doGetRequest(IS_AUTH_URL, userAuthToken);
                    if (response == null) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
            // Start the thread to execute it (you may also use an Executor)
            new Thread(f).start();
            // Get the result
            try {
                return f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static String getAuthToken(Context c) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("userAuthToken",  null);
    }

    public static String getLoggedInUserName(Context c) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("userName",  null);
    }

    public static void logout(Context c) {
        // Update the saved auth token to be null
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userAuthToken", null);
        editor.putString("userName", null);
        editor.commit();
    }
}