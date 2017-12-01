package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.tango.mobdev_assignment1.Activities.GameActivity;
import com.example.tango.mobdev_assignment1.Activities.MainActivity;
import com.example.tango.mobdev_assignment1.R;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;

/**
 * The GameViewModel class implements the MVVM pattern to abstract the view and sensor input
 * implementation from the GameController; view operations are performed on the UI main thread.
 * Created by tango on 24/11/2017.
 */

public class GameViewModel {



    private float ax_Roll = 0;
    private float ay_Pitch = 0;
    private float az_Yaw = 0;
    private float min_az_Yaw;
    private float max_az_Yaw;
    private float gx_Roll = 0;
    private float gy_Pitch = 0;
    private float gz_Yaw = 0;
    private float min_gx_Roll;
    private float max_gx_Roll;
    private GameActivity activity;
    private TextView levelText;
    private TextView scoreText;
    private TextView nextPlanetText;
    private ImageView nextPlanetImage;
    private ImageView gameOverImage;
    private GridLayout gameDetailsGrid;
    private RelativeLayout gameLayout;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private SensorEventListener accelerometerEventListener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                ax_Roll = event.values[0];
                ay_Pitch = event.values[1];
                az_Yaw = event.values[2];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
    private SensorEventListener gyroscopeEventListener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                gx_Roll = event.values[0];
                gy_Pitch = event.values[1];
                gz_Yaw = event.values[2];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    /**
     * Constructor for GameViewModel class that abstracts the view implementation.
     * @param context the level text view
     */
    public GameViewModel(GameActivity context)
    {
        this.activity = context;
        this.scoreText = (TextView) context.findViewById(R.id.ScoreText);
        this.levelText = (TextView) context.findViewById(R.id.LevelText);
        this.nextPlanetText = (TextView) context.findViewById(R.id.NextPlanetNameText);
        this.nextPlanetImage = (ImageView) context.findViewById(R.id.NextPlanetImage);
        this.gameOverImage = (ImageView) context.findViewById(R.id.GameOverImage);
        this.gameDetailsGrid = (GridLayout) context.findViewById(R.id.GameDetailsGridLayout);
        this.gameLayout = (RelativeLayout) context.findViewById(R.id.GameLayout);
        SensorManager sensorManager = (SensorManager) this.activity.getSystemService(SENSOR_SERVICE);

        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.min_az_Yaw = -1 * this.accelerometer.getMaximumRange();
        this.max_az_Yaw = this.accelerometer.getMaximumRange();
        sensorManager.registerListener(accelerometerEventListener, this.accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        this.gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.min_gx_Roll = -1 * this.gyroscope.getMaximumRange();
        this.max_gx_Roll = this.gyroscope.getMaximumRange();
        sensorManager.registerListener(gyroscopeEventListener, this.gyroscope,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Draws the game board images on the GameSurfaceView canvas.
     */
    protected void drawGameBoard(final GameBoard gameBoard)
    {
        this.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            // Draw all blocks on game board
            for (int column = 0; column < gameBoard.getColumns(); column++)
            {
                for (int row = 0; row < gameBoard.getRows(); row++)
                {
                    if (gameBoard.getCell(column, row).isOccupied())
                    {
                        gameBoard.getCell(column, row).getImageView().invalidate();
                    }
                }
            }
            }
        });
    }

