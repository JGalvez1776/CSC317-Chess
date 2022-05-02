/*
 * @author: Min Tran
 * @author: Jaygee Galvez
 * @description: This fragment handles the menu screen and its associated buttons.
 */

package com.example.chess;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MenuFragment extends Fragment implements View.OnClickListener {

    // fragment variables
    private static final int LAYOUT = R.layout.fragment_menu;
    private AppCompatActivity containerActivity;

    /**
     * Sets container activity.
     * @param containerActivity - activity that fragment is contained in
     */
    public void setContainerActivity(AppCompatActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Upon view creation, setups layout and buttons.
     * @param inflater - layout inflater
     * @param container - view group container
     * @param savedInstanceState - saved instance state
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get inflated view
        View inflatedView = inflater.inflate(LAYOUT, container, false);

        // setup buttons
        inflatedView.findViewById(R.id.new_game_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.daily_puzzle_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.settings_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.help_button).setOnClickListener(this);

        return inflatedView;
    }

    /**
     * Performs associated actions depending on which view was clicked.
     * @param view view that was clicked
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = containerActivity.
                getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        // setup transaction according to button pressed
        switch (view.getId()) {
            case R.id.new_game_button:
                fragment = new GameFragment();
                ((GameFragment) fragment).setContainerActivity(containerActivity);
                break;
            case R.id.daily_puzzle_button:
                fragment = new PuzzleFragment();
                ((PuzzleFragment) fragment).setContainerActivity(containerActivity);
                break;
            case R.id.settings_button:
                fragment = new SettingsFragment();
                ((SettingsFragment) fragment).setContainerActivity(containerActivity);
                break;
            case R.id.help_button:
                fragment = new HelpFragment();
                ((HelpFragment) fragment).setContainerActivity(containerActivity);
                break;
        }

        // do transaction
        if (fragment != null) transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
