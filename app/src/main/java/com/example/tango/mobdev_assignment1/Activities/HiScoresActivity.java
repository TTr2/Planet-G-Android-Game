package com.example.tango.mobdev_assignment1.Activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.tango.mobdev_assignment1.Game.SerializableSessionState;
import com.example.tango.mobdev_assignment1.HiScores.HiScore;
import com.example.tango.mobdev_assignment1.HiScores.HiScoreComparator;
import com.example.tango.mobdev_assignment1.HiScores.HiScoreListViewAdapter;
import com.example.tango.mobdev_assignment1.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * the HiScores activity page, displays all recorded high scores
 */
public class HiScoresActivity extends AppCompatActivity {

    public final static String hiScoresFileName = "hiScores.txt";
    private ArrayList<HiScore> hiScores;
    private ConstraintLayout layout;
    private HiScoreListViewAdapter hiScoresListViewAdapter;
    private ListView hiScoresListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_scores);

        this.hiScoresListView = (ListView) findViewById(R.id.HiScoresListView);
        this.hiScores = HiScoresActivity.loadHiScores(this, HiScoresActivity.hiScoresFileName);
        Collections.sort(this.hiScores, new HiScoreComparator());
        this.hiScoresListViewAdapter = new HiScoreListViewAdapter(this, R.layout.hi_score_list_item_view, hiScores);
        this.hiScoresListView.setAdapter(this.hiScoresListViewAdapter);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            int rank = extras.getInt("Rank");
            this.hiScoresListView.smoothScrollToPosition(rank);
        }
    }

    /**
     * Attempts to load the hi scores file from app's internal directory, if present else
     * returns empty array list.
     * @param context the application context.
     * @param hiScoresFileNameArg the hi scores filename.
     * @return the hi scores array list (can be empty).
     */
    public static ArrayList<HiScore> loadHiScores(Context context, String hiScoresFileNameArg)
    {
        try
        {
            String[] files = context.fileList();
            for (String filename : files)
            {
                if (filename.equals(hiScoresFileNameArg))
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    FileInputStream inputStream = context.openFileInput(hiScoresFileNameArg);
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    Gson gson = new Gson();
                    ArrayList<HiScore> hiScoreValues = gson.fromJson(bufferedReader,
                            new TypeToken<ArrayList<HiScore>>(){}.getType());
                    Log.e("HiScoresActivity", "Loaded Hi Scores.");
                    return hiScoreValues;
                }
            }
        }
        catch (Exception e) {
            Log.e("HiScoresActivity", "Failed to load Hi Scores. ", e);
        }

        return new ArrayList<>();
    }
}
