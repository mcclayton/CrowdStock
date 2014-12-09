package com.crowdstock.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.Authentication;
import com.crowdstock.app.utils.Connectivity;
import com.crowdstock.app.utils.HttpRequest;
import com.crowdstock.app.utils.NavigationDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends Activity {
    private static final String ACTIVITY_NAME = "Search";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> viewAdapter;

    private static String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Authentication.authenticateWithServer(this, "Admin", "BrandanMillerDotCom");
        final String authToken = Authentication.getAuthToken(this);
        setContentView(R.layout.activity_search);

        final Context c = this.getApplicationContext();

        // Initialize the drawer items
        NavigationDrawer.initDrawerItems(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, NavigationDrawer.getActivityNames()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        Button button = (Button)findViewById(R.id.stockSearchSubmitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == findViewById(R.id.stockSearchSubmitButton)) {
                    TextView text = (TextView) findViewById(R.id.stockSearchView);
                    String userSelectedStock = text.getText().toString();

                    httpCompanyStockDataRequest(userSelectedStock, c);
                }
            }
        });

        Button userNameButton = (Button)findViewById(R.id.userNameSearchButton);
        userNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == findViewById(R.id.userNameSearchButton)) {
                    TextView text = (TextView) findViewById(R.id.userNameSearchView);
                    String selectedUser = text.getText().toString();

                    httpUserNameDataRequest(selectedUser, c);
                }
            }
        });

        /*AutoCompleteTextView autoComplete = (AutoCompleteTextView)findViewById(R.id.stockSearchView);
        String[] test = {"Hello"} ;
        viewAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, test);
        autoComplete.setAdapter(viewAdapter);
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                Log.w("HELOOOOOOO", "THIS: ");
                viewAdapter.notifyDataSetChanged();
                String[] hello = {"Hello"};
                viewAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, hello);
            }
        });*/

// In the onCreate method
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.stockSearchView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        textView.setAdapter(adapter);

        COUNTRIES = new String[]{"scheboygan", "waawaaa", "arkansas"};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        textView.setAdapter(adapter);

        /*autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.w("HELOOOOOOO", "THIS: " + charSequence);
                viewAdapter.notifyDataSetChanged();
                String[] hello = {"Hello"};
                viewAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, hello);
                //autoComplete.setAdapter(viewAdapter);
            }

            //generated stub
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            //generated stub
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
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

    private void httpUserNameDataRequest(String selectedUserName, final Context c) {
        final String webURL = "http://server.billking.io/crowdstock/api/Search/" + selectedUserName;
        final String userName = selectedUserName;
       // Authentication.authenticateWithServer(this, "Admin@billking.io", "BrandanMillerDotCom");
        final EditText view = (EditText) findViewById(R.id.userNameSearchView);
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
                                    JSONArray jobj = null;
                                    try {
                                        jobj = new JSONArray(response);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(jobj!=null) {
                                        JSONObject jsonObj = jobj.getJSONObject(0);
                                        if(jsonObj.getString("Type").equals("user")) {
                                            Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                                            i.putExtra("userName", userName);
                                            startActivity(i);
                                        }
                                    }
                                } else {
                                    view.setText("User does not exist!");
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
}
