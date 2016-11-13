package com.angelosoft.angelo_romel.weatherquote;

import java.io.Serializable;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class FavLocation implements Serializable {
    private String location;
    /*
    private String forecast7Days;
    private String[] forecast3Hours;
    */

    public FavLocation() {
        this.location = "";
        /*
        this.forecast7Days = "";
        this.forecast3Hours = new String[7];
        */
    }

    public FavLocation(String location) {
        this.location = location;
        /*
        this.forecast7Days = "";
        this.forecast3Hours = new String[7];
        */
    }

    public String getLocation() {
        return this.location;
    }

    /*
    public String getForecast7Days() {
        return this.forecast7Days;
    }

    public String getForecast3Hours(int position) {
        return this.forecast3Hours[position];
    }
    */

    public void setLocation(String location) {
        this.location = location;
    }

    /*
    public void setForecast7Days(String forecast7Days) {
        this.forecast7Days = forecast7Days;
    }

    public void setForecast3Hours(String forecast3Hours, int position) {
        this.forecast3Hours[position] = forecast3Hours;
    }
    */

}
