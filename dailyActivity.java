package com.example.openweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class dailyActivity extends AppCompatActivity {

    private ArrayList<dailyWeather> dailyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        Intent intent = getIntent();
        String location = (String) intent.getSerializableExtra("name");
        setTitle(location);
        if (intent.hasExtra("dailyList")) {
            dailyList = (ArrayList<dailyWeather>) intent.getSerializableExtra("dailyList");
        }
        RecyclerView recyclerView = findViewById(R.id.dailyRecycler);
        dailyAdapter dailyadapter = new dailyAdapter(dailyList, this);
        dailyadapter.notifyDataSetChanged();
        recyclerView.setAdapter(dailyadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}