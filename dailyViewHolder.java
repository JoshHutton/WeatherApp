package com.example.openweatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class dailyViewHolder extends RecyclerView.ViewHolder{

    TextView wDaynTime, wHighLowTemp,wDescription,wPrec,wUVIndex,
            wMornTemp,wDayTimeTemp,wEveningTemp,wNightTemp;
    ImageView wIcon;
    dailyViewHolder(View view) {
        super(view);
        wIcon = view.findViewById(R.id.wIcon);
        wDaynTime = view.findViewById(R.id.wDaynTimeTV);
        wHighLowTemp = view.findViewById(R.id.wHighLowTempTV);
        wDescription = view.findViewById(R.id.wDescriptionTV);
        wPrec = view.findViewById(R.id.wPrecTV);
        wUVIndex = view.findViewById(R.id.wUVIndexTV);
        wMornTemp = view.findViewById(R.id.wMornTempTV);
        wDayTimeTemp = view.findViewById(R.id.wDayTimeTempTV);
        wEveningTemp = view.findViewById(R.id.wEveningTempTV);
        wNightTemp = view.findViewById(R.id.wNightTempTV);

    }
}
