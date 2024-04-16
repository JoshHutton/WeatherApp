package com.example.openweatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class dailyAdapter extends RecyclerView.Adapter<dailyViewHolder>{

    private final ArrayList<dailyWeather> dailyList;
    private final dailyActivity dailyAct;


    dailyAdapter(ArrayList<dailyWeather> dailyList, dailyActivity da){
        this.dailyList = dailyList;

        this.dailyAct = da;

    }
    @NonNull
    @Override
    public dailyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_weather_rows, parent, false);
        return new dailyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull dailyViewHolder holder, int position) {
        double temp;

        dailyWeather day = dailyList.get(position);

        String iconCode = day.getdIcon();
        iconCode = "_" + iconCode;
        int iconResId = dailyAct.getResources()
                .getIdentifier(iconCode, "drawable", dailyAct.getPackageName());
        holder.wIcon.setImageResource(iconResId);

        holder.wDaynTime .setText(" " + day.getDtime());
        holder.wHighLowTemp.setText(day.getdHigh() + "/" + day.getdLow());
        holder.wDescription.setText(day.getdDescr());
        temp = Double.parseDouble(day.getdPrec()) * 100;
        holder.wPrec.setText("(" + String.format("%.0f",temp) + "% precip.)");
        temp = Double.parseDouble(day.getdUVI());
        holder.wUVIndex.setText("UV Index: " + String.format("%.0f", temp));
        holder.wMornTemp.setText(day.getdMorn());
        holder.wDayTimeTemp.setText(day.getdDay());
        holder.wEveningTemp.setText(day.getdEven());
        holder.wNightTemp.setText(day.getdNight());
    }


    @Override
    public int getItemCount() {
        return dailyList.size();
    }

}



