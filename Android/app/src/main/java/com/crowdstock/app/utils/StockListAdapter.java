package com.crowdstock.app.utils;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.crowdstock.app.R;

import java.util.ArrayList;

public class StockListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> stockData;
    private final ArrayList<String> stockSymbolData;

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
        Button voteButton = (Button) rowView.findViewById(R.id.voteButton);
        txtTitle.setText(stockData.get(position));


        voteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("STRING: ", stockData.get(position) + "----" + stockSymbolData.get(position));

            }
        });

        return rowView;
    }
}