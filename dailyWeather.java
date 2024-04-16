package com.example.openweatherapp;

import java.io.Serializable;

public class dailyWeather implements Serializable {

    private final String dtime;
    private final String dHigh;
    private final String dLow;
    private final String dDescr;
    private final String dPrec;
    private final String dUVI;
    private final String dIcon;
    private final String dMorn;
    private final String dDay;
    private final String dEven;
    private final String dNight;

    dailyWeather (String dt, String dh, String dl, String dd, String dp, String du,
        String di, String dm,String ddy, String de, String dn){
        dtime=dt;dHigh=dh;dLow=dl;dDescr=dd;dPrec=dp;dUVI=du;dIcon=di;dMorn=dm;dDay=ddy;dEven=de;dNight=dn;
    }
    public String getDtime(){return dtime;}
    public String getdHigh() { return dHigh; }
    public String getdLow() { return dLow; }
    public String getdDescr() { return dDescr; }
    public String getdPrec() { return dPrec; }
    public String getdUVI() { return dUVI; }
    public String getdIcon() { return dIcon; }
    public String getdMorn() { return dMorn; }
    public String getdDay() { return dDay; }
    public String getdEven() { return dEven; }
    public String getdNight() { return dNight; }
}
