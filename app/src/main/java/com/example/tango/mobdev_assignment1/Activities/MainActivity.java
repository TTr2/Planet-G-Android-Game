package com.example.tango.mobdev_assignment1.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tango.mobdev_assignment1.R;

public class MainActivity extends AppCompatActivity {

    private static int deviceWidth;
    private static int deviceHeight;
    private Button newGameButton;
    private Button hiScoresButton;
    private Button planetStatsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newGameButton = (Button) findViewById(R.id.NewGameButton);
        hiScoresButton = (Button) findViewById(R.id.HiScoresButton);
        planetStatsButton = (Button) findViewById(R.id.PlanetStatsButton);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGameIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(newGameIntent);
            }
        });
        hiScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hiScoresIntent = new Intent(MainActivity.this, HiScoresActivity.class);
                startActivity(hiScoresIntent);
            }
        });
        planetStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent planetStatsIntent = new Intent(MainActivity.this, PlanetStatsActivity.class);
                startActivity(planetStatsIntent);
            }
        });
    }
}
