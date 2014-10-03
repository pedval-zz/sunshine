package com.pedrovalencia.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pedrovalencia.sunshine.app.data.WeatherContract;

/**
 * Created by pedrovalencia on 30/09/2014.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String DATE_KEY = "forecast_date";
    public static final String LOCATION_KEY = "forecast_location";
    public static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private String mForecastStr;
    SimpleCursorAdapter mForecastAdapter;

    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        return rootView;

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share action provider is null?");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            mForecastStr = savedInstanceState.getString(LOCATION_KEY);
        }
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mForecastStr);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(DetailActivity.DATE_KEY)
                && mForecastStr != null &&
                !mForecastStr.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(DATE_KEY)) {
            return null;
        }
        String forecastDate = intent.getStringExtra(DATE_KEY);

        String location = Utility.getPreferredLocation(getActivity());
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, forecastDate);

        return new CursorLoader(
                getActivity(),
                weatherUri,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        String dateString = Utility.getFriendlyDayString(this.getActivity().getApplicationContext(),
                data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)));
        String weatherDescription =
                data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));

        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(this.getActivity().getApplicationContext(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
        String low = Utility.formatTemperature(this.getActivity().getApplicationContext(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), isMetric);

        String humidity = Utility.formatHumidity(this.getActivity().getApplicationContext(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));

        float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
        float windDirStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));



        String wind = Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr);


        String pressure = Utility.formatPressure(this.getActivity().getApplicationContext(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE)));


        ((TextView) getView().findViewById(R.id.detail_date)).setText(dateString);
        ((TextView) getView().findViewById(R.id.detail_high)).setText(high);
        ((TextView) getView().findViewById(R.id.detail_low)).setText(low);
        ((TextView) getView().findViewById(R.id.detail_humidity)).setText(humidity);
        ((TextView) getView().findViewById(R.id.detail_wind)).setText(wind);
        ((TextView) getView().findViewById(R.id.detail_pressure)).setText(pressure);
        ((TextView) getView().findViewById(R.id.detail_forecast)).setText(weatherDescription);
        ((ImageView)getView().findViewById(R.id.detail_icon)).setImageResource(Utility.getImageDetail(weatherDescription));

        // We still need this for the share intent
        mForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        Log.v(LOG_TAG, "Forecast String: " + mForecastStr);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
}