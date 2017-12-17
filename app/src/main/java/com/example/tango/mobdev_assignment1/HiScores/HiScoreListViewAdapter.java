package com.example.tango.mobdev_assignment1.HiScores;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tango.mobdev_assignment1.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * List View adapter for displaying hi scores.
 * Created by tango on 01/12/2017.
 */

public class HiScoreListViewAdapter extends ArrayAdapter<HiScore> {

    private ArrayList<HiScore> hiScores;
    private LayoutInflater inflater;

    public HiScoreListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<HiScore> objects) {
        super(context, resource, objects);
        this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.hiScores = (ArrayList<HiScore>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
        {
            convertView = this.inflater.inflate(R.layout.hi_score_list_item_view, null);
        }
        HiScore hiScoreItem = hiScores.get(position);

        long score = hiScoreItem.getScore();
        String scoreStr = String.valueOf(hiScoreItem.getScore());

        if (score < 1000)
        {
            scoreStr = String.valueOf(hiScoreItem.getScore());
        }
        else if (score < 10000)
        {
            scoreStr = scoreStr.substring(0,1) + "," + scoreStr.substring(1,scoreStr.length());
        }
        else if (score < 100000)
        {
            scoreStr = scoreStr.substring(0,2) + "," + scoreStr.substring(2,scoreStr.length());
        }
        else if (score < 1000000)
        {
            scoreStr = scoreStr.substring(0,3) + "," + scoreStr.substring(3,scoreStr.length());
        }
        else if (score < 10000000)
        {
            scoreStr = scoreStr.substring(0,1) + "," + scoreStr.substring(1,4) + "," + scoreStr.substring(4,scoreStr.length());
        }
        else if (score < 100000000)
        {
            scoreStr = scoreStr.substring(0,2) + "," + scoreStr.substring(2,5) + "," + scoreStr.substring(5,scoreStr.length());
        }
        else if (score < 1000000000)
        {
            scoreStr = scoreStr.substring(0,3) + "," + scoreStr.substring(3,6) + "," + scoreStr.substring(6,scoreStr.length());
        }

        ((TextView)convertView.findViewById(R.id.HiScoreRank)).setText(String.valueOf(position + 1));
        ((TextView)convertView.findViewById(R.id.HiScoreLevel)).setText(String.valueOf(hiScoreItem.getLevel()));
        ((TextView)convertView.findViewById(R.id.HiScoreName)).setText(hiScoreItem.getName());
        ((TextView)convertView.findViewById(R.id.HiScoreScore)).setText(scoreStr);

        return convertView;
    }
}
