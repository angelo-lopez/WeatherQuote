package com.angelosoft.angelo_romel.weatherquote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class Forecast3HrActivityFragment extends Fragment {
    //private final String LOG_TAG = MainActivity.class.getSimpleName();
    private FetchWeatherTask weatherTask;
    private ListView listView;
    private ThreeHrCustomAdapter customAdapter;

    private String selectedDate;

    private String[] forecast;
    private Integer[] weatherIcon;
    private String[] descForecast;
    private String[] temperature;
    private String[] windSpeed;
    private String[] windDirection;
    private String[] humidityClouds;

    public Forecast3HrActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_forecast3_hr, container, false);
        //ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_quote_forecast, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_forecast3);
        //listView.addHeaderView(header, null, false);
        /*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(prefs.getString("authType", "").equals("email")) {
            if (!HelperMethod.isLoggedIn(getActivity())) {
                final Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
                prefs.edit().putString("uid", "0").apply();
                prefs.edit().putString("selectedPlace", "").apply();
                mFireRef.unauth();
                Intent intent = new Intent(getActivity(),
                        LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        }

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            this.selectedDate = extras.getString("EXTRA_DATE");
            ((TextView) header.findViewById(R.id.h_am_textview)).setText(extras.getString("EXTRA_AM"));
            ((TextView) header.findViewById(R.id.h_pm_textview)).setText(extras.getString("EXTRA_PM"));
            ((TextView) header.findViewById(R.id.h_highlow_textview)).setText(extras.getString("EXTRA_HIGHLOW"));
            ((TextView) header.findViewById(R.id.h_description_textview)).setText(extras.getString("EXTRA_DESC"));
            ((TextView) header.findViewById(R.id.h_wind_speed_textview)).setText(extras.getString("EXTRA_WIND"));
            ((ImageView) header.findViewById(R.id.h_image_view)).setImageResource(extras.getInt("EXTRA_ART"));
            ((TextView) header.findViewById(R.id.h_quote_textview)).setText(HelperMethod.getRandomQuote(getActivity()));

        }
        */

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.selectedDate = prefs.getString("forecastDate", "");
        //((TextView) header.findViewById(R.id.h_am_textview)).setText(prefs.getString("morningTemp", ""));
        //((TextView) header.findViewById(R.id.h_pm_textview)).setText(prefs.getString("nightTemp", ""));
        //((TextView) header.findViewById(R.id.h_highlow_textview)).setText(prefs.getString("highLow", ""));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(getActivity(), "Pos: " + forecast[listView.getPositionForView(view)],
                //        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    //We want to get notified when a menu item gets selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle action bar item clicks here. The action bar will automatically
        //handle clicks on the Home/Up button, so long as you specify a
        //parent activity in AndroidManifest.xml.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int id = item.getItemId();
        if(id == R.id.action_show_map) {
            HelperMethod.openPreferredLocationInMap(getActivity());
            return true;
        }
        else if(id == R.id.action_goto_fav_location) {
            //if(prefs.getBoolean("isLoggedIn", false)) {
            //    Intent intent = new Intent(getActivity(), FavLocationAuthActivity.class);
            //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //    startActivity(intent);
            //    getActivity().finish();
            //}
            //else {
                Intent intent = new Intent(getActivity(), FavLocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            //}
        }
        else if(id == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(HelperMethod.getReadableDayString(Integer.parseInt(selectedDate)) + ", 3 Hour Forecast");
        updateWeather();
    }

    @Override
    public void onPause() {
        try {
            if(weatherTask != null) {
                weatherTask.cancel(true);
                weatherTask = null;
            }
        }
        catch(Exception ex) {
            //Log.e(LOG_TAG, "Error cancelling weatherTask.", ex);
        }
        finally {
            super.onPause();
        }
    }

    private void startForecastFail() {
        weatherTask = null;
        Bundle extras = new Bundle();
        extras.putString("EXTRA_TITLE", HelperMethod.getReadableDayString(Integer.parseInt(selectedDate)) + ", 3 Hour Forecast");
        //extras.putString("EXTRA_PARENT", "Forecast3");

        Intent intent = new Intent(getActivity(), ForecastFailActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            /*
            * Code below fires when user hits the Android back button(Button at bottom of screen).
            * Code is needed as meta-data "ParentActivityName" is disabled to allow two fragments to
            * use one xml file.
            * */
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void updateWeather() {
        weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        weatherTask.execute(prefs.getString("selectedPlace", ""));
    }

    public class FetchWeatherTask extends AsyncTask<String, String, Boolean> {
        //private ProgressDialog mProgressDialog;
        private SunSpinProgressDialog progressDialog;
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableTimeString(int time){
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("ZZZZZ");
            long lTime = time;
            return shortenedDateFormat.format(lTime * 1000);
        }

        private boolean storeWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {

            DecimalFormat df = new DecimalFormat("###.##");
            //These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_DESCRIPTION = "description";
            final String OWM_MAIN = "main";
            final String OWM_TEMP_MIN = "temp_min";
            final String OWM_TEMP_MAX = "temp_max";
            final String OWM_WIND = "wind";
            final String OWM_SPEED = "speed";
            final String OWM_DIRECTION = "deg";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_CLOUDS = "clouds";
            final String OWM_ALL = "all";
            final String OWM_ICON = "icon";

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                ArrayList<String> tempForecast = new ArrayList<String>();
                ArrayList<String> tempDescForecast = new ArrayList<String>();
                ArrayList<Integer> tempWeatherIcon = new ArrayList<Integer>();
                ArrayList<String> tempTemperature = new ArrayList<String>();
                ArrayList<String> tempWindSpeed = new ArrayList<String>();
                ArrayList<String> tempWindDirection = new ArrayList<String>();
                ArrayList<String> tempHumidityClouds = new ArrayList<String>();

                for (int i = 0; i < weatherArray.length(); i++) {
                    String time;

                    //Get the JSON object representing the day.
                    JSONObject dayForecast = weatherArray.getJSONObject(i);

                    time = dayForecast.getString("dt_txt").substring(11, 16);
                    //if (getReadableDateString(Integer.parseInt(selectedDate)).
                    //        equals(getReadableDateString(dayForecast.getInt("dt")))) {
                    if(HelperMethod.getReadableDateISOString(Integer.parseInt(selectedDate)).
                            equals(HelperMethod.getReadableDateISOString(dayForecast.getInt("dt")))) {
                        //Log.e(LOG_TAG,getReadableDateISOString(Integer.parseInt(selectedDate)) + " --- " +
                        //        getReadableDateISOString(dayForecast.getInt("dt")));
                        String description;
                        //Description is in a child array called "weather", which is 1 element long.
                        JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                        description = Character.toUpperCase(weatherObject.getString(OWM_DESCRIPTION).
                                toString().charAt(0)) + weatherObject.getString(OWM_DESCRIPTION).
                                toString().substring(1);

                        tempForecast.add(time);
                        tempDescForecast.add(description);
                        tempWeatherIcon.add(
                                HelperMethod.getIconResourceValue(HelperMethod.getIconResourceValueFromTime(weatherObject.getString(OWM_ICON),
                                        time)));

                        JSONObject tempObject = dayForecast.getJSONObject(OWM_MAIN);
                        tempTemperature.add("Low: " +
                                String.valueOf(tempObject.getDouble(OWM_TEMP_MIN))
                                + " " + getResources().getString(R.string.pref_unit_temperature) +
                                ", High:" +
                                String.valueOf(tempObject.getDouble(OWM_TEMP_MAX)) +
                                " " + getResources().getString(R.string.pref_unit_temperature));
                        try {
                            JSONObject windObject = dayForecast.getJSONObject(OWM_WIND);
                            tempWindSpeed.add("Wind Speed: " + windObject.getString(OWM_SPEED) +
                                    " meter/second");
                            tempWindDirection.add("Wind Direction: " + windObject.getString(OWM_DIRECTION) +
                                    " \u00B0");
                        }
                        catch(Exception ex) {
                            tempWindSpeed.add("Wind Speed: no data");
                            tempWindDirection.add("Wind Direction: data");
                        }


                        JSONObject humidityCloudsObject = dayForecast.getJSONObject(OWM_CLOUDS);
                        tempHumidityClouds.add("Humidity: " + tempObject.getString(OWM_HUMIDITY) + " %" +
                               ", Clouds: " +  humidityCloudsObject.getString(OWM_ALL) + " %");
                    }
                }

                tempForecast.trimToSize();
                tempDescForecast.trimToSize();
                tempWeatherIcon.trimToSize();
                tempTemperature.trimToSize();
                tempWindSpeed.trimToSize();
                tempWindDirection.trimToSize();
                tempHumidityClouds.trimToSize();

                forecast = new String[tempForecast.size()];
                descForecast = new String[tempDescForecast.size()];
                weatherIcon = new Integer[tempWeatherIcon.size()];
                temperature = new String[tempTemperature.size()];
                windSpeed = new String[tempWindSpeed.size()];
                windDirection = new String[tempWindDirection.size()];
                humidityClouds = new String[tempHumidityClouds.size()];

                forecast = tempForecast.toArray(forecast);
                descForecast = tempDescForecast.toArray(descForecast);
                weatherIcon = tempWeatherIcon.toArray(weatherIcon);
                temperature = tempTemperature.toArray(temperature);
                windSpeed = tempWindSpeed.toArray(windSpeed);
                windDirection = tempWindDirection.toArray(windDirection);
                humidityClouds = tempHumidityClouds.toArray(humidityClouds);

                if (forecast != null && forecast.length > 0) {
                    return true;
                } else {
                    return false;
                }
            }
          catch(Exception ex) {
              Log.e(LOG_TAG, "error json parsing.", ex);
              return false;
          }

        }

        @Override
        public Boolean doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            String location = params[0].toString().trim()
                    .replace(" ", "%20");

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=" +
                        location + "&units=" +
                        units + "&appid=" + getResources().getString(R.string.weather_appid));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                //Create a buffer that will store and build the contents of the input stream.
                StringBuffer buffer = new StringBuffer();
                if(isCancelled()) {
                    urlConnection.disconnect();
                    return false;
                }
                if(inputStream == null) {
                    urlConnection.disconnect();
                    return false;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    if(isCancelled()) {
                        urlConnection.disconnect();
                        return false;
                    }
                    buffer.append(line);
                }

                if(buffer.length() == 0) {
                    urlConnection.disconnect();
                    return false;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                urlConnection.disconnect();
                cancel(true);
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try{
                return storeWeatherDataFromJson(forecastJsonStr);
            }
            catch(JSONException e){
                urlConnection.disconnect();
                cancel(true);
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return false;
        }//end doInBackground()

        /*
        * Overriden method of the AsynTask. Runs on the main thread.
        * */
        @Override
        protected void onPostExecute(Boolean result) {
            //mProgressDialog.dismiss();
            progressDialog.dismiss();
            if(result == true){
                if (forecast != null && forecast.length > 0 && !forecast[0].equals("")) {
                    try {
                        customAdapter = new ThreeHrCustomAdapter(getContext(), forecast, descForecast,
                                weatherIcon, temperature, windSpeed, windDirection, humidityClouds);
                        listView.setAdapter(customAdapter);
                    }
                    catch(Exception ex) {
                        startForecastFail();
                    }
                }
                else {
                    startForecastFail();
                }
            }
            else {
                startForecastFail();
            }
        }

        @Override
        protected void onPreExecute() {
            /*
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Contacting the weather man to get your forecast.");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
            */
            if(isCancelled()) {
                startForecastFail();
            }
            else {
                progressDialog = new SunSpinProgressDialog(getActivity());
                progressDialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }

    }//end class FetchWeatherTask
}
