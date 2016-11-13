package com.angelosoft.angelo_romel.weatherquote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.github.clans.fab.FloatingActionButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class NowForecastActivityFragment extends Fragment {
    //private final String LOG_TAG = MainActivity.class.getSimpleName();
    private FetchWeatherTask weatherTask;
    private View view;

    private String description;
    private String temperature;
    private String pressure;
    private String humidity;
    private String minTemperature;
    private String maxTemperature;
    private String windSpeed;
    private String windDirection;
    private String clouds;
    private String date;
    private int weatherAnim;

    private TextView descriptionTextView;
    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView maxTempTextView;
    private TextView minTempTextView;
    private TextView windSpeedTextView;
    private TextView windDirectionTextView;
    private TextView cloudsTextView;
    private ImageView animationImageView;

    private AnimationDrawable animation;

    private Button fiveDayButton;
    private Button threeHourButton;
    //private FloatingActionMenu nowForecastFam;
    //private FloatingActionButton fiveDayFab;
    //private FloatingActionButton threeHourFab;

    AdRequest adRequest;

    public NowForecastActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Uncomment below before generating release build.
        adRequest = new AdRequest.Builder().build();

        //Test device id = 88418297FE29DF850CF429E3442F5CEA
        //Test ad:
        //adRequest = new AdRequest.Builder()
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
        //        .addTestDevice("88418297FE29DF850CF429E3442F5CEA")  // An example device ID
        //        .build();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_now_forecast, container, false);
        //nowForecastFam = (FloatingActionMenu) view.findViewById(R.id.now_forecast_fab_menu);
        //fiveDayFab = (FloatingActionButton) view.findViewById(R.id.five_days_fab);
        //threeHourFab = (FloatingActionButton) view.findViewById(R.id.three_hours_fab);
        fiveDayButton = (Button)view.findViewById(R.id.fiveDay_button);
        threeHourButton = (Button)view.findViewById(R.id.hour3_button);

        AdView adView = (AdView)view.findViewById(R.id.adViewNowForecast);
        adView.loadAd(adRequest);

        fiveDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ForecastActivity.class);
                startActivity(intent);
            }
        });

        threeHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putString("forecastDate", date).apply();
                Intent intent = new Intent(getActivity(), Forecast3HrActivity.class);
                startActivity(intent);
            }
        });

        /*
        fiveDayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowForecastFam.close(true);
                Intent intent = new Intent(getActivity(), ForecastActivity.class);
                startActivity(intent);
            }
        });

        threeHourFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowForecastFam.close(true);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putString("forecastDate", date).apply();
                Intent intent = new Intent(getActivity(), Forecast3HrActivity.class);
                startActivity(intent);
            }
        });
        */

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle action bar item clicks here. The action bar will automatically
        //handle clicks on the Home/Up button, so long as you specify a
        //parent activity in AndroidManifest.xml. But in this case, parent activity property name
        //in the AndroidManifest to allow more than one fragment access one layout.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int id = item.getItemId();
        if(id == R.id.action_show_map) {
            HelperMethod.openPreferredLocationInMap(getActivity());
            return true;
        }
        else if(id == R.id.action_goto_fav_location) {
            prefs.edit().putString("selectedPlace", "").apply();
            getActivity().finish();
        }
        //Catch the event when the user clicks on the action bar's back button.
        else if(id == android.R.id.home) {
            prefs.edit().putString("selectedPlace", "").apply();
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
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

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    prefs.edit().putString("selectedPlace", "").apply();
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getActivity().setTitle(prefs.getString("selectedPlace", ""));
        updateWeather();
        /*
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPreferredLocationInMap();
            }
        });
        */
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

    private void updateWeather() {
        weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        weatherTask.execute(prefs.getString("selectedPlace", ""));
    }

    private void startForecastFail() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        weatherTask = null;

        Bundle extras = new Bundle();
        extras.putString("EXTRA_TITLE", prefs.getString("selectedPlace", ""));
        extras.putString("EXTRA_PARENT", "ForecastNow");

        Intent intent = new Intent(getActivity(), ForecastFailActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
        getActivity().finish();
    }

    public class FetchWeatherTask extends AsyncTask<String, String, Boolean> {

        //private ProgressDialog mProgressDialog;
        private SunSpinProgressDialog progressDialog;
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private boolean storeWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            //These are the names of the JSON objects that need to be extracted.
            //weather
            final String OWM_WEATHER = "weather";
            final String OWM_DESCRIPTION = "description";
            final String OWM_ICON = "icon";
            //main
            final String OWM_MAIN = "main";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_PRESSURE = "pressure";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            //wind
            final String OWM_WIND = "wind";
            final String OWM_SPEED = "speed";
            final String OWM_DIRECTION = "deg";
            //clouds
            final String OWM_CLOUDS = "clouds";
            final String OWM_CLOUD = "all";


            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);

                JSONObject weatherObject = forecastJson.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = Character.toUpperCase(weatherObject.getString(OWM_DESCRIPTION).
                        toString().charAt(0)) + weatherObject.getString(OWM_DESCRIPTION).
                        toString().substring(1);
                weatherAnim = HelperMethod.getWeatherAnimationResourceValue(weatherObject.getString(OWM_ICON));
                date = Integer.toString(forecastJson.getInt("dt"));

                JSONObject mainObject = new JSONObject();

                try {
                    mainObject = forecastJson.getJSONObject(OWM_MAIN);
                }
                catch(Exception ex) {
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                }

                try {
                    temperature = "Temperature: " + mainObject.getString(OWM_TEMPERATURE) + " " +
                            getResources().getString(R.string.pref_unit_temperature);
                }
                catch(Exception ex) {
                    temperature = "Temperature: no data";
                }

                try {
                    humidity = "Humidity: " + mainObject.getString(OWM_HUMIDITY) + " %";
                }
                catch(Exception ex) {
                    humidity = "Humidity: no data";
                }

                try {
                    pressure = "Pressure: " + mainObject.getString(OWM_PRESSURE) + " hPa";
                }
                catch(Exception ex) {
                    pressure = "Pressure: no data";
                }

                try {
                    maxTemperature = "High: " + mainObject.getString(OWM_MAX) + " " +
                            getResources().getString(R.string.pref_unit_temperature);
                    minTemperature = "Low: " + mainObject.getString(OWM_MIN) + " " +
                            getResources().getString(R.string.pref_unit_temperature);
                }
                catch(Exception ex) {
                    maxTemperature = "High: no data";
                    minTemperature = "Low: no data";
                }

                JSONObject windObject = forecastJson.getJSONObject(OWM_WIND);
                try {
                    windSpeed = "Wind Speed: " + windObject.getString(OWM_SPEED) + " meter/second";
                }
                catch(Exception ex) {
                    windSpeed = "Wind Speed: no data";
                }
                try {
                    windDirection = "Wind Direction: " +  windObject.getString(OWM_DIRECTION) + " \u00B0";
                }
                catch(Exception ex) {
                    windDirection = "Wind Direction: no data";
                }

                JSONObject cloudObject = forecastJson.getJSONObject(OWM_CLOUDS);
                try {
                    clouds = "Clouds: " + cloudObject.getString(OWM_CLOUD) + " %";
                }
                catch(Exception ex) {
                    clouds = "Clouds: no data";
                }

                return true;
            }
            catch(Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
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
            int numDays = 16;
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" +
                        location + "&units=" +
                        units + "&appid=" + getResources().getString(R.string.weather_appid));
                /*
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" +
                        location + "&mode=" + format + "&units=" +
                        units + "&appid=" + getResources().getString(R.string.weather_appid));
                 */

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
                        urlConnection.disconnect();
                        cancel(true);
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try{
                return storeWeatherDataFromJson(forecastJsonStr, numDays);
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
            if(result == true) {
                if (description != null & view != null) {
                    try {
                        //customAdapter = new SevenDayCustomAdapter(getContext(), forecast, descForecast, subForecast,
                        //        morningTemp, nightTemp, windSpeed, weatherIcon);
                        //listView.setAdapter(customAdapter);
                        descriptionTextView = (TextView)view.findViewById(R.id.description_textView);
                        temperatureTextView = (TextView)view.findViewById(R.id.temperature_textView);
                        humidityTextView = (TextView)view.findViewById(R.id.humidity_textView);
                        pressureTextView = (TextView)view.findViewById(R.id.pressure_textView);
                        maxTempTextView = (TextView)view.findViewById(R.id.high_textView);
                        minTempTextView = (TextView)view.findViewById(R.id.low_textView);
                        windSpeedTextView = (TextView)view.findViewById(R.id.windSpeed_textView);
                        windDirectionTextView = (TextView)view.findViewById(R.id.windDirection_textView);
                        cloudsTextView = (TextView)view.findViewById(R.id.cloud_textView);
                        animationImageView = (ImageView)view.findViewById(R.id.animation_imageView);
                        animationImageView.setBackgroundResource(weatherAnim);

                        descriptionTextView.setText(description);
                        temperatureTextView.setText(temperature);
                        humidityTextView.setText(humidity);
                        pressureTextView.setText(pressure);
                        maxTempTextView.setText(maxTemperature);
                        minTempTextView.setText(minTemperature);
                        windSpeedTextView.setText(windSpeed);
                        windDirectionTextView.setText(windDirection);
                        cloudsTextView.setText(clouds);

                        animation = (AnimationDrawable) animationImageView.getBackground();
                        animation.start();

                        /*
                         imageQuote = ((ImageView)header.findViewById(R.id.quote_imageView));
                            animation = (AnimationDrawable) imageQuote.getBackground();
                            animation.start()
                         */
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
