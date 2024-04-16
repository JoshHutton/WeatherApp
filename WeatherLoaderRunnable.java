package com.example.openweatherapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class WeatherLoaderRunnable  implements Runnable {
    private static final String TAG = "WeatherLoaderRunnable";
    private final MainActivity mainActivity;
    private final boolean fahrenheit;
    private String currentTime;
    private final double lat;
    private final double lon;

    private static final String DATA_URL = "https://api.openweathermap.org/data/2.5/onecall?" +
            "exclude=minutely&appid=a534d9b21d8229a44e1646235fe9c16c";
    ArrayList<hourlyWeather> hourlyList = new ArrayList<>();
    ArrayList<dailyWeather> dailyList = new ArrayList<>();

    WeatherLoaderRunnable(MainActivity mainActivity, boolean fahrenheit, double lat, double lon) {
        this.mainActivity = mainActivity;
        this.fahrenheit = fahrenheit;
        this.lat = lat;
        this.lon = lon;
    }


    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        buildURL.appendQueryParameter("lat",String.valueOf(lat));
        buildURL.appendQueryParameter("lon",String.valueOf(lon));
        buildURL.appendQueryParameter("units", (fahrenheit ? "imperial" : "metric"));
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK" + conn.getResponseCode());
                handleResults(null);
                return;
            }
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    private void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            return;
        }

        final Weather w = parseJSON(s);
        final ArrayList<hourlyWeather> h = parseJSON2(s);
        final ArrayList<dailyWeather> d = parseJSON3(s);
        mainActivity.runOnUiThread(() -> mainActivity.updateData(w));
        mainActivity.runOnUiThread(() -> {
            assert h != null;
            mainActivity.updateHourly(h);
        });
        mainActivity.runOnUiThread(() -> mainActivity.updateDaily(d));


    }
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
    public String formatTemp(String s){
        double temp = Double.parseDouble(s);
        if (fahrenheit) {
            return String.format("%.0f", temp) + "°F";
        }else return String.format("%.0f", temp) + "°C";
    }
    public String timeFormat(String timeOffset, String dt){
        LocalDateTime ldt =
                LocalDateTime.ofEpochSecond(Long.parseLong(dt) + Long.parseLong(timeOffset), 0, ZoneOffset.UTC);
        DateTimeFormatter dtf =
                DateTimeFormatter.ofPattern("EEE MMM dd h:mm a, yyyy", Locale.getDefault());
        return ldt.format(dtf);
    }
    public String timeOnly(String timeOffset,String dt){
        LocalDateTime ldt =
                LocalDateTime.ofEpochSecond(Long.parseLong(dt) + Long.parseLong(timeOffset), 0, ZoneOffset.UTC);
        DateTimeFormatter dtf =
                DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());
        return ldt.format(dtf);
    }
    public String dayOnly(String dt, String timeOffset){
        LocalDateTime ldt =
                LocalDateTime.ofEpochSecond(Long.parseLong(dt) + Long.parseLong(timeOffset), 0, ZoneOffset.UTC);
        LocalDateTime currentldt =
                LocalDateTime.ofEpochSecond(Long.parseLong(currentTime) + Long.parseLong(timeOffset), 0, ZoneOffset.UTC);
        DateTimeFormatter currentdtf=
                DateTimeFormatter.ofPattern("EEEE", Locale.getDefault());
        DateTimeFormatter dtf =
                DateTimeFormatter.ofPattern("EEEE", Locale.getDefault());
        if (currentldt.format(currentdtf).equals(ldt.format(dtf))){
            return "Today";
        }
        else return ldt.format(dtf);
    }
    public String dailyFormat(String timeOffset, String dt){
        LocalDateTime ldt =
                LocalDateTime.ofEpochSecond(Long.parseLong(dt) + Long.parseLong(timeOffset), 0, ZoneOffset.UTC);
        DateTimeFormatter dtf =
                DateTimeFormatter.ofPattern("EEEE MM/dd", Locale.getDefault());
        return ldt.format(dtf);
    }

    private ArrayList<dailyWeather> parseJSON3(String s){
        try {
            JSONObject jObjMain = new JSONObject(s);
            String timeoffset = jObjMain.getString("timezone_offset");
            JSONArray daily = jObjMain.getJSONArray("daily");
            for (int i=0; i<7; i++) {
                JSONObject jDaily = (JSONObject) daily.get(i);

                String dTime = jDaily.getString("dt");
                JSONObject jdTemp = jDaily.getJSONObject("temp");
                String dHigh = jdTemp.getString("max");
                String dLow = jdTemp.getString("min");
                JSONArray jdWeather = jDaily.getJSONArray("weather");
                JSONObject jdw = (JSONObject) jdWeather.get(0);
                String dDescr = jdw.getString("description");
                String dIcon = jdw.getString("icon");
                Log.d(TAG, "parseJSON3: ICON" + dIcon);
                String dPrec = jDaily.getString("pop");
                String dUVI = jDaily.getString("uvi");
                String dMorn = jdTemp.getString("morn");
                String dDay = jdTemp.getString("day");
                String dEven = jdTemp.getString("eve");
                String dNight = jdTemp.getString("night");
                dailyList.add(new dailyWeather(dailyFormat(timeoffset,dTime),formatTemp(dHigh), formatTemp(dLow),
                        toTitleCase(dDescr), dPrec, dUVI, dIcon, formatTemp(dMorn), formatTemp(dDay), formatTemp(dEven), formatTemp(dNight)));
            } return dailyList;
        }catch (Exception e) {
            Log.d(TAG, "parseJSON:  " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<hourlyWeather> parseJSON2(String s){
        try{
            JSONObject jObjMain = new JSONObject(s);
            String timeoffset = jObjMain.getString("timezone_offset");
            JSONArray hourly = jObjMain.getJSONArray("hourly");
            Log.d(TAG, "parseJSON: Hourly" + hourly.toString());
            for (int i=0; i<48;i++){
                JSONObject jHourly = (JSONObject) hourly.get(i);

                String hTime = jHourly.getString("dt");
                String hTemp = jHourly.getString("temp");

                JSONArray hWeather = jHourly.getJSONArray("weather");
                JSONObject jhWeather = (JSONObject) hWeather.get(0);
                String hDescr = jhWeather.getString("description");
                String hIcon = jhWeather.getString("icon");
                hourlyList.add(new hourlyWeather(hTime,timeoffset,timeOnly(timeoffset,hTime),hIcon,formatTemp(hTemp),
                        toTitleCase(hDescr),dayOnly(hTime,timeoffset)));
            }

            return hourlyList;

        }catch (Exception e) {
            Log.d(TAG, "parseJSON:  " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Weather parseJSON(String s) {
        try {
        //current
            JSONObject jObjMain = new JSONObject(s);
            String lat = jObjMain.getString("lat");
            String lon = jObjMain.getString("lon");
            String location = jObjMain.getString("timezone");

            String timeoffset = jObjMain.getString("timezone_offset");
            // "weather" section
            JSONObject jCurrent = jObjMain.getJSONObject("current");
            currentTime = jCurrent.getString("dt");
            JSONArray weather = jCurrent.getJSONArray("weather");
            JSONObject jWeather = (JSONObject) weather.get(0);
            String description = jWeather.getString("description");
            String icon = jWeather.getString("icon");
            String temp = jCurrent.getString("temp");
            String feelsLike = jCurrent.getString("feels_like");
            String humidity = jCurrent.getString("humidity");
            String UVIndex = jCurrent.getString("uvi");
            String sunrise = jCurrent.getString("sunrise");
            String sunset = jCurrent.getString("sunset");
            String windSpeed = jCurrent.getString("wind_speed");
            String windDir = jCurrent.getString("wind_deg");
            String visibility = jCurrent.getString("visibility");

        //daily misc. stuff
            String dMorn, dDay,dEven,dNight;
            JSONArray daily = jObjMain.getJSONArray("daily");
                JSONObject jDaily = (JSONObject) daily.get(0);
                JSONObject jdTemp = jDaily.getJSONObject("temp");
                dMorn = jdTemp.getString("morn");
                dDay = jdTemp.getString("day");
                dEven = jdTemp.getString("eve");
                dNight = jdTemp.getString("night");

            return new Weather(location,lat,lon,timeFormat(timeoffset,currentTime),temp,feelsLike,humidity, UVIndex,
                    dMorn,dDay,dEven,dNight, timeOnly(timeoffset,sunrise),timeOnly(timeoffset,sunset),icon,
                    toTitleCase(description),windSpeed,windDir,visibility);

        } catch (Exception e) {
            Log.d(TAG, "parseJSON:  " + e.getMessage());
            e.printStackTrace();

        }
        return null;
    }

}












