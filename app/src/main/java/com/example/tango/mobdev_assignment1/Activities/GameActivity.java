package com.example.tango.mobdev_assignment1.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.tango.mobdev_assignment1.Game.GameController;
import com.example.tango.mobdev_assignment1.Game.GameViewModel;
import com.example.tango.mobdev_assignment1.R;

public class GameActivity extends AppCompatActivity {

    private GameController gameController;
    private GameViewModel viewModel;
    private boolean isPaused;

    /**
     * Overrides the activity onCreate method to initialise view objects.
     * @param savedInstanceState the saved instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.isPaused = false;
        this.viewModel = new GameViewModel(this);
    }

    /**
     * Instantiates the game controller which runs the game loop, called by game board view's
     * onSizeChanged event to ensure that game board has correct dimensions before continuing.
     */
    public void launchNewGameController()
    {
        if (this.gameController == null)
        {
            this.gameController = new GameController(this.viewModel);
        }
    }

    /**
     * Called when the activity is paused.
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        this.isPaused = true;
        this.gameController.pauseGameLoop();
    }

    /**
     * Called when the activity is resumed after being paused.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        if (this.isPaused)
        {
            this.isPaused = false;
            this.gameController.resumeGameLoop();
        }
        //TODO: load session from xml and start game again.
    }
}