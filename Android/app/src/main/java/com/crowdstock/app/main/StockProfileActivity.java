package com.crowdstock.app.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdstock.app.R;
import com.crowdstock.app.utils.Connectivity;
import com.crowdstock.app.utils.HttpRequest;
import com.crowdstock.app.utils.NavigationDrawer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StockProfileActivity extends Activity {
    private static final String ACTIVITY_NAME = "Stock";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_profile);

        Intent i = getIntent();
        String stockSymbol = null;
        if(i.getExtras()!=null) {
            stockSymbol = i.getStringExtra("stockSymbol");
        }

        // Initialize the drawer items
        NavigationDrawer.initDrawerItems(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, NavigationDrawer.getActivityNames()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        httpCompanyStockDataRequest(stockSymbol);

        TextView stockName = (TextView)findViewById(R.id.userNameTextView);
        stockName.setText("Stock Name: "+stockSymbol.toUpperCase());

        httpCompanyStockGraphInfo(stockSymbol);

        // init example series data
  /*      GraphViewSeries exampleSeries = new GraphViewSeries(new GraphView.GraphViewData[] {
                new GraphView.GraphViewData(1, 2.0d)
                , new GraphView.GraphViewData(2, 1.5d)
                , new GraphView.GraphViewData(3, 2.5d)
                , new GraphView.GraphViewData(4, 1.0d)
        });

        GraphView graphView = new LineGraphView(
                this // context
                , "" // heading
        );
        graphView.addSeries(exampleSeries); // data

        LinearLayout layout = (LinearLayout)findViewById(R.id.masterLinearLayout);
        layout.addView(graphView);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock_profile, menu);
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

    private void httpCompanyStockDataRequest(String stockSymbol) {
        final String webURL = "https://server.billking.io/crowdstock/api/Stocks/" + stockSymbol;
        final String stockName = stockSymbol;
        final TextView view = (TextView) findViewById(R.id.stockPrice);
        if (!Connectivity.isConnected(this)) {
            Toast.makeText(this, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    String resp = null;
                    try {
                            resp = HttpRequest.doGetRequest(webURL);
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
                                        JSONObject newObj = jobj.getJSONObject("LastHistory");
                                        view.setText("Current Stock Price: "+newObj.getString("Value"));
                                        String imageArray = jobj.getString("Logo");
                                        byte[] image = Base64.decode(imageArray,Base64.DEFAULT);
                                        ImageView imageView = (ImageView)findViewById(R.id.stockImageView);
                                        Bitmap companyLogo = BitmapFactory.decodeByteArray(image, 0, image.length);
                                        imageView.setImageBitmap(companyLogo);

                                        TextView textView = (TextView)findViewById(R.id.bullishPercentageField);
                                        Integer num = jobj.getInt("Consensus") * 100;
                                        textView.setText(num.toString()+"%");

                                        textView = (TextView)findViewById(R.id.bearishPercentageField);
                                        num = 100 - num;
                                        textView.setText(num.toString()+"%");
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

    private void httpCompanyStockGraphInfo(String stockSymbol) {
        final String webURL = "http://server.billking.io/CrowdStock/api/History/?stock="+stockSymbol+"&count=5";
        final String stockName = stockSymbol;
        final TextView view = (TextView) findViewById(R.id.stockPrice);
        if (!Connectivity.isConnected(this)) {
            Toast.makeText(this, "Please ensure an internet connection is established.", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    String resp = null;
                    try {
                        resp = HttpRequest.doGetRequest(webURL);
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
                                    JSONArray jarr = null;
                                    try {
                                        jarr = new JSONArray(response);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(jarr!=null) {
                                        GraphView.GraphViewData[] graphInputArray = new GraphView.GraphViewData[5];
                                        JSONArray array = jarr.getJSONArray(0);
                                        for(int i=0; i<array.length(); i++){
                                            JSONObject newObj = array.getJSONObject(i);
                                            Double value = newObj.getDouble("Value");
                                            GraphView.GraphViewData obj = new GraphView.GraphViewData(i+1,value);
                                            graphInputArray[i]=obj;
                                        }


                                        /*GraphViewSeries exampleSeries = new GraphViewSeries(new GraphView.GraphViewData[] {
                                                new GraphView.GraphViewData(1, 2.0d)
                                                , new GraphView.GraphViewData(2, 1.5d)
                                                , new GraphView.GraphViewData(3, 2.5d)
                                                , new GraphView.GraphViewData(4, 1.0d)
                                        });*/

                                        GraphViewSeries exampleSeries = new GraphViewSeries(graphInputArray);

                                        LinearLayout layout = (LinearLayout)findViewById(R.id.masterLinearLayout);

                                        GraphView graphView = new LineGraphView(
                                                layout.getContext() // context
                                                , "" // heading
                                        );
                                        graphView.addSeries(exampleSeries); // data
                                        layout.addView(graphView);
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
