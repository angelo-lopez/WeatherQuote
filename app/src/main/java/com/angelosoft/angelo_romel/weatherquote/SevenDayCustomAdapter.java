package com.angelosoft.angelo_romel.weatherquote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class SevenDayCustomAdapter extends ArrayAdapter<String> {

    Context context;
    String[] forecast;
    String[] descForecast;
    String[] subForecast;
    String[] pressure;
    String[] humidity;
    String[] windSpeed;
    String[] temperature;
    Integer[] weatherIcon;

    public SevenDayCustomAdapter(Context c, String[] forecast, String[] descForecast, String[] subForecast,
                                 String[] pressure, String[] humidity, String[] windSpeed, Integer[] weatherIcon,
                                 String[] temperature){
        super(c, R.layout.list_item_forecast, R.id.list_item_forecast_textview, forecast);
        context = c;
        this.forecast = forecast;
        this.descForecast = descForecast;
        this.subForecast = subForecast;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_forecast, parent, false);
        ImageView weatherImage = (ImageView) row.findViewById(R.id.image_view);
        TextView forecast = (TextView) row.findViewById(R.id.list_item_forecast_textview);
        TextView descForecast = (TextView) row.findViewById(R.id.list_item_desc_forecast_textview);
        TextView subForecast = (TextView) row.findViewById(R.id.list_item_subforecast_textview);
        TextView pressure = (TextView) row.findViewById(R.id.list_item_pressure_textview);
        TextView humidity = (TextView) row.findViewById(R.id.list_item_humidity_textview);
        TextView windSpeed = (TextView) row.findViewById(R.id.list_item_wind_textview);
        TextView temperature = (TextView) row.findViewById(R.id.list_item_temperature_textview);

        weatherImage.setImageResource(weatherIcon[position]);
        forecast.setText(this.forecast[position]);
        descForecast.setText(this.descForecast[position]);
        subForecast.setText(this.subForecast[position]);
        pressure.setText(this.pressure[position]);
        humidity.setText(this.humidity[position]);
        windSpeed.setText(this.windSpeed[position]);
        temperature.setText(this.temperature[position]);

        return row;
    }

}