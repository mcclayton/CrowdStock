package com.crowdstock.app.utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Michael Clayton on 9/28/14.
 * <p/>
 * Utility class for HTTP requests.
 */
public class HttpRequest {
    private static final String ERROR_TAG = "HttpRequest";
    private static HttpClient httpclient = new DefaultHttpClient();

    static {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(
                new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        httpclient = new DefaultHttpClient(cm, params);
    }

    /**
     * Send a GET request to {@endpoint}
     *
     * @param endpoint The endpoint to send the GET request to.
     */
    public static String doGetRequest(String endpoint) {
        HttpClient httpclient = new DefaultHttpClient();
        return doGetRequest(endpoint, null);
    }

    /**
     * Execute the GET request {@getRequest}
     *
     * @param endpoint The endpoint to send the GET request to.
     * @param authToken The auth token to authenticate the request with
     */
    public static String doGetRequest(String endpoint, String authToken) {
        HttpGet httpGet = new HttpGet(endpoint);
        httpGet.addHeader("Authorization", "Bearer " + authToken);

        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            try {
                // Send the variable and value, in other words POST, to the URL
                HttpResponse response = httpclient.execute(httpGet);

                int status = response.getStatusLine().getStatusCode();
                if(status == HttpStatus.SC_OK) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    return null;
                }
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


    /**
     * Send a POST request to {@endpoint}
     *
     * @param endpoint       The endpoint to send the POST request to.
     * @param nameValuePairs The name and value pairs to post to the endpoint
     */
    public static String doPostData(String endpoint, ArrayList<BasicNameValuePair> nameValuePairs) {
        HttpClient httpclient = new DefaultHttpClient();
        // Specify the URL you want to post to
        HttpPost httpPost = new HttpPost(endpoint);

        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Send the variable and value, in other words POST, to the URL
                HttpResponse response = httpclient.execute(httpPost);

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