    /**
     * Redraws an individual block on the game board.
     * @param cell the GameBoardCell to extract the image and x,y coordinates to redraw the image at.
     */
    protected void drawIndividualImage(final GameBoardCell cell)
    {
        final ImageView tmpImageView = cell.getImageView();
        this.activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tmpImageView.setX(cell.getImageX());
                tmpImageView.setY(cell.getImageY());
                tmpImageView.invalidate();
            }
        });
    }

    /**
     * Adds an image view to the game layout.
     * @param imageView the new image to add to layout.
     */
    protected void addImageViewToGameLayout(final ImageView imageView)
    {
         this.activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                gameLayout.addView(imageView);
                imageView.invalidate();
            }
        });
    }

    /**
     * Removes an image view from the game layout.
     * @param imageView the image to remove from the game layout.
     */
    public void removeImageViewFromGameLayout(final ImageView imageView)
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameLayout.removeView(imageView);
                gameLayout.invalidate();
            }
        });
    }

    /**
     * Updates the score TextView with latest score from game session.
     * @param score the new score to display as formatted string.
     */
    protected void setScoreText(final long score)
    {
        String str = String.format(Locale.ENGLISH, "%08d", score);
        final String scoreStr = str.substring(0,2) + "," + str.substring(2,5) + "," + str.substring(5,8);
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreText.setText(scoreStr);
                scoreText.invalidate();
            }
        });
    }

    /**
     * Updates the game level TextView with latest level from game session.
     * @param levelTextStr the new level text
     */
    protected void setLevelText(final String levelTextStr)
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                levelText.setText(levelTextStr);
                levelText.invalidate();
            }
        });
    }

    /**
     * Sets the name and image of the next planet display.
     * @param nextPlanet the next planet.
     */
    protected void setNextPlanet(PlanetsEnum nextPlanet)
    {
        this.setNextPlanetText(nextPlanet.name());
        this.setNextPlanetImage(nextPlanet.getImageResource());
    }

    /**
     * Sets the next planet name.
     * @return the next planet name.
     */
    protected void setNextPlanetText(final String nextPlanetName)
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextPlanetText.setText(nextPlanetName);
                nextPlanetText.invalidate();
            }
        });
    }

    /**
     * Updates the next Planet ImageView with image of next Planet to drop.
     * @param nextPlanetDrawableId the drawable id of the nxt planet image.
     */
    protected void setNextPlanetImage(final int nextPlanetDrawableId)
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextPlanetImage.setImageResource(nextPlanetDrawableId);
                nextPlanetImage.invalidate();
            }
        });
    }

    /**
     * Saves a JSON serialized string representing the game session to private internal storage.
     * @param sessionStateAsJson the game session to save, as a JSON string.
     */
    public void saveSessionState(String sessionStateAsJson)
    {
        try
        {
            FileOutputStream outputStream = this.activity.openFileOutput(MainActivity.sessionStateFileName, Context.MODE_PRIVATE);
            outputStream.write(sessionStateAsJson.getBytes());
            outputStream.close();
            Log.e("GameViewModel", "Saved session state.");
        }
        catch (Exception e)
        {
            Log.e("GameViewModel", "Failed to read saved state ", e);
        }
    }

    /**
     * Attempts to load a saved Game Session state from app's internal directory, if present else
     * returns null.
     * @return a saved Game Session state if present (can return null).
     */
    public SerializableSessionState loadSessionState()
    {
        try
        {
            File file = new File(this.activity.getFilesDir(), MainActivity.sessionStateFileName);
            if (file != null)
            {
                StringBuilder stringBuilder = new StringBuilder();
                FileInputStream inputStream = activity.openFileInput(MainActivity.sessionStateFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String gsonString = stringBuilder.toString();
                if (gsonString != null && !gsonString.isEmpty())
                {
                    Gson gson = new Gson();
                    SerializableSessionState sessionValues = gson.fromJson(gsonString,
                            SerializableSessionState.class);
                    this.activity.deleteFile(MainActivity.sessionStateFileName);
                    Log.e("GameViewModel", "Saved session state.");
                    return sessionValues;
                }
            }
        }
        catch (Exception e)
        {
            Log.e("GameViewModel", "Failed to load saved state. ", e);
        }

        return null;
    }

    /**
     * Gets the motion of the device on the vertical axis.
     * @return the motion of the device on the vertical axis.
     */
    protected float getVerticalMotion()
    {
        return az_Yaw;
    }

    /**
     * Gets the motion of the device on the horizontal axis.
     * @return the motion of the device on the horizontal axis.
     */
    protected float getHorizontalMotion() { return ax_Roll; }

    /**
     * Gets the normalized motion of the device on the vertical axis (-1 to 1).
     * @return the normalized motion of the device on the vertical axis.
     */
    protected float getNormalizedVerticalMotion()
    {
        float normalized = (az_Yaw - min_az_Yaw) / (max_az_Yaw - min_az_Yaw);
        return normalized * (1 - -1) + -1;
    }

    /**
     * Gets the normalized motion of the device on the horizontal axis.
     * @return the normalized motion of the device on the horizontal axis.
     */
    protected float getNormalizedHorizontalMotion()
    {
        float normalized = (gx_Roll - min_gx_Roll) / (max_gx_Roll - min_gx_Roll);
        return normalized * (1 - -1) + -1;
    }

    /**
     * Gets the context of the game board layout for instantiating null image objects.
     * @return the context of the game board layout.
     */
    protected Context getGameLayoutContext()
    {
        return this.gameLayout.getContext();
    }

    /**
     * Displays the game over display image.
     */
    public void displayGameOverImage()
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameOverImage.setVisibility(View.VISIBLE);
                gameOverImage.bringToFront();
                gameOverImage.invalidate();
            }
        });
    }
}
