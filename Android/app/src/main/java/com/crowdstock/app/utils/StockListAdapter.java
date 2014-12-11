package com.crowdstock.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crowdstock.app.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StockListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> stockData;
    private final ArrayList<String> stockSymbolData;
    private static final String VOTE_API_URL = "https://server.billking.io/CrowdStock/api/Votes";

    public StockListAdapter(Activity context, ArrayList<String> stockData, ArrayList<String> stockSymbolData) {
        super(context, R.layout.list_item, stockData);
        this.context = context;
        this.stockData = stockData;
        this.stockSymbolData = stockSymbolData;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtStockName);
        final Button voteButton = (Button) rowView.findViewById(R.id.voteButton);
        txtTitle.setText(stockData.get(position));

        voteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                LayoutInflater inflater = context.getLayoutInflater();
                final View voteDialog = inflater.inflate(R.layout.vote_dialog, null);
                RadioButton upButton = (RadioButton) voteDialog.findViewById(R.id.RadioButtonTrue);
                final RadioGroup radioGroup = (RadioGroup) voteDialog.findViewById(R.id.radio_group);
                radioGroup.check(upButton.getId());

                new AlertDialog.Builder(context)
                        .setTitle("Voting")
                        .setMessage("Voting on stock: " + stockSymbolData.get(position))
                        .setPositiveButton("Vote", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: implement the voting here
                                EditText et = (EditText) voteDialog.findViewById(R.id.editText);

                                boolean isPositive = false;
                                RadioButton rb = (RadioButton) voteDialog.findViewById(radioGroup.getCheckedRadioButtonId());
                                if (rb.getText().equals("Up")) {
                                    isPositive = true;
                                } else {
                                    isPositive = false;
                                }

                                int numOfDays = Integer.parseInt(et.getText().toString());

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Calendar c = Calendar.getInstance();
                                String nowAsISO = sdf.format(new Date());

                                try {
                                    c.setTime(sdf.parse(nowAsISO));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                c.add(Calendar.DATE, numOfDays);  // number of days to add
                                nowAsISO = sdf.format(c.getTime());  // dt is now the new date

                                final String date = nowAsISO;
                                final boolean positive = isPositive;








                                new Thread(new Runnable() {
                                    public void run() {
                                        ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                                        nameValuePairs.add(new BasicNameValuePair("StockId", stockSymbolData.get(position)));
                                        nameValuePairs.add(new BasicNameValuePair("isPositive", positive+""));
                                        nameValuePairs.add(new BasicNameValuePair("EndDate", date));
                                        final String response = HttpRequest.doPostData(VOTE_API_URL, nameValuePairs, Authentication.getAuthToken(context));
                                        Log.v("VOTE RESPONSE: ", response);


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
                                                            // Display duplicate vote error message
                                                            new AlertDialog.Builder(context)
                                                                    .setTitle("Duplicate Vote")
                                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                                    .setMessage("You have already voted on this stock.")
                                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                        }
                                                                    })
                                                                    .show();
                                                        }

                                                        if(jobj!=null) {
                                                            // Display success message
                                                            ImageView image = new ImageView(context);
                                                            image.setImageResource(R.drawable.check);
                                                            new AlertDialog.Builder(context)
                                                                    .setTitle("Success")
                                                                    .setMessage("Your vote has been successfully received.")
                                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                        }
                                                                    })
                                                                    .setView(image)
                                                                    .show();
                                                        }
                                                    } else {
                                                       // failure
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
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                        .setView(voteDialog)
                        .show();

            }
        });

        return rowView;
    }
}