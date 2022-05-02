/*
 * @author: Min Tran
 * @author: Jaygee Galvez
 * @description: This fragment handles the help screen, which gives information on
 * how to use the app.
 */

package com.example.chess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HelpFragment extends Fragment implements View.OnClickListener {

    // fragment variables
    private static final int LAYOUT = R.layout.fragment_help;
    private AppCompatActivity containerActivity;

    /**
     * Sets container activity.
     * @param containerActivity - activity that fragment is contained in
     */
    public void setContainerActivity(AppCompatActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Upon view creation, setup layout and buttons.
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

        // setup button
        inflatedView.findViewById(R.id.rules_button).setOnClickListener(this);

        return inflatedView;
    }

    /**
     * Performs associated actions depending on which view was clicked.
     * @param view view that was clicked
     */
    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.rules_button) return;

        // create and display rules fragment
        FragmentTransaction transaction = containerActivity.
                getSupportFragmentManager().beginTransaction();
        RulesFragment rf = new RulesFragment();
        transaction.replace(R.id.container, rf);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
