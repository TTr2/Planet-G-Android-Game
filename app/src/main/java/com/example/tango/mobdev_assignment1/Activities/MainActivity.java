package com.example.tango.mobdev_assignment1.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tango.mobdev_assignment1.Fragments.Settings;
import com.example.tango.mobdev_assignment1.R;

public class MainActivity extends AppCompatActivity {

    public static String sessionStateFileName = "lastSession.txt";
    private static int deviceWidth;
    private static int deviceHeight;
    private Button playGameButton;
    private Button hiScoresButton;
    private Button planetStatsButton;
    private ImageButton settingsButton;

    /**
     * Called when activity os created.
     * @param savedInstanceState an instance saved state bundle.
     */
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
        this.settingsButton = (ImageButton) findViewById(R.id.SettingsButton);

        this.checkForSavedSession();

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
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(settingsIntent);

            }
        });
    }

    /**
     * Called when activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.checkForSavedSession();
    }

    /**
     * Checks the app's file directory for a saved session file and sets the play game text.
     */
    private void checkForSavedSession()
    {
        try {
            boolean savedSessionFound = false;
            String[] files = this.fileList();

            for (String filename : files)
            {
                if (filename.equals(MainActivity.sessionStateFileName))
                {
                    savedSessionFound = true;
                }
            }

            if (savedSessionFound)
            {
                this.playGameButton.setText("RESUME");
            }
            else
            {
                this.playGameButton.setText("NEW GAME");
            }
        }
        catch (Exception e) {
            Log.i("MainActivity", "No saved session state found.");
        }
    }
}
