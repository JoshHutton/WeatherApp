package com.example.openweatherapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class hourlyAdapter extends RecyclerView.Adapter<hourlyViewHolder>{
    private final ArrayList<hourlyWeather> hourlyList;
    private final MainActivity mainAct;

    hourlyAdapter(ArrayList<hourlyWeather> hourlyList, MainActivity ma) {
            this.hourlyList = hourlyList;
            mainAct = ma;
        }

    private static final String TAG = "HourlyAdapter";

        @NonNull
        @Override
        public hourlyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {


            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.daily_weather_columns, parent, false);

            itemView.setOnClickListener(mainAct);

            return new hourlyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull hourlyViewHolder holder, int position) {
            hourlyWeather hour = hourlyList.get(position);
            Log.d(TAG, "onBindViewHolder: " + hour.toString());
            holder.tempTV.setText(hour.gethTemp());
            holder.timeTV.setText(hour.getTime());
            String iconCode = "_" + hour.gethIcon();
            int iconResId = mainAct.getResources().getIdentifier(iconCode, "drawable", mainAct.getPackageName());
            holder.weatherIconView.setImageResource(iconResId);
            holder.descriptionTV.setText(hour.gethDescr());
            holder.DayTV.setText(hour.gethDay());
        }

        @Override
        public int getItemCount() {
            return hourlyList.size();
        }

}
