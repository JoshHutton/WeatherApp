package com.example.openweatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class hourlyViewHolder extends RecyclerView.ViewHolder {


    TextView DayTV,timeTV,tempTV,descriptionTV;
    ImageView weatherIconView;

    hourlyViewHolder(View view) {
        super(view);
        DayTV = view.findViewById(R.id.DayTV);
        timeTV = view.findViewById(R.id.timeTV);
        weatherIconView = view.findViewById(R.id.weatherIconView);
        tempTV = view.findViewById(R.id.tempTV);
        descriptionTV = view.findViewById(R.id.descriptionTV);
    }
}
