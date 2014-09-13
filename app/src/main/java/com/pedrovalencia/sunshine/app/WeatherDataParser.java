package com.pedrovalencia.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pedrovalencia on 13/09/2014.
 */
public class WeatherDataParser {

    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) throws JSONException{

        double maxTemp = 0.0;

        JSONObject jsonObject = new JSONObject(weatherJsonStr);

        JSONArray jsonList = jsonObject.getJSONArray("list");

        if(jsonList.length() >= dayIndex+1) {
            maxTemp = jsonList.getJSONObject(dayIndex).getJSONObject("temp").getDouble("max");
        }

        return maxTemp;

    }


}
