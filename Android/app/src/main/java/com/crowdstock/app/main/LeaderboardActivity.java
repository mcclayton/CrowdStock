package com.crowdstock.app.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.NavigationDrawer;


public class LeaderboardActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Initialize the drawer items
        NavigationDrawer.initDrawerItems(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, NavigationDrawer.getActivityNames()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        String[] testUserData = {"0\t mleeli", "0\tadmin@billking.io"};
        String[] testStockData = {"ADP\t0.00%",
                "ALXN\t0.00%", "AMAG\t0.00%", "ANCX\t0.00%", "ARC\t0.00%",
                "ASPX\t0.00%", "ATHL\t0.00%", "BAH\t0.00%", "BREW\t0.00%",
                "BSTC\t0.00%", "CALD\t0.00%", "CEMP\t0.00%", "CF\t0.00%",
                "CFI\t0.00%", "CHDN\t0.00%", "CP\t0.00%", "CTAS\t0.00%",
                "DENN\t0.00%", "DSPG\t0.00%", "DTSI\t0.00%", "DVCR\t0.00%",
                "EIGI\t0.00%", "EPIQ\t0.00%", "ERIE\t0.00%"};
        ListView userListView = (ListView) findViewById(R.id.topUserslistView);
        ListView stockListView = (ListView) findViewById(R.id.topStocksListView);

        // Set the adapter for the list views
        userListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.list_item, testUserData));
        stockListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.list_item, testStockData));
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
            startActivity(NavigationDrawer.getActivityIntentMap().get(activityNameSelected));
        }
    }
}
