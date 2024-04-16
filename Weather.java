package com.example.openweatherapp;

import java.io.Serializable;



public class Weather implements Serializable {
    private final String location;
    private final String lat;
    private final String lon;
    private final String time;

    private final String temp;
    private final String feelsLike;
    private final String humidity;
    private final String UVindex;
    private final String mornTemp;
    private final String daytTemp;
    private final String evenTemp;
    private final String nightTemp;
    private final String sunrise;
    private final String sunset;
    private final String weatherIcon;
    private final String weatherDesc;
    private final String windSpeed;
    private final String windDirection;
    private final String visibility;



    Weather (String locat,String la, String lo,String tm, String t, String fl, String h, String UV, String mT, String dT,
        String eT, String nT, String sr, String ss, String wI, String wD, String wndSd,String wndD, String v) {
        location = locat;lat = la; lon = lo; time = tm; temp = t;   feelsLike = fl;     humidity = h;   UVindex = UV;   mornTemp = mT;  daytTemp = dT;
        evenTemp = eT;  nightTemp = nT; sunrise = sr; sunset = ss; weatherIcon = wI; weatherDesc = wD;
        windSpeed = wndSd; windDirection=wndD; visibility=v;
    }
    public String getLocation(){return location;}
    public String getLat(){return lat;}
    public String getLon(){return lon;}
    public String getTime(){return time;}
    public String getTemp(){ return temp;}
    public String getFeelsLike() { return feelsLike; }
    public String getHumidity(){ return humidity; }
    public String getUVindex () { return UVindex; }
    public String getMornTemp(){ return mornTemp; }

    public String getDaytTemp(){ return daytTemp; }
    public String getEvenTemp(){ return evenTemp; }
    public String getNightTemp(){ return nightTemp; }
    public String getSunrise(){ return sunrise; }
    public String getSunset(){ return sunset; }
    public String getWeatherIcon(){ return weatherIcon; }
    public String getWeatherDesc() { return weatherDesc; }
    public String getWindSpeed(){ return windSpeed; }
    public String getWindDirection(){return windDirection;}

    public String getVisibility(){ return visibility; }

}
