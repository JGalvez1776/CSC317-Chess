/*
 * @author: Min Tran
 * @author: Jaygee Galvez
 * @description: This fragment handles the settings screen, which allows the user to adjust their
 * preferences.
 */

package com.example.chess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class SettingsFragment extends Fragment {

    // fragment variables
    private static final int LAYOUT = R.layout.fragment_settings;
    private AppCompatActivity containerActivity;
    private View inflatedView;

    // preferences variables
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static final HashMap<Integer, Integer> themeMap;
    static {
        themeMap = new HashMap<>();
        themeMap.put(R.style.Theme_Classic, R.id.theme1_selection);
        themeMap.put(R.id.theme1_selection, R.style.Theme_Classic);
        themeMap.put(R.style.Theme_Wooden, R.id.theme2_selection);
        themeMap.put(R.id.theme2_selection, R.style.Theme_Wooden);
        themeMap.put(R.style.Theme_Olive, R.id.theme3_selection);
        themeMap.put(R.id.theme3_selection, R.style.Theme_Olive);
        themeMap.put(R.style.Theme_Night, R.id.theme4_selection);
        themeMap.put(R.id.theme4_selection, R.style.Theme_Night);
    }

    /**
     * Sets container activity.
     * @param containerActivity activity that fragment is contained in
     */
    public void setContainerActivity(AppCompatActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Upon view creation, setups layout and buttons.
     * @param inflater layout inflater
     * @param container view group container
     * @param savedInstanceState saved instance state
     * @return Inflated view.
     */
    @SuppressWarnings("ConstantConditions")
    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get inflated view and shared prefs
        inflatedView = inflater.inflate(LAYOUT, container, false);
        sharedPref = containerActivity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // setup initial theme selection
        RadioGroup radioGroup = (RadioGroup) inflatedView.findViewById(R.id.theme_group);
        radioGroup.check(themeMap.get(sharedPref.getInt("theme",R.style.Theme_Classic)));

        // set listener for dynamic theme change
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            editor.putInt("theme",themeMap.get(checkedId));
            editor.apply();
            containerActivity.setTheme(sharedPref.getInt("theme", R.style.Theme_Classic));
            ConstraintLayout cl = containerActivity.findViewById(R.id.container);
            cl.setBackgroundColor(getThemeColor("colorSecondary"));
        });

        // setup toggles
        setupToggle(R.id.animation_switch,"animate");
        setupToggle(R.id.undo_switch,"undo");

        return inflatedView;
    }

    /**
     * Sets up switch toggle.
     * @param viewId id of switch view
     * @param pref preference that switch controls
     */
    public void setupToggle(int viewId, String pref) {
        SwitchCompat undoSwitch = inflatedView.findViewById(viewId);
        undoSwitch.setChecked(sharedPref.getInt(pref, 1) == 1);
        undoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putInt(pref,1);
            } else editor.putInt(pref,0);
            editor.apply();
        });
    }

    /**
     * Returns the color id of a color given the color name.
     * @param name color name
     * @return Color id.
     */
    public int getThemeColor(String name){
        TypedValue outValue = new TypedValue();
        int colorAttr = containerActivity.getResources().getIdentifier
                (name, "attr", containerActivity.getPackageName());
        containerActivity.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

}
