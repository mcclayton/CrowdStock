package com.crowdstock.app.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crowdstock.app.R;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> userData;
    private final ArrayList<String> userNameData;

    public UserListAdapter(Activity context, ArrayList<String> userData, ArrayList<String> userNameData) {
        super(context, R.layout.list_item, userData);
        this.context = context;
        this.userData = userData;
        this.userNameData = userNameData;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.user_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtUserName);
        final ImageView profileImg = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(userData.get(position));

        return rowView;
    }
}