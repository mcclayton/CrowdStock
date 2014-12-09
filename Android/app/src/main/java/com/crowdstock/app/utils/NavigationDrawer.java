package com.crowdstock.app.utils;

import android.content.Context;
import android.content.Intent;

import com.crowdstock.app.main.LeaderboardActivity;
import com.crowdstock.app.main.LoginActivity;
import com.crowdstock.app.main.PredictionActivity;
import com.crowdstock.app.main.SearchActivity;
import com.crowdstock.app.main.SettingsActivity;

import java.util.HashMap;

/**
 * Created by Michael Clayton on 10/8/14.
 */
public class NavigationDrawer {

    static HashMap<String, Intent> activityMap = new HashMap<String, Intent>();

    public static void initDrawerItems(Context c) {
        activityMap.clear();
        Intent login = new Intent(c, LoginActivity.class);
        Intent search = new Intent(c, SearchActivity.class);
        Intent stocks = new Intent(c, PredictionActivity.class);
        Intent leaderboard = new Intent(c, LeaderboardActivity.class);
        Intent settings = new Intent(c, SettingsActivity.class);
        //Intent userProfile = new Intent(c, UserProfileActivity.class);

        // Dynamically add links to navigation drawer based on whether the user is logged in or not
        if (Authentication.isAuthenticated(c)) {
            // Add activity names and their corresponding intents to the ActivityMap
            activityMap.put("Login", login);
            activityMap.put("Search", search);
            activityMap.put("Stocks", stocks);
            activityMap.put("Leaderboard", leaderboard);
            activityMap.put("Settings", settings);
            //activityMap.put("Profile", userProfile);
        } else {
            // Add activity names and their corresponding intents to the ActivityMap
            activityMap.put("Login", login);
        }
    }

    /**
     * Returns an array of the activity names (The keys of the activityMap)
     */
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
