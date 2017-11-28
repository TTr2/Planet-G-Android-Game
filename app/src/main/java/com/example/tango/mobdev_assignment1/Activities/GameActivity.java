package com.example.tango.mobdev_assignment1.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.tango.mobdev_assignment1.Game.GameManager;
import com.example.tango.mobdev_assignment1.Game.GameViewModel;
import com.example.tango.mobdev_assignment1.R;

public class GameActivity extends AppCompatActivity {

    private GameManager gameManager;
    private GameViewModel viewModel;

    /**
     * Overrides the activity onCreate method to initialise view objects.
     * @param savedInstanceState the saved instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.viewModel = new GameViewModel(this);
    }

    /**
     * Starts the game manager loop, called by game board view's onSizeChanged event to ensure that
     * game board has correct dimensions before continuing.
     */
    public void launchGameManager() {
        this.gameManager = new GameManager(this.viewModel);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // TODO: saved session
//        this.launchGameManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: load session from xml and start game again.

    }

    @Override
    protected void onStop() {
        this.gameManager.stopGameLoop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



}