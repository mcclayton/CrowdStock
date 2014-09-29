package com.crowdstock.app.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.Connectivity;
import com.crowdstock.app.utils.HttpRequest;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        // TODO: Remove this HTTP request test
        httpTest();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Remove this HTTP request test method
    private void httpTest() {
        final TextView view = (TextView) findViewById(R.id.testTextView);
        if (!Connectivity.isConnected(this)) {
            Toast.makeText(this, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    final String response = HttpRequest.doGetRequest("http://date.jsontest.com/");
                    // If the data was retrieved successfully, parse and place the data into the UI
                    Handler handler = new Handler(Looper.getMainLooper());
                    // Handler is necessary to gain reference to UI thread.
                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                if (response != null) {
                                    // TODO: Handle data
                                    view.setText(response);
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
