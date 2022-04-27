package com.example.chess;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SettingsFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_settings;

    private AppCompatActivity containerActivity;
    private View inflatedView;

    /**
     * Sets container activity.
     * @param containerActivity activity that fragment is contained in
     */
    public void setContainerActivity(AppCompatActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Upon view creation, sets layout, and returns inflated view.
     * @param inflater layout inflater
     * @param container view group container
     * @param savedInstanceState saved instance state
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get inflated view
        inflatedView = inflater.inflate(LAYOUT, container, false);

        RadioGroup radioGroup = (RadioGroup) inflatedView.findViewById(R.id.theme_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences sharedPref = containerActivity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                switch (checkedId) {
                    case R.id.theme1_selection:
                        editor.putInt("theme",1); break;
                    case R.id.theme2_selection:
                        editor.putInt("theme",2); break;
                    case R.id.theme3_selection:
                        editor.putInt("theme",3); break;
                    case R.id.theme4_selection:
                        editor.putInt("theme",4); break;
                }
                editor.apply();
            }
        });

        return inflatedView;
    }

    // TODO: listeners for radio/switches

}
