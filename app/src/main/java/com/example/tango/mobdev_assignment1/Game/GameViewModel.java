package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.tango.mobdev_assignment1.Activities.GameActivity;
import com.example.tango.mobdev_assignment1.R;

import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;

/**
 * The GameViewModel class implements the MVVM pattern to abstract the view implementation from the
 * GameManager controller.
 * Created by tango on 24/11/2017.
 */

public class GameViewModel {

    private GameActivity activity;
    private TextView levelText;
    private TextView scoreText;
    private TextView nextPlanetText;
    private ImageView nextPlanetImage;
    private GridLayout gameDetailsGrid;
    private RelativeLayout gameLayout;
    private Sensor accelerometer;
    private Sensor gyroscope;
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
    protected void drawGameBoard(GameBoard gameBoard)
    {
        final GameBoard tmpGameBoard = gameBoard;
        this.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Draw all blocks on game board
                for (int column = 0; column < tmpGameBoard.getColumns(); column++)
                {
                    for (int row = 0; row < tmpGameBoard.getRows(); row++)
                    {
                        tmpGameBoard.getCell(column, row).getImageView().invalidate();
                    }
                }
            }
        });
    }

    /**
     * Redraws an individual block on the game board.
     * @param imageView
     * @param cell the GameBoardCell to extract the x and y coordinates to redraw the image at.
     */
    protected void drawIndividualImage(ImageView imageView, GameBoardCell cell)
    {
        final ImageView tmpImageView = imageView;
        final float tmpX = cell.getImageX();
        final float tmpY = cell.getImageY();
        this.activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tmpImageView.setX(tmpX);
                tmpImageView.setY(tmpY);
                tmpImageView.invalidate();
            }
        });
    }

    /**
     * Adds an image view to the game layout.
     * @param imageView
     */
    protected void addImageViewToGameLayout(ImageView imageView)
    {
        final ImageView tmpImageView = imageView;
        final RelativeLayout tmpLayout = this.gameLayout;
        this.activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tmpLayout.addView(tmpImageView);
                tmpImageView.invalidate();
                tmpLayout.invalidate();
            }
        });
    }

    /**
     * Updates the score TextView with latest score from game session.
     * @param score the new score to display as formatted string.
     */
    protected void setScoreText(long score)
    {
        String str = String.format(Locale.ENGLISH, "%07d", score);
        final String scoreStr = str.substring(0,1) + "," + str.substring(1,4) + "," + str.substring(4,7);
        final TextView tmpScoreText = this.scoreText;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tmpScoreText.setText(scoreStr);
                tmpScoreText.invalidate();
            }
        });
    }

    /**
     * Updates the game level TextView with latest level from game session.
     * @param levelText the new level text
     */
    protected void setLevelText(String levelText)
    {
        final TextView tmpLevelText = this.levelText;
        final String tmpLevelStr = levelText;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tmpLevelText.setText(tmpLevelStr);
                tmpLevelText.invalidate();
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
    protected void setNextPlanetText(String nextPlanetName)
    {
        final TextView tmpNextPlanetText = this.nextPlanetText;
        final String tmpPlanetNameStr = nextPlanetName;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tmpNextPlanetText.setText(tmpPlanetNameStr);
                tmpNextPlanetText.invalidate();
            }
        });
    }

    /**
     * Updates the next Planet ImageView with image of next PlanetBlockImageView to drop
     * from game session.
     * @param nextPlanetDrawableId the drawable id of the new image.
     */
    protected void setNextPlanetImage(int nextPlanetDrawableId)
    {
        final ImageView tmpNextPlanetImage = this.nextPlanetImage;
        final int tmpPlanetDrawableId = nextPlanetDrawableId;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tmpNextPlanetImage.setImageResource(tmpPlanetDrawableId);
                tmpNextPlanetImage.invalidate();
            }
        });
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
    protected float getHorizontalMotion()
    {
//        return gx_Roll;
        return ax_Roll;
    }

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
    protected Context getGameBoardLayoutContext()
    {
        return this.gameLayout.getContext();
    }



}
