package com.example.chess;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
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

        // setup theme selection radio group
        RadioGroup radioGroup = (RadioGroup) inflatedView.findViewById(R.id.theme_group);
        SharedPreferences sharedPref = containerActivity.getPreferences(Context.MODE_PRIVATE);

        switch (sharedPref.getInt("theme", R.style.Theme_Classic)) {
            case R.style.Theme_Classic:
                radioGroup.check(R.id.theme1_selection); break;
            case R.style.Theme_Wooden:
                radioGroup.check(R.id.theme2_selection); break;
            case R.style.Theme_Olive:
                radioGroup.check(R.id.theme3_selection); break;
            case R.style.Theme_Night:
                radioGroup.check(R.id.theme4_selection); break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = sharedPref.edit();
                switch (checkedId) {
                    case R.id.theme1_selection:
                        editor.putInt("theme",R.style.Theme_Classic);
                        break;
                    case R.id.theme2_selection:
                        editor.putInt("theme",R.style.Theme_Wooden);
                        break;
                    case R.id.theme3_selection:
                        editor.putInt("theme",R.style.Theme_Olive);
                        break;
                    case R.id.theme4_selection:
                        editor.putInt("theme",R.style.Theme_Night);
                        break;
                }
                editor.apply();
                containerActivity.setTheme(sharedPref.getInt("theme", R.style.Theme_Classic));
            }
        });

        return inflatedView;
    }

    // TODO: listeners for radio/switches

}
