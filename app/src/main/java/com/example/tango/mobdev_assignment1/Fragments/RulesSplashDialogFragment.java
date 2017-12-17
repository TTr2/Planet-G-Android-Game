package com.example.tango.mobdev_assignment1.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tango.mobdev_assignment1.R;

/**
 * Created by tango on 14/12/2017.
 */

public class RulesSplashDialogFragment extends Fragment {

    OnOkButtonClickedListener callback;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static RulesSplashDialogFragment newInstance() {
        RulesSplashDialogFragment fragment = new RulesSplashDialogFragment();

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (OnOkButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnOkButtonClickedListener interface.");
        }
    }

    public interface OnOkButtonClickedListener{
        public void okButtonClickedListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rules, container, false);
        // Watch for button clicks.
        Button okButton = (Button)v.findViewById(R.id.OkStartButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callback.okButtonClickedListener();
            }
        });

        return v;
    }
}

