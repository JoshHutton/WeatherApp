package com.example.openweatherapp;

import java.io.Serializable;

public class hourlyWeather implements Serializable {
    private final String totalTime;
    private final String timeZone;
    private final String time;
    private final String hIcon;
    private final String hTemp;
    private final String hDescr;
    private final String hDay;


    hourlyWeather (String tt,String tZ,String tm, String I, String tmp, String desc, String day){
        totalTime = tt + "000";timeZone=tZ; time = tm;  hIcon = I; hTemp = tmp; hDescr = desc;hDay = day;
    }
    public String getTimeZone() { return timeZone; }
    public String getTotalTime(){ return totalTime; }
    public String getTime(){return time;}
    public String gethIcon(){return hIcon;}
    public String gethTemp(){return hTemp;}
    public String gethDescr(){return hDescr;}
    public String gethDay(){return hDay;}





}
