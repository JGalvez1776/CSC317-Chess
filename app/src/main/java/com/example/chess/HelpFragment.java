package com.example.chess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HelpFragment extends Fragment implements View.OnClickListener {

    private static final int LAYOUT = R.layout.fragment_help;

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

        inflatedView.findViewById(R.id.rules_button).setOnClickListener(this);

        return inflatedView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.rules_button) return;
        FragmentTransaction transaction = containerActivity.
                getSupportFragmentManager().beginTransaction();
        RulesFragment rf = new RulesFragment();
        rf.setContainerActivity(containerActivity);
        transaction.replace(R.id.container, rf);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
