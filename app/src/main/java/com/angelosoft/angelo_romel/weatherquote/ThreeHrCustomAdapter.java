package com.angelosoft.angelo_romel.weatherquote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by angelo_romel on 05/05/2016.
 */
public class ThreeHrCustomAdapter extends ArrayAdapter<String> {
    Context context;
    String[] forecast;
    String[] descForecast;
    Integer[] weatherIcon;
    String[] temperature;
    String[] windSpeed;
    String[] windDirection;
    String[] humidityClouds;


    public ThreeHrCustomAdapter(Context c, String[] forecast, String[] descForecast,
                                Integer[] weatherIcon, String[] temperature, String[] windSpeed, String[] windDirection,
                                String[] humidityClouds){
        super(c, R.layout.list_item_forecast3, R.id.list_item_forecast_textview, forecast);
        context = c;
        this.forecast = forecast;
        this.descForecast = descForecast;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidityClouds = humidityClouds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_forecast3, parent, false);
        ImageView weatherImage = (ImageView) row.findViewById(R.id.image_view);
        TextView forecast = (TextView) row.findViewById(R.id.list_item_forecast_textview);
        TextView descForecast = (TextView) row.findViewById(R.id.list_item_desc_forecast_textview);
        TextView temperature = (TextView) row.findViewById(R.id.list_item_temp_forecast_textview);
        TextView windSpeed = (TextView) row.findViewById(R.id.list_item_wind_speed_forecast_textview);
        TextView windDirection = (TextView) row.findViewById(R.id.list_item_wind_direction_forecast_textview);
        TextView humidityClouds = (TextView) row.findViewById(R.id.list_item_humidity_clouds_forecast_textview);

        weatherImage.setImageResource(weatherIcon[position]);
        forecast.setText(this.forecast[position]);
        descForecast.setText(this.descForecast[position]);
        temperature.setText(this.temperature[position]);
        windSpeed.setText(this.windSpeed[position]);
        windDirection.setText(this.windDirection[position]);
        humidityClouds.setText(this.humidityClouds[position]);

        return row;
    }
}
