package com.crowdstock.app.utils;

import android.content.Context;
import android.content.Intent;

import com.crowdstock.app.main.MainActivity;
import com.crowdstock.app.main.SearchActivity;
import com.crowdstock.app.main.UserProfileActivity;

import java.util.HashMap;

/**
 * Created by Michael Clayton on 10/8/14.
 */
public class NavigationDrawer {

    static HashMap<String, Intent> activityMap = new HashMap<String, Intent>();

    public static void initDrawerItems(Context c) {
        Intent main = new Intent(c, MainActivity.class);
        Intent search = new Intent(c, SearchActivity.class);
        Intent userProfile = new Intent(c, UserProfileActivity.class);

        activityMap.put("Main", main);
        activityMap.put("Search", search);
        activityMap.put("Profile", userProfile);


    }

    public static String[] getActivityNames() {
        String[] activityNames = new String[activityMap.size()];
        int i=0;
        for (String s : activityMap.keySet()) {
            activityNames[i++] = s;
        }
        return activityNames;
    }

    public static HashMap<String, Intent> getActivityIntentMap() {
        return activityMap;
    }
}
