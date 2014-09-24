package com.pedrovalencia.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pedrovalencia.sunshine.app.data.WeatherContract;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by pedrovalencia on 23/09/2014.
 */
public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString("location", "94043");
    }

    public static String getPreferredUnit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString("unit", "metric");
    }

    public static String formatTemperature(double tempIn, boolean isMetric) {
        double tempOut;
        if(!isMetric) {
            tempOut = 9*tempIn/5+32;
        } else {
            tempOut = tempIn;
        }
        return String.format("%.0f", tempOut);

    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("unit", "metric").equals("metric");
    }

    static String formatDate(String dateString) {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }
}
