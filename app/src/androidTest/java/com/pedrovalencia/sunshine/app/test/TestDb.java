package com.pedrovalencia.sunshine.app.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.pedrovalencia.sunshine.app.data.WeatherContract;
import com.pedrovalencia.sunshine.app.data.WeatherDbHelper;

/**
 * Created by pedrovalencia on 19/09/2014.
 */
public class TestDb extends AndroidTestCase {

    private static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        db.close();
    }

    public void testInsertReadDb() {
        //Test data to be inserted on database
        String testName = "North Pole";
        String testLocationSettings = "99705";
        double testLatitude = 66.772;
        double testLongitude = -147.355;

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, testName);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSettings);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);

        //Verify we got a row back
        assertTrue(locationRowId >= 1);
        Log.d(LOG_TAG, "New row id: "+locationRowId);

        //Specify the columns we want
        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };

        //Interface to access to the results
        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, columns,
                null, null, null, null, null);

        if(cursor.moveToFirst()) {

            String location = (String)values.get(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
            double latitude = (Double)values.get(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
            double longitude = (Double)values.get(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
            String cityName = (String)values.get(WeatherContract.LocationEntry.COLUMN_CITY_NAME);

            assertEquals(cityName, testName);
            assertEquals(location, testLocationSettings);
            assertEquals(latitude, testLatitude);
            assertEquals(longitude, testLongitude);
        } else {
            fail("No values returned");
        }

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);

        assertTrue(weatherRowId != -1);
        Log.d(LOG_TAG, "New row in weather table added "+weatherRowId);

        String [] weatherColumns = {
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        };

        Cursor weatherCursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME, weatherColumns,
                null,null,null,null,null);

        if(weatherCursor.moveToFirst()) {
            int weatherId = (Integer)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
            long locKey = (Long)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
            String date = (String)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_DATETEXT);
            double degrees = (Double)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_DEGREES);
            double humidity = (Double)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
            double pressure = (Double)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
            int maxTemp = (Integer)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
            int minTemp = (Integer)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
            String shortDesc = (String)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
            double windSpeed = (Double)weatherValues.get(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);

            assertEquals(weatherId, 321);
            assertEquals(locKey, locationRowId);
            assertEquals(date, "20141205");
            assertEquals(degrees, 1.1);
            assertEquals(humidity, 1.2);
            assertEquals(pressure, 1.3);
            assertEquals(maxTemp, 75);
            assertEquals(minTemp, 65);
            assertEquals(shortDesc, "Asteroids");
            assertEquals(windSpeed, 5.5);

        } else {
            fail("Failing retrieving row from weather table");
        }


    }


}
