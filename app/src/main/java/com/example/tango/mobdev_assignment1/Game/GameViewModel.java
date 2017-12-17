package com.example.tango.mobdev_assignment1.Game;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tango.mobdev_assignment1.Activities.GameActivity;
import com.example.tango.mobdev_assignment1.Activities.HiScoresActivity;
import com.example.tango.mobdev_assignment1.Activities.MainActivity;
import com.example.tango.mobdev_assignment1.Fragments.RulesSplashDialogFragment;
import com.example.tango.mobdev_assignment1.HiScores.HiScore;
import com.example.tango.mobdev_assignment1.HiScores.HiScoreComparator;
import com.example.tango.mobdev_assignment1.R;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * The GameViewModel class implements the MVVM pattern to abstract the view and sensor input
 * implementation from the GameController; view operations are performed on the UI main thread.
 * Created by tango on 24/11/2017.
 */

public class GameViewModel {

    private float ax_Roll = 0;
    private float az_Yaw = 0;
    private long score;
    private int level;
    private int rank;
    private int numColumnsSetting;
    private boolean vibrateEnabled;
    private boolean musicEnabled;
    private boolean sfxEnabled;
    boolean isNewGame;
    private String playerName;
    private GameActivity activity;
    private TextView levelText;
    private TextView scoreText;
    private TextView nextPlanetText;
    private TextView playerNameText;
    private ImageView nextPlanetImage;
    private ImageView gameOverImage;
    private Button navigateToHiScoresButton;
    private GridLayout gameDetailsGrid;
    private RelativeLayout gameLayout;
    private RelativeLayout gameOverLayout;
    private Toast toast;
    private Sensor accelerometer;
    private Vibrator vibrator;
    private MediaPlayer bgndMediaPlayer;
    private MediaPlayer actionMediaPlayer;
    private SensorEventListener accelerometerEventListener;
    private Fragment rulesFragment;

