package com.example.chess;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MenuFragment extends Fragment implements View.OnClickListener {

    private static final int LAYOUT = R.layout.fragment_menu;

    private AppCompatActivity containerActivity;
    private View inflatedView;

    /**
     * Sets container activity.
     * @param containerActivity - activity that fragment is contained in
     */
    public void setContainerActivity(AppCompatActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Upon view creation, sets layout, and returns inflated view.
     * @param inflater - layout inflater
     * @param container - view group container
     * @param savedInstanceState - saved instance state
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get inflated view
        inflatedView = inflater.inflate(LAYOUT, container, false);

        // setup buttons
        inflatedView.findViewById(R.id.new_game_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.continue_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.daily_puzzle_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.settings_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.help_button).setOnClickListener(this);

        return inflatedView;
    }

    /**
     * Holds on click functions for each button in the layout.
     * @param view - view that was clicked
     */
    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = containerActivity.
                getSupportFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.new_game_button:

            case R.id.continue_button:

            case R.id.daily_puzzle_button:

            case R.id.settings_button:

            case R.id.help_button:

        }
    }

}
