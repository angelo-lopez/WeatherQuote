package com.angelosoft.angelo_romel.weatherquote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class ForecastActivityFragment extends Fragment {
    //private final String LOG_TAG = MainActivity.class.getSimpleName();
    private FetchWeatherTask weatherTask;

    private ListView listView;
    private SevenDayCustomAdapter customAdapter;
    private String[] forecast;
    private String[] descForecast;
    private String[] subForecast;
    private String[] pressure;
    private String[] humidity;
    private String[] windSpeed;
    private String[] temperature;
    private Integer[] weatherIcon;
    private String[] date;

    public ForecastActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_forecast, container, false);
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header_forecast, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putString("forecastDate", date[listView.getPositionForView(view)-1]).apply();
                //prefs.edit().putString("morningTemp", morningTemp[listView.getPositionForView(view)-1]).apply();
                //prefs.edit().putString("nightTemp", nightTemp[listView.getPositionForView(view)-1]).apply();
                //prefs.edit().putString("highLow", subForecast[listView.getPositionForView(view)-1]).apply();
                //prefs.edit().putString("windSpeed", windSpeed[listView.getPositionForView(view)-1]).apply();
                /*
                Bundle extras = new Bundle();
                extras.putString("EXTRA_DATE", date[listView.getPositionForView(view)]);
                extras.putString("EXTRA_DESC", descForecast[listView.getPositionForView(view)]);
                extras.putString("EXTRA_AM", morningTemp[listView.getPositionForView(view)]);
                extras.putString("EXTRA_PM", nightTemp[listView.getPositionForView(view)]);
               Intent intent = new Intent(getActivity(), Forecast3HrActivity.class);
                //intent.putExtras(extras);
                startActivity(intent);  extras.putString("EXTRA_HIGHLOW", subForecast[listView.getPositionForView(view)]);
                extras.putString("EXTRA_WIND", windSpeed[listView.getPositionForView(view)]);
                extras.putInt("EXTRA_ART", weatherArt[listView.getPositionForView(view)]);
                */
                Intent intent = new Intent(getActivity(), Forecast3HrActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    //We want to get notified when a menu item gets selected.
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
        //Catch the event when the user clicks on the action bar's back button.
        else if(id == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getActivity().setTitle(prefs.getString("selectedPlace", ""));
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        weatherTask = null;

        Bundle extras = new Bundle();
        extras.putString("EXTRA_TITLE", prefs.getString("selectedPlace", ""));
        //extras.putString("EXTRA_PARENT", "Forecast5");

        Intent intent = new Intent(getActivity(), ForecastFailActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
        getActivity().finish();
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

        private String formatHighLows(double high, double low){
            //For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = "(High/Low) " + roundedHigh +
                    getResources().getString(R.string.pref_unit_temperature) + "/" +
                    roundedLow + getResources().getString(R.string.pref_unit_temperature);
            return highLowStr;
        }

        private boolean storeWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {

            //These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_MAIN = "main";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            final String OWM_PRESSURE = "pressure";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_DESCRIPTION = "description";
            final String OWM_MORNING = "morn";
            final String OWM_NIGHT = "night";
            final String OWM_WIND = "wind";
            final String OWM_SPEED = "speed";
            final String OWM_ICON = "icon";
            String dtDate = "";
            int newIndex = 0;

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                forecast = new String[weatherArray.length()];
                descForecast = new String[weatherArray.length()];
                subForecast = new String[weatherArray.length()];
                pressure = new String[weatherArray.length()];
                humidity = new String[weatherArray.length()];
                windSpeed = new String[weatherArray.length()];
                weatherIcon = new Integer[weatherArray.length()];
                date = new String[weatherArray.length()];
                temperature = new String[weatherArray.length()];

                ArrayList<String> tempForecast = new ArrayList<String>();
                ArrayList<String> tempDescForecast = new ArrayList<String>();
                ArrayList<String> tempSubForecast = new ArrayList<String>();
                ArrayList<String> tempPressure = new ArrayList<String>();
                ArrayList<String> tempHumidity = new ArrayList<String>();
                ArrayList<String> tempTemperature = new ArrayList<String>();
                ArrayList<String> tempWindSpeed = new ArrayList<String>();
                ArrayList<String> tempDate = new ArrayList<String>();
                ArrayList<Integer> tempWeatherIcon = new ArrayList<Integer>();

                for (int i = 0; i < weatherArray.length(); i++) {
                    String day;
                    String description;
                    String highAndLow;

                    //Get the JSON object representing the day.
                    JSONObject dayForecast = weatherArray.getJSONObject(i);
                    if(!dayForecast.getString("dt_txt").substring(0,10)
                    .equals(dtDate)) {
                        if (Integer.parseInt(dayForecast.getString("dt_txt").substring(11, 13)) > 9) {
                            day = HelperMethod.getReadableDayString(dayForecast.getInt("dt"));
                            tempDate.add(Integer.toString(dayForecast.getInt("dt")));
                            //date[newIndex] = Integer.toString(dayForecast.getInt("dt"));
                            //nameDate[newIndex] = HelperMethod.getReadableDateNameString(dayForecast.getInt("dt"));

                            JSONObject windObject = dayForecast.getJSONObject(OWM_WIND);
                            try {
                                tempWindSpeed.add("Wind Speed: " + windObject.getString(OWM_SPEED) + " meter/second");
                            } catch (Exception ex) {
                                tempWindSpeed.add("Wind Speed: no data");
                            }

                            //Description is in a child array called "weather", which is 1 element long.
                            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                            description = Character.toUpperCase(weatherObject.getString(OWM_DESCRIPTION).
                                    toString().charAt(0)) + weatherObject.getString(OWM_DESCRIPTION).
                                    toString().substring(1);
                            tempForecast.add(day);
                            tempDescForecast.add(description);
                            tempWeatherIcon.add(HelperMethod.getIconResourceValue(weatherObject.getString(OWM_ICON)));

                            JSONObject mainObject = dayForecast.getJSONObject(OWM_MAIN);
                            tempSubForecast.add("High:" + mainObject.getString(OWM_MAX) +
                                    getResources().getString(R.string.pref_unit_temperature) +
                                    ", Low:" + mainObject.getString(OWM_MIN) +
                                    getResources().getString(R.string.pref_unit_temperature));
                            tempPressure.add("Prs: " + mainObject.getString(OWM_PRESSURE) + " hPa");
                            tempHumidity.add("Hum: " + mainObject.getString(OWM_HUMIDITY) + " %");
                            tempTemperature.add(mainObject.getString(OWM_TEMPERATURE) + " " +
                                    getResources().getString(R.string.pref_unit_temperature));

                            tempForecast.trimToSize();
                            tempDescForecast.trimToSize();
                            tempSubForecast.trimToSize();
                            tempPressure.trimToSize();
                            tempHumidity.trimToSize();
                            tempTemperature.trimToSize();
                            tempWindSpeed.trimToSize();
                            tempDate.trimToSize();
                            tempWeatherIcon.trimToSize();

                            forecast = new String[tempForecast.size()];
                            descForecast = new String[tempDescForecast.size()];
                            subForecast = new String[tempSubForecast.size()];
                            pressure = new String[tempPressure.size()];
                            humidity = new String[tempHumidity.size()];
                            temperature = new String[tempTemperature.size()];
                            windSpeed = new String[tempWindSpeed.size()];
                            date = new String[tempDate.size()];
                            weatherIcon = new Integer[tempWeatherIcon.size()];

                            forecast = tempForecast.toArray(forecast);
                            descForecast = tempDescForecast.toArray(descForecast);
                            subForecast = tempSubForecast.toArray(subForecast);
                            pressure = tempPressure.toArray(pressure);
                            humidity = tempHumidity.toArray(humidity);
                            temperature = tempTemperature.toArray(temperature);
                            windSpeed = tempWindSpeed.toArray(windSpeed);
                            date = tempDate.toArray(date);
                            weatherIcon = tempWeatherIcon.toArray(weatherIcon);

                            //JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                            //morningTemp[newIndex] = "AM: ";// +
                            //String.valueOf(temperatureObject.getDouble(OWM_MORNING)) +
                            //getResources().getString(R.string.pref_unit_temperature);
                            //nightTemp[newIndex] = "PM: ";// +
                            //String.valueOf(temperatureObject.getDouble(OWM_NIGHT)) +
                            //getResources().getString(R.string.pref_unit_temperature);
                            //double high = temperatureObject.getDouble(OWM_MAX);
                            //double low = temperatureObject.getDouble(OWM_MIN);

                            highAndLow = "high, low";//formatHighLows(high, low);


                            dtDate = weatherArray.getJSONObject(i).getString("dt_txt").substring(0,10);
                            //newIndex++;
                        }

                    }
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
            int numDays = 5;

            try {
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" +
                //        location + "&cnt=" + numDays + "&units=" + units +
                //        "&appid=" + getResources().getString(R.string.weather_appid));

                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=" +
                        location + "&mode=" + format +
                        "&units=" + units + "&appid="+ getResources().getString(R.string.weather_appid));

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
            if(result == true) {
                if (forecast != null && forecast.length > 0 && !forecast[0].equals("")) {
                    try {
                        customAdapter = new SevenDayCustomAdapter(getContext(), forecast, descForecast, subForecast,
                                pressure, humidity, windSpeed, weatherIcon, temperature);
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
