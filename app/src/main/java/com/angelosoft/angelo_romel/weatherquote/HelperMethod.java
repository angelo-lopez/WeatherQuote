package com.angelosoft.angelo_romel.weatherquote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class HelperMethod {

    public HelperMethod() {
        super();
    }

    public static void displayMessage(String message, Context c) {
        AlertDialog.Builder alertDiag = new AlertDialog.Builder(c);
        alertDiag.setMessage(message);
        alertDiag.setTitle("WeatherQuote");
        alertDiag.setPositiveButton("OK", null);
        alertDiag.setCancelable(true);
        alertDiag.create().show();
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.
                matcher(email).matches();
    }

    public static boolean isLoggedIn(Context c) {
        Firebase mFireRef = new Firebase(c.getResources().getString(R.string.firebase_url));
        AuthData auth = mFireRef.getAuth();
        if(auth != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public static int getIconResourceValue(String weatherIcon) {
        int imageRes = R.drawable.ic_clear;
        switch (weatherIcon) {
            case "01d":
                imageRes = R.drawable.ic_clear;
                break;
            case "01n":
                imageRes = R.drawable.ic_clear_eve;
                break;
            case "02d":
                imageRes = R.drawable.ic_clear_cloud;
                break;
            case "02n":
                imageRes = R.drawable.ic_clear_eve;
                break;
            case "03d":
                imageRes = R.drawable.ic_cloud;
                break;
            case "03n":
                imageRes = R.drawable.ic_clear_eve;
                break;
            case "04d":
                imageRes = R.drawable.ic_cloud;
                break;
            case "04n":
                imageRes = R.drawable.ic_clear_eve;
                break;
            case "09d":
                imageRes = R.drawable.ic_rain;
                break;
            case "09n":
                imageRes = R.drawable.ic_rain_eve;
                break;
            case "10d":
                imageRes = R.drawable.ic_rain;
                break;
            case "10n":
                imageRes = R.drawable.ic_rain_eve;
                break;
            case "11d":
                imageRes = R.drawable.ic_storm;
                break;
            case "11n":
                imageRes = R.drawable.ic_storm_eve;
                break;
            case "13d":
                imageRes = R.drawable.ic_snow;
                break;
            case "13n":
                imageRes = R.drawable.ic_snow_eve;
                break;
            case "50d":
                imageRes = R.drawable.ic_mist;
                break;
            case "50n":
                imageRes = R.drawable.ic_mist_eve;
                break;
        }
        return imageRes;
    }

    public static int getWeatherAnimationResourceValue(String weatherIcon) {
        int animRes =  R.drawable.clear_weather_animation;
        switch (weatherIcon) {
            case "01d":
                animRes = R.drawable.clear_weather_animation;
                break;
            case "01n":
                animRes = R.drawable.clear_eve_weather_animation;
                break;
            case "02d":
                animRes = R.drawable.clear_cloud_weather_animation;
                break;
            case "02n":
                animRes = R.drawable.clear_cloud_eve_weather_animation;
                break;
            case "03d":
                animRes = R.drawable.cloud_weather_animation;
                break;
            case "03n":
                animRes = R.drawable.cloud_eve_weather_animation;
                break;
            case "04d":
                animRes = R.drawable.cloud_weather_animation;
                break;
            case "04n":
                animRes = R.drawable.cloud_eve_weather_animation;
                break;
            case "09d":
                animRes = R.drawable.rain_weather_animation;
                break;
            case "09n":
                animRes = R.drawable.rain_eve_weather_animation;
                break;
            case "10d":
                animRes = R.drawable.rain_weather_animation;
                break;
            case "10n":
                animRes = R.drawable.rain_eve_weather_animation;
                break;
            case "11d":
                animRes = R.drawable.storm_weather_animation;
                break;
            case "11n":
                animRes = R.drawable.storm_eve_weather_animation;
                break;
            case "13d":
                animRes = R.drawable.snow_weather_animation;
                break;
            case "13n":
                animRes = R.drawable.snow_eve_weather_animation;
                break;
            case "50d":
                animRes = R.drawable.mist_weather_animation;
                break;
            case "50n":
                animRes = R.drawable.mist_eve_weather_animation;
                break;
        }
        return animRes;
    }

    public static String getIconResourceValueFromTime(String weatherIcon, String time) {
        String newIcon = "";
        switch(time) {
            case "00:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "01:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "02:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "03:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "04:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "05:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "06:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "07:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "08:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "09:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "10:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "11:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "12:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "13:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "14:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "15:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "16:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "17:00":
                newIcon = weatherIcon.substring(0, 2) + "d";
                break;
            case "18:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "19:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "20:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "21:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "22:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "23:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
            case "24:00":
                newIcon = weatherIcon.substring(0, 2) + "n";
                break;
        }
        return newIcon;
    }

    public static int getHeaderBackgroundResource(int value) {
        int backgroundResource = R.drawable.material_appbar_image_city_1;;
        switch(value) {
            case 0:
                backgroundResource = R.drawable.material_appbar_image_city_1;
                break;
            case 1:
                backgroundResource = R.drawable.material_appbar_image_city_2;
                break;
            case 2:
                backgroundResource = R.drawable.material_appbar_image_city_3;
                break;
            case 3:
                backgroundResource = R.drawable.material_appbar_image_city_4;
                break;
            case 4:
                backgroundResource = R.drawable.material_appbar_image_city_5;
                break;
            case 5:
                backgroundResource = R.drawable.material_appbar_image_city_6;
                break;
            case 6:
                backgroundResource = R.drawable.material_appbar_image_city_7;
                break;
            case 7:
                backgroundResource = R.drawable.material_appbar_image_city_8;
                break;
            case 8:
                backgroundResource = R.drawable.material_appbar_image_city_9;
                break;
            case 9:
                backgroundResource = R.drawable.material_appbar_image_city_10;
                break;
            case 10:
                backgroundResource = R.drawable.material_appbar_image_city_11;
                break;
            case 11:
                backgroundResource = R.drawable.material_appbar_image_nature_1;
                break;
            case 12:
                backgroundResource = R.drawable.material_appbar_image_nature_2;
                break;
            case 13:
                backgroundResource = R.drawable.material_appbar_image_nature_3;
                break;
            case 14:
                backgroundResource = R.drawable.material_appbar_image_nature_4;
                break;
            case 15:
                backgroundResource = R.drawable.material_appbar_image_nature_5;
                break;
            case 16:
                backgroundResource = R.drawable.material_appbar_image_nature_6;
                break;
            case 17:
                backgroundResource = R.drawable.material_appbar_image_nature_7;
                break;
            case 18:
                backgroundResource = R.drawable.material_appbar_image_nature_8;
                break;
            case 19:
                backgroundResource = R.drawable.material_appbar_image_nature_9;
                break;
            case 20:
                backgroundResource = R.drawable.material_appbar_image_nature_10;
                break;
            case 21:
                backgroundResource = R.drawable.material_appbar_image_nature_11;
                break;
            case 22:
                backgroundResource = R.drawable.material_appbar_image_nature_12;
                break;
        }
        return backgroundResource;
    }

    public static int getRandomHeaderBackgroundResource(Context c) {
        SecureRandom randomNum = new SecureRandom();
        return getHeaderBackgroundResource(randomNum.nextInt(22));
    }

    public static String getRandomQuote(Context c) {
        String[] quotes = c.getResources().getStringArray(R.array.weather_quotes);
        SecureRandom randomNum = new SecureRandom();

        return quotes[randomNum.nextInt(quotes.length - 1)];
    }

    public static void openPreferredLocationInMap(Context c) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(c);

        Uri geolocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", prefs.getString("selectedPlace", "")).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geolocation);

        if (intent.resolveActivity(c.getPackageManager()) != null) {
            c.startActivity(intent);
        }
    }

    public static String getReadableDateISOString(int time) {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lTime = time;
        return isoDateFormat.format(lTime * 1000);
    }

    public static String getReadableDayString(int time){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEEE");
        long lTime = time;
        return shortenedDateFormat.format(lTime * 1000);
    }

    public static String getReadableDateString(int time){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lTime = time;
        return shortenedDateFormat.format(lTime * 1000);
    }

    public static String getReadableDateNameString(int time) {
        SimpleDateFormat nameDateFormat = new SimpleDateFormat("MMM dd',' yyyy");
        long lTime = time;
        return nameDateFormat.format(lTime * 1000);
    }

}