    /**
     * Constructor for GameViewModel class that abstracts the view implementation.
     * @param context the level text view
     */
    public GameViewModel(GameActivity context) {
        this.activity = context;

        this.gameDetailsGrid = (GridLayout) context.findViewById(R.id.GameDetailsGridLayout);
        this.gameLayout = (RelativeLayout) context.findViewById(R.id.GameLayout);
        this.scoreText = (TextView) context.findViewById(R.id.ScoreText);
        this.levelText = (TextView) context.findViewById(R.id.LevelText);
        this.nextPlanetImage = (ImageView) context.findViewById(R.id.NextPlanetImage);
        this.nextPlanetText = (TextView) context.findViewById(R.id.NextPlanetNameText);

        this.gameOverLayout = (RelativeLayout) this.activity.findViewById(R.id.GameOverLayout);
        this.playerNameText = (TextView) context.findViewById(R.id.PlayerNameText);
        this.navigateToHiScoresButton = (Button) context.findViewById(R.id.NavigateToHiScoresButton);

        this.gameOverImage = (ImageView) context.findViewById(R.id.GameOverImage);
        this.playerNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                playerName = v.getText().toString();
                return false;
            }
        });
        this.navigateToHiScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName != null && !playerName.isEmpty() && playerName != "Name")
                {
                    HiScore hiScore = new HiScore(score, level, playerName);
                    rank = saveHiScore(hiScore);
                    Intent hiScoresIntent = new Intent(activity, HiScoresActivity.class);
                    hiScoresIntent.putExtra("Rank", rank);
                    activity.startActivity(hiScoresIntent);
                    killGameActivity();
                }
            }
        });

        SensorManager sensorManager = (SensorManager) this.activity.getSystemService(SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.accelerometerEventListener = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    ax_Roll = event.values[0];
                    az_Yaw = event.values[2];
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        sensorManager.registerListener(accelerometerEventListener, this.accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.activity);
        this.numColumnsSetting = Integer.valueOf(settings.getString("num_columns", "7"));
        this.vibrateEnabled = settings.getBoolean("vibrate_on", true);
        if (this.vibrateEnabled)
        {
            this.vibrator = (Vibrator) this.activity.getSystemService(Context.VIBRATOR_SERVICE);
        }

        this.musicEnabled = settings.getBoolean("bgnd_music_on", true);
        if (this.musicEnabled)
        {
            this.bgndMediaPlayer = MediaPlayer.create(this.activity, R.raw.jazz);
            this.bgndMediaPlayer.setLooping(true);
            this.bgndMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // restart playback
                    try {
                        mp.reset();
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        Log.e("GameViewModel", "Failed to recover background music media player.");
                    }
                    return false;
                }
            });
        }
        this.sfxEnabled = settings.getBoolean("sound_fx_on", true);
        if (sfxEnabled)
        {
            this.actionMediaPlayer = MediaPlayer.create(this.activity, R.raw.cymbals);
            this.actionMediaPlayer.setLooping(false);
            this.actionMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // restart playback
                    try {
                        mp.reset();
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        Log.e("GameViewModel", "Failed to recover action sound media player.");
                    }
                    return false;
                }
            });
        }
    }

    /**
     * Sets the game session values for saving to file after player inputs name.
     * @param score the game score to save to the hi score text file.
     * @param level the game level to save to the hi score file.
     */
    public void gameOverRoutine(long score, int level) {
        this.playGameOver();
        this.displayGameOverImage();
        this.deleteSessionState();
        this.score = score;
        this.level = level;
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameOverLayout.setVisibility(View.VISIBLE);
                gameOverLayout.bringToFront();
                gameOverImage.bringToFront();
                playerNameText.bringToFront();
                navigateToHiScoresButton.bringToFront();
            }
        });
    }

    /**
     * Saves a JSON serialized string representing the game session to private internal storage.
     * @param sessionStateAsJson the game session to save, as a JSON string.
     */
    protected void saveSessionState(String sessionStateAsJson)
    {
        try
        {
            FileOutputStream outputStream = this.activity.openFileOutput(MainActivity.sessionStateFileName, MODE_PRIVATE);
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
    protected SerializableSessionState loadSessionState()
    {
        try
        {
            String[] files = this.activity.fileList();
            for (String filename : files)
            {
                if (filename.equals(MainActivity.sessionStateFileName))
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    FileInputStream inputStream = activity.openFileInput(MainActivity.sessionStateFileName);
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(line);
                    }

                    String gsonString = stringBuilder.toString();
                    if (gsonString != null && !gsonString.isEmpty())
                    {
                        Gson gson = new Gson();
                        SerializableSessionState sessionValues = gson.fromJson(gsonString,
                                SerializableSessionState.class);
                        this.deleteSessionState();
                        Log.e("GameViewModel", "Saved session state.");
                        return sessionValues;
                    }
                }
            }
        }
        catch (Exception e) {
            Log.e("GameViewModel", "Failed to load saved state. ", e);
        }

        return null;
    }

    /**
     * Deletes the saved session state file.
     */
    protected void deleteSessionState()
    {
        this.activity.deleteFile(MainActivity.sessionStateFileName);
    }

    /**
     * Adds a score to the app's hi scores text file as JSON serialized data.
     * @param hiScore the new score to add to the app's hi score text file.
     * @return rank the position of the new score in the hi scores list.
     */
    protected int saveHiScore(HiScore hiScore)
    {
        ArrayList<HiScore> hiScores = HiScoresActivity.loadHiScores(this.activity,
                HiScoresActivity.hiScoresFileName);
        hiScores.add(hiScore);
        Collections.sort(hiScores, new HiScoreComparator());
        int rank = hiScores.indexOf(hiScore);
        try
        {
            Gson gson = new Gson();
            String jsonString = gson.toJson(hiScores);
            FileOutputStream outputStream = this.activity.openFileOutput(HiScoresActivity.hiScoresFileName,
                    MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
            Log.e("GameViewModel", "Saved hi score.");
        }
        catch (Exception e)
        {
            Log.e("GameViewModel", "Failed to save hi score.", e);
        }

        return rank;
    }

    /**
     * Vibrates the device for the given duration.
     * @param ms the duration in milliseconds to vibrate for.
     */
    protected void vibrate(int ms)
    {
        if (this.vibrateEnabled)
        {
            this.vibrator.vibrate(ms);
        }
    }

    /**
     * Applies the cell's display value's to the image views properties.
     * @param cell the cell to display in the layout.
     */
    protected void updateCellOnUiThread(final GameBoardCell cell)
    {
        this.activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GameBoardCell mutableCell = cell;
                mutableCell.setImageBitmap(cell.getCellBitmap());
                mutableCell.setX(cell.getImageX());
                mutableCell.setY(cell.getImageY());
                mutableCell.invalidate();
            }
        });
    }

    /**
     * Adds the game board cell Image Views to the game layout.
     * @param gameBoard the game board.
     */
    public void addGameBoardToLayout(final GameBoard gameBoard)
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                for (int column = 0; column < gameBoard.getColumns(); column++)
                {
                    for (int row = 0; row < gameBoard.getRows(); row++)
                    {
                        GameBoardCell cell = gameBoard.getCell(column, row);
                        gameLayout.addView(cell);
                        cell.setVisibility(View.VISIBLE);
                        cell.bringToFront();
                        gameLayout.invalidate();
                    }
                }
            }
        });
    }

    /**
     * Plays the background music track, media player should be in prepared state when created.
     */
    protected void playBackgroundMusic()
    {
        if (this.musicEnabled)
        {
            this.bgndMediaPlayer.start();
        }
    }

    /**
     * Pauses the background music track.
     */
    protected void pauseBackgroundMusic()
    {
        if (this.musicEnabled)
        {
            try
            {
                if (this.bgndMediaPlayer.isPlaying())
                {
                    this.bgndMediaPlayer.pause();
                }
            }
            catch (Exception e)
            {
                Log.e("GameViewModel", "Failed to pause background media player.");
            }
        }
    }

    /**
     * Plays the action sound, media player should be in prepared state when created.
     */
    protected void playActionSound()
    {
        if (this.sfxEnabled)
        {
            this.actionMediaPlayer.start();
        }
    }

    /**
     * Stops and releases the background music and action sound resources, after gradually reducing
     * the music volume.
     */
    protected void playGameOver()
    {
        if (this.sfxEnabled)
        {
            this.actionMediaPlayer = MediaPlayer.create(this.activity, R.raw.scream);
            this.actionMediaPlayer.start();
        }

        if (this.musicEnabled)
        {
            float volume = 1;
            while(volume > 0)
            {
                volume -= 0.01;
                this.bgndMediaPlayer.setVolume(volume, volume);
            }

            this.bgndMediaPlayer.stop();
            this.bgndMediaPlayer.release();
        }
    }

    /**
     * Removes all planet cells from the game board, usually called after saving game state.
     * @param gameBoard the game board.
     */
    protected void wipeGameBoardClean(final GameBoard gameBoard)
    {
        this.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            for (int column = 0; column < gameBoard.getColumns(); column++)
            {
                for (int row = 0; row < gameBoard.getRows(); row++)
                {
                    if (gameBoard.getCell(column, row).isOccupied())
                    {
                        gameLayout.removeView(gameBoard.getCell(column, row));
                    }
                }
            }
            gameLayout.invalidate();
            }
        });
    }

    /**
     * Displays the game over display image.
     */
    private void displayGameOverImage()
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null)
                {
                    toast.cancel();
                }
                gameOverLayout.setVisibility(View.VISIBLE);
                gameOverLayout.bringToFront();
                gameOverLayout.invalidate();
            }
        });
    }

    /**
     * Finishes the activity, killing the instance on the activity stack.
     */
    protected void killGameActivity()
    {
        this.activity.finish();
    }

    /* Getters & Setters */

    /**
     * Updates the score TextView with latest score from game session.
     * @param score the new score to display as formatted string.
     */
    protected void setScoreText(final long score)
    {
        final GameActivity activity = this.activity;
        String str = String.format(Locale.ENGLISH, "%09d", score);
        final String scoreStr = str.substring(0,3) + "," + str.substring(3,6) + "," + str.substring(6,9);
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreText.setText(scoreStr);
                scoreText.invalidate();
            }
        });
    }

    /**
     * Display toast notification to display latest points.
     * @param points the points.
     */
    protected void makeToastForPoints(final long points)
    {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast = Toast.makeText(activity.getApplicationContext(), "+" + String.valueOf(score), Toast.LENGTH_SHORT);
                CountDownTimer countDown;
                countDown = new CountDownTimer(500, 1000) {
                    public void onTick(long duration) {
                        toast.show();
                    }

                    public void onFinish() {
                        toast.cancel();
                    }
                };
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
        this.setNextPlanetImage(nextPlanet.getSmallImageResource());
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
     * Gets the number of columns in the game board setting.
     * @return the number of columns to set in the game board.
     */
    public int getNumberOfColumnsSetting() { return numColumnsSetting; }

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
     * Gets the context of the game board layout for instantiating null image objects.
     * @return the context of the game board layout.
     */
    protected Context getGameLayoutContext()
    {
        return this.gameLayout.getContext();
    }


}
