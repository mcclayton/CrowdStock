package com.crowdstock.app.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import com.crowdstock.app.R;
import com.crowdstock.app.utils.Connectivity;
import com.crowdstock.app.utils.HttpRequest;
import com.jjoe64.graphview.*;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StockProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_profile);

        Intent i = getIntent();
        String stockSymbol = null;
        if(i.getExtras()!=null) {
            stockSymbol = i.getStringExtra("stockSymbol");
        }

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
            final String webURL = "http://server.billking.io/crowdstock/api/Stocks/" + stockSymbol;
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
}
