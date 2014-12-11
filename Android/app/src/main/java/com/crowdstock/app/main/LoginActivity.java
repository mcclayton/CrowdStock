package com.crowdstock.app.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.Authentication;
import com.crowdstock.app.utils.NavigationDrawer;


public class LoginActivity extends Activity {
    private static final String ACTIVITY_NAME = "Login";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent registerIntent = new Intent(this, RegisterActivity.class);
        final Intent mainIntent = new Intent(this, LoginActivity.class);
        final Context context = this;

        // Initialize the drawer items
        NavigationDrawer.initDrawerItems(this);


        // Login fields
        final TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        final TextView passwordTextView = (TextView) findViewById(R.id.passwordTextView);

        final ImageView checkView = new ImageView(this);
        checkView.setImageResource(R.drawable.check);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, NavigationDrawer.getActivityNames()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        final Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(registerIntent);
            }
        });
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Authentication.authenticateWithServerAndShowDialogs(context, usernameTextView.getText().toString(), passwordTextView.getText().toString(), checkView);
            }
        });

        if (Authentication.isAuthenticated(this)) {
            TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
            welcomeText.setText("Welcome, "+Authentication.getLoggedInUserName(context));

            loginButton.setText("Logout");
            loginButton.setBackgroundResource(R.drawable.red_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Logout and reset the Navigation drawer
                    Authentication.logout(context);
                    NavigationDrawer.initDrawerItems(context);

                    new AlertDialog.Builder(context)
                            .setTitle("Logout Success")
                            .setMessage("You successfully logged out.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close Dialog
                                    startActivity(mainIntent);
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        // TODO: Remove this HTTP request test
        //httpTest();

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
}
