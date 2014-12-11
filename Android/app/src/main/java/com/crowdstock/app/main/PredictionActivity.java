package com.crowdstock.app.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.Authentication;
import com.crowdstock.app.utils.Connectivity;
import com.crowdstock.app.utils.HttpRequest;
import com.crowdstock.app.utils.NavigationDrawer;
import com.crowdstock.app.utils.StockListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PredictionActivity extends Activity {
    private static final String ACTIVITY_NAME = "Stocks";
    private static final String STOCK_URL = "http://server.billking.io/crowdstock/api/Stocks";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView stockListView;
    private ArrayList<String> stockData = new ArrayList<String>();




    String[] web = {
            "Google Plus",
            "Twitter",
            "Windows",
            "Bing",
            "Itunes",
            "Wordpress",
            "Drupal"
    } ;
    Integer[] imageId = {
            R.drawable.check,
            R.drawable.check,
            R.drawable.check,
            R.drawable.check,
            R.drawable.check,
            R.drawable.check,
            R.drawable.check
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        // Initialize the drawer items
        NavigationDrawer.initDrawerItems(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, NavigationDrawer.getActivityNames()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Set the adapter for the list views
//        stockListView = (ListView) findViewById(R.id.stocksListView);
//        final ArrayAdapter<String> stockAdapter = new ArrayAdapter<String>(this, R.layout.list_item, stockData);
//        stockListView.setAdapter(stockAdapter);

        StockListAdapter stockAdapter = new StockListAdapter(this, web, imageId);
        stockListView=(ListView)findViewById(R.id.stocksListView);
        stockListView.setAdapter(stockAdapter);
        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(c, "You Clicked at " +web[position], Toast.LENGTH_SHORT).show();
            }
        });


        populateStocksListView(this, stockAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // Switch the activity to the one selected in the drawer
            String activityNameSelected = NavigationDrawer.getActivityNames()[position];

            if (activityNameSelected.equals(ACTIVITY_NAME)) {
                // Don't go to the current activity if it is selected again
                mDrawerLayout.closeDrawers();
                return;
            }
            startActivity(NavigationDrawer.getActivityIntentMap().get(activityNameSelected));
        }
    }

    private void populateStocksListView(final Context c, final ArrayAdapter<String> stockAdapter) {
        if (!Connectivity.isConnected(c)) {
            Toast.makeText(this, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    String resp = null;
                    try {
                        if(Authentication.isAuthenticated(c)) {
                            resp = HttpRequest.doGetRequest(STOCK_URL, Authentication.getAuthToken(c));
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    final String response = resp;
                    // If the data was retrieved successfully, parse and place the data into the UI
                    Handler handler = new Handler(Looper.getMainLooper());
                    // Handler is necessary to gain reference to UI thread.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (response != null) {
                                    Log.v("REPONSE: ", response);

                                    JSONArray jobj = null;
                                    try {
                                        jobj = new JSONArray(response);


                                        if(jobj!=null) {
                                            for(int i=0; i<jobj.length(); i++) {
                                                JSONObject jsonObj = jobj.getJSONObject(i);

                                                String entry = jsonObj.get("Id").toString() + " -- " + jsonObj.get("Name").toString();

                                                stockData.add(entry);
                                                stockAdapter.add(entry);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }



                                stockAdapter.notifyDataSetChanged();
                            } else {
                                //view.setText("Failed to load stock data.");
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
