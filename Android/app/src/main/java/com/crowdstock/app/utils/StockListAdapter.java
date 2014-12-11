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

public class StockListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;

    public StockListAdapter(Activity context, String[] web, Integer[] imageId) {
        super(context, R.layout.list_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtStockName);
        Button voteButton = (Button) rowView.findViewById(R.id.voteButton);
        txtTitle.setText(web[position]);


        voteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("STRING: ", web[position]);
            }
        });

        return rowView;
    }
}