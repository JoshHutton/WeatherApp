package com.example.openweatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean fahrenheit = true;
    private static final String TAG = "MainActivity :";
    private double lat;
    private double lon;
    private String location;
    private SwipeRefreshLayout swiper;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;


    private ArrayList<hourlyWeather> hourlyList = new ArrayList<>();
    private ArrayList<dailyWeather> dailyList = new ArrayList<>();
    private hourlyAdapter hourlyadapter;
    Menu menu;
    TextView geoCode, currentDate;
    String symFar = "°F";
    String symCel = "°C";
    String finalSym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swiper = findViewById(R.id.swiper);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        if (!sharedPref.contains("FAHRENHEIT")){
            editor.putBoolean("FAHRENHEIT", true);
            editor.apply();
        }
        if (!sharedPref.contains("LAT")){
            editor.putString("LAT","41.8675766");
        }if (!sharedPref.contains("LON")){
            editor.putString("LON","-87.616232");
        }if (!sharedPref.contains("LOCATION")){
            editor.putString("LOCATION","Chicago, Illinois");
        }
        fahrenheit = sharedPref.getBoolean("FAHRENHEIT",true);
        lat = Double.parseDouble(sharedPref.getString("LAT","41.8675766"));
        lon = Double.parseDouble(sharedPref.getString("LON","-87.616232"));
        location = sharedPref.getString("LOCATION", "Chicago, Illinois");
        if (fahrenheit){
            finalSym = symFar;
        }else {
            finalSym = symCel;
        }
        recyclerView = findViewById(R.id.recycler48hour);
        hourlyadapter = new hourlyAdapter(hourlyList, this);

        geoCode = findViewById(R.id.geocodeTV);
        currentDate = findViewById(R.id.currentDateTV);

        if (hasNetworkConnection()) {
            doDownload();
            recyclerView.setAdapter(hourlyadapter);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    doDownload();
                    swiper.setRefreshing(false);
                }
            });
        }else {
            currentDate.setText("No internet connection");
        }


    }

    private void doDownload(){
        WeatherLoaderRunnable loaderTaskRunnable = new WeatherLoaderRunnable(this,fahrenheit,lat,lon);
        new Thread(loaderTaskRunnable).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (fahrenheit){
            menu.getItem(0).setIcon(R.drawable.units_f);
        }else {
            menu.getItem(0).setIcon(R.drawable.units_c);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!hasNetworkConnection()){
            Toast.makeText(this, "Internet Connection Needed", Toast.LENGTH_SHORT).show();
            return false;
        }else {
        if (item.getItemId() == R.id.changeL){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Create an edittext and set it to be the builder's view
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);
            builder.setPositiveButton("OK", (dialog, id) -> {
                Log.d(TAG, "onClick: " + getLocationName(et.getText().toString()));
                try {
                    String temp = location;
                    String delims = "[,]";
                    location = getLocationName(et.getText().toString());
                    String[] tokens = location.split(delims);
                    if (tokens[0].equals("null")){
                        location = temp;
                        throw new IllegalArgumentException("");
                    }
                    getLatLon(getLocationName(et.getText().toString()));
                    editor.putString("LOCATION", location);
                    editor.putString("LAT",String.valueOf(lat));
                    editor.putString("LON",String.valueOf(lon));
                    editor.apply();
                    doDownload();
                } catch (IllegalArgumentException i) {
                    Toast.makeText(this, "Try Again: Enter Valid Location", Toast.LENGTH_SHORT).show();
                }

            }).setNegativeButton("Cancel", (dialog, id) -> {
            });
            builder.setMessage("For US locations, enter as 'City', or " +
                    "'City,\nState' \n \n" + "For international locations enter " +
                    "as 'City, \nCountry' ");
            builder.setTitle("Enter a Location");
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
        if (item.getItemId() == R.id.dailyForecast) {
            Intent intent = new Intent(MainActivity.this, dailyActivity.class);
            intent.putExtra("name", location);
            intent.putExtra("dailyList", dailyList);
            Log.d(TAG, "onOptionsItemSelected: " + dailyList.get(0).getdHigh());
            startActivity(intent);
            return true;

        }else if (item.getItemId() == R.id.unitChange) {
            if (fahrenheit) {
                fahrenheit = false;
                finalSym= symCel;
                editor.putBoolean("FAHRENHEIT", false);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.units_c));
            }else {
                fahrenheit = true;
                finalSym = symFar;
                editor.putBoolean("FAHRENHEIT", true);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.units_f));
            }
            editor.apply();
            doDownload();
            return false;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }}

    public String roundDouble(String s){
        double temp = Double.parseDouble(s);
        return String.format("%.0f",temp);
    }

    private String getDirection(String windDirection) {
        double degrees = Double.parseDouble(windDirection);
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }

    private double[] getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address =
                    geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty()) {
                // Nothing returned!
                return null;
            }
            lat = address.get(0).getLatitude();
            lon = address.get(0).getLongitude();

            return new double[] {lat, lon};
        } catch (IOException e) {
            // Failure to get an Address object
            return null;
        }
    }
    private String getLocationName(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address =
                    geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty()) {
                // Nothing returned!
                return null;
            }
            String country = address.get(0).getCountryCode();
            String p1;
            String p2;

            if (country.equals("US")) {
                p1 = address.get(0).getLocality();
                p2 = address.get(0).getAdminArea();
            } else {
                p1 = address.get(0).getLocality();

                if (p1 == null)
                    p1 = address.get(0).getSubAdminArea();
                p2 = address.get(0).getCountryName();
                }

            return p1 + ", " + p2;
        } catch (IOException e) {
            // Failure to get an Address object
            return null;
        }catch (NullPointerException n){
            return null;
        }
    }

    private boolean hasNetworkConnection(){
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }


    public void updateHourly(ArrayList <hourlyWeather> h){

        hourlyList.clear();
        hourlyList.addAll(h);
        hourlyadapter.notifyDataSetChanged();
    }
    public void updateDaily(ArrayList <dailyWeather> d){
        dailyList.clear();
        dailyList.addAll(d);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void updateData(Weather weather){
        if (weather == null) {
            Toast.makeText(this, "Please Enter a Valid City Name", Toast.LENGTH_SHORT).show();
        }else {

            currentDate.setText(weather.getTime());
            geoCode.setText(location);
            TextView mornTime = findViewById(R.id.mornTimeTV);
            mornTime.setText("8am");
            TextView dayTime = findViewById(R.id.daytimeTimeTV);
            dayTime.setText("1pm");
            TextView evenTime = findViewById(R.id.eveningTimeTV);
            evenTime.setText("5pm");
            TextView nightTime = findViewById(R.id.nightTimeTV);
            nightTime.setText("11pm");

            TextView mainTemp = findViewById(R.id.mainTempTV);
            mainTemp.setText(roundDouble(weather.getTemp()) + finalSym);

            TextView feelsLike = findViewById(R.id.feelsLikeTV);
            feelsLike.setText("Feels Like " + roundDouble(weather.getFeelsLike()) + finalSym);

            TextView humidity = findViewById(R.id.humidityTV);
            humidity.setText("Humidity: " + weather.getHumidity() + "%");

            TextView uvindex = findViewById(R.id.uvIndexTV);
            uvindex.setText("UV Index: " + roundDouble(weather.getUVindex()));

            TextView mornTemp = findViewById(R.id.mornTempTV);
            mornTemp.setText(roundDouble(weather.getMornTemp()) + finalSym);

            TextView dayTemp = findViewById(R.id.daytimeTempTV);
            dayTemp.setText(roundDouble(weather.getDaytTemp()) + finalSym);

            TextView evenTemp = findViewById(R.id.eveningTempTV);
            evenTemp.setText(roundDouble(weather.getEvenTemp()) + finalSym);

            TextView nightTemp = findViewById(R.id.nightTempTV);
            nightTemp.setText(roundDouble(weather.getNightTemp()) + finalSym);

            TextView sunrise = findViewById(R.id.sunriseTV);
            sunrise.setText("Sunrise: " + weather.getSunrise());


            TextView sunset = findViewById(R.id.sunsetTV);
            sunset.setText("Sunset: "+ weather.getSunset());

            ImageView icons = findViewById(R.id.mainWeatherIcon);
            String iconCode = "_" + weather.getWeatherIcon();

            int iconResId = getResources().getIdentifier(iconCode, "drawable", getPackageName());
            icons.setImageResource(iconResId);


            TextView description = findViewById(R.id.mainDescrTV);

            description.setText(weather.getWeatherDesc());

            TextView wind = findViewById(R.id.windTV);
            if (fahrenheit) {
                wind.setText("Winds: " + getDirection(weather.getWindDirection()) +
                        " at " + roundDouble(weather.getWindSpeed()) + " mph");
            }else {wind.setText("Winds: " + getDirection(weather.getWindDirection()) +
                    " at " + roundDouble(weather.getWindSpeed()) + " m/s");}

            TextView visibility = findViewById(R.id.visibilityTV);
            if (fahrenheit){
                double tempVis = Double.parseDouble(weather.getVisibility())/1600;
                visibility.setText("Visibility: " +  String.format("%.1f",tempVis) + " mi");
            }else {
                double tempVis = Double.parseDouble(weather.getVisibility())/1000;
                visibility.setText("Visibility: " +  String.format("%.1f",tempVis) + " Km");
            }
        }
    }

    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        long temp = Long.parseLong(hourlyList.get(pos).getTotalTime());
        temp += Integer.parseInt(hourlyList.get(pos).getTimeZone()) + 60*100;
        intent.putExtra("beginTime", temp+60*1000);
        intent.putExtra("allDay", false);
        intent.putExtra("endTime", temp+60*61*1000);
        startActivity(intent);
    }

}