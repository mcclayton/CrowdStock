package com.crowdstock.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.Authentication;
import com.crowdstock.app.utils.Connectivity;
import com.crowdstock.app.utils.HttpRequest;
import com.crowdstock.app.utils.NavigationDrawer;
import com.crowdstock.app.utils.StockListAdapter;
import com.crowdstock.app.utils.UserListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class LeaderboardActivity extends Activity {
    private static final String ACTIVITY_NAME = "Leaderboard";
    private static final String STOCK_URL = "http://server.billking.io/crowdstock/api/Stocks";
    private static final String USERS_URL = "https://server.billking.io/CrowdStock/api/users/top/20";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView stockListView;
    private ArrayList<String> stockData = new ArrayList<String>();
    private ArrayList<String> stockSymbolData = new ArrayList<String>();

    private ListView userListView;
    private ArrayList<String> userData = new ArrayList<String>();
    private ArrayList<String> userNameData = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        final Context context = this;

        // Initialize the drawer items
        NavigationDrawer.initDrawerItems(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, NavigationDrawer.getActivityNames()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        userListView = (ListView) findViewById(R.id.topUserslistView);
        UserListAdapter userAdapter = new UserListAdapter(this, userData, userNameData);
        userListView.setAdapter(userAdapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Take to user page
                //httpCompanyStockDataRequest(stockSymbolData.get(position), context);
            }
        });
        populateUsersListView(this, userAdapter);

        stockListView = (ListView) findViewById(R.id.topStocksListView);
        StockListAdapter stockAdapter = new StockListAdapter(this, stockData, stockSymbolData);
        stockListView.setAdapter(stockAdapter);
        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                httpCompanyStockDataRequest(stockSymbolData.get(position), context);
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

    private void httpCompanyStockDataRequest(String stockSymbol, final Context c) {
        final String webURL = "http://server.billking.io/crowdstock/api/Stocks/" + stockSymbol;
        final String stockName = stockSymbol;
        // Authentication.authenticateWithServer(this, "Admin@billking.io", "BrandanMillerDotCom");
        final EditText view = (EditText) findViewById(R.id.stockSearchView);
        if (!Connectivity.isConnected(this)) {
            Toast.makeText(this, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    String resp = null;
                    try {

                        if(Authentication.isAuthenticated(c)) {
                            resp = HttpRequest.doGetRequest(webURL, Authentication.getAuthToken(c));
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    final String response = resp;
                    // If the data was retrieved successfully, parse and place the data into the UI
                    Handler handler = new Handler(Looper.getMainLooper());
                    // Handler is necessary to gain reference to UI thread.
                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                if (response != null) {
                                    JSONObject jobj = null;
                                    try {
                                        jobj = new JSONObject(response);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(jobj!=null) {
                                        Intent i = new Intent(getApplicationContext(), StockProfileActivity.class);
                                        i.putExtra("stockSymbol", stockName);
                                        startActivity(i);
                                    }
                                } else {
                                    view.setText("Stock does not exist!");
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

                                                String entry = "SYMBOL: " + jsonObj.get("Id").toString() + "\nCONSENSUS: " + jsonObj.get("Consensus").toString() + " - OPTIMISM: " + jsonObj.get("Optimism").toString();
                                                stockSymbolData.add(jsonObj.get("Id").toString());
                                                stockData.add(entry);
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

    private void populateUsersListView(final Context c, final ArrayAdapter<String> userAdapter) {
        if (!Connectivity.isConnected(c)) {
            Toast.makeText(this, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    String resp = null;
                    try {
                        if(Authentication.isAuthenticated(c)) {
                            resp = HttpRequest.doGetRequest(USERS_URL, Authentication.getAuthToken(c));
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
                                    Log.v("RESPONSE: ", response);

                                    JSONArray jobj = null;
                                    try {
                                        jobj = new JSONArray(response);

                                        final DecimalFormat oneDigit = new DecimalFormat("#,##0.0");//format to 1 decimal place
                                        if(jobj!=null) {
                                            for(int i=0; i<jobj.length(); i++) {
                                                JSONObject jsonObj = jobj.getJSONObject(i);

                                                String entry = "NAME: " + jsonObj.get("Name").toString() + "\nREPUTATION: " + oneDigit.format(Double.parseDouble(jsonObj.get("Reputation").toString())) + "%\nAVG. SCORE: " + jsonObj.get("AverageScore").toString();
                                                userNameData.add(jsonObj.get("Name").toString());
                                                userData.add(entry);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    userAdapter.notifyDataSetChanged();
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
