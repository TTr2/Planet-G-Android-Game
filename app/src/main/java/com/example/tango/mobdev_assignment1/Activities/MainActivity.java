package com.example.tango.mobdev_assignment1.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tango.mobdev_assignment1.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static String sessionStateFileName = "lastSession.txt";
    private static int deviceWidth;
    private static int deviceHeight;
    private Button playGameButton;
    private Button hiScoresButton;
    private Button planetStatsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // So finishing this will get us to the last viewed activity
            finish();
            return;
        }

        this.setContentView(R.layout.activity_main);
        this.playGameButton = (Button) findViewById(R.id.PlayGameButton);
        this.hiScoresButton = (Button) findViewById(R.id.HiScoresButton);
        this.planetStatsButton = (Button) findViewById(R.id.PlanetStatsButton);

        try {
            String[] files = this.fileList();
            for (String filename : files)
            {
                if (filename == MainActivity.sessionStateFileName)
                {
                    this.playGameButton.setText("RESUME");
                }
            }
        }
        catch (Exception e) {
            Log.i("MainActivity", "No saved session state found.");
        }

        playGameButton.setOnClickListener(new View.OnClickListener() {
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
