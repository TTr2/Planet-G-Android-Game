package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.example.tango.mobdev_assignment1.Activities.GameActivity;

/**
 * The game relative layout contains the game board blocks.
 * Created by tango on 24/11/2017.
 */
public class GameRelativeLayout extends RelativeLayout {

    /**
     * Constructor for game relative layout that will display the game board.
     * @param context the Game Activity context.
     */
    public GameRelativeLayout(Context context) {
        super(context);
    }

    /**
     * Constructor for game relative layout that will display the game board.
     * @param context the Game Activity context.
     */

    public GameRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor for game relative layout that will display the game board.
     * @param context the Game Activity context.
     */
    public GameRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the game board's static dimensions fields then notifies the activity's LaunchGameLoop
     * method once the display has adjusted for the devices screen dimensions.
     * @param hasWindowFocus has windows focus?
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        GameBoard.setGameBoardDimensions(this.getWidth(), this.getHeight());
        ((GameActivity) this.getContext()).launchNewGameController();
    }
}
