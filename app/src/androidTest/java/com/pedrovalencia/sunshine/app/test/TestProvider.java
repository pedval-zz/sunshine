package com.pedrovalencia.sunshine.app.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.pedrovalencia.sunshine.app.data.WeatherContract;
import com.pedrovalencia.sunshine.app.data.WeatherDbHelper;

/**
 * Created by pedrovalencia on 19/09/2014.
 */
public class TestProvider extends AndroidTestCase {

    private static final String LOG_TAG = TestProvider.class.getSimpleName();

    static public String TEST_LOCATION = "99705";
    static public String TEST_DATE = "20141205";

    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testUpdateLocation() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestDb.createNorthPoleLocationValues();

        Uri locationUri = mContext.getContentResolver().
                insert(WeatherContract.LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(WeatherContract.LocationEntry._ID, locationRowId);
        updatedValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(
                WeatherContract.LocationEntry.CONTENT_URI, updatedValues, WeatherContract.LocationEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, updatedValues);
    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }



    public void testInsertReadProvider() {
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

        Uri locationUserUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, values);

        long locationRowId = ContentUris.parseId(locationUserUri);

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
        Cursor cursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.CONTENT_URI,
                null, null, null, null);

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

        Uri insertUri = mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);

        long weatherRowId = ContentUris.parseId(insertUri);
        assertTrue(weatherRowId != -1);
        Log.d(LOG_TAG, "New row in weather table added " + weatherRowId);

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

        Cursor weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                null,
                null,
                null,
                null
                );

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

    public void testGetType() {
         // content://com.example.android.sunshine.app/weather/
         String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(WeatherContract.LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(WeatherContract.LocationEntry.CONTENT_ITEM_TYPE, type);
    }


}
