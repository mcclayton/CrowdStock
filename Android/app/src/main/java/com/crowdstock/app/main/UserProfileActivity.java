package com.crowdstock.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.Authentication;
import com.crowdstock.app.utils.Connectivity;
import com.crowdstock.app.utils.HttpRequest;
import com.crowdstock.app.utils.NavigationDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileActivity extends Activity {
    private static final String ACTIVITY_NAME = "User";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent i = getIntent();
        String userName = null;
        if(i.getExtras()!=null) {
            userName = i.getStringExtra("userName");
        }

        // Initialize the drawer items
        NavigationDrawer.initDrawerItems(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, NavigationDrawer.getActivityNames()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        final Context c = this.getApplicationContext();
        httpUserNameDataRequest(userName, c);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
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
        final String webURL = "http://server.billking.io/crowdstock/api/user/name/" + selectedUserName;
        final String userName = selectedUserName;
        final EditText view = (EditText) findViewById(R.id.userNameSearchView);
        if (!Connectivity.isConnected(this)) {
            Toast.makeText(this, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    String resp = null;
                    try {

                        if (Authentication.isAuthenticated(c)) {
                            resp = HttpRequest.doGetRequest(webURL, Authentication.getAuthToken(c));
                        }
                    } catch (Exception e) {
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
                                    JSONObject jobj = null;
                                    try {
                                        jobj = new JSONObject(response);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(jobj!=null) {
                                        TextView textViewUser = (TextView) findViewById(R.id.userNameTextView);
                                        textViewUser.setText(jobj.getString("Name"));

                                        Integer reputation = jobj.getInt("Reputation");
                                        TextView textViewReputation = (TextView)findViewById(R.id.reputation_percentage);
                                        textViewReputation.setText(Integer.toString(reputation)+"%");

                                        Integer avgScore = jobj.getInt("AverageScore");
                                        TextView textViewAvgScore = (TextView)findViewById(R.id.average_score_percentage);
                                        textViewAvgScore.setText(Integer.toString(avgScore));

                                        Integer voteCount = jobj.getInt("nVotes");
                                        TextView textViewVoteCount = (TextView)findViewById(R.id.votes_cast_percentage);
                                        textViewVoteCount.setText(Integer.toString(voteCount));
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
