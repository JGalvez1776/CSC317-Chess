/**
 * @author: Min Tran
 * @description: This activity handles the container activity for all the app's fragments.
 */

package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /**
     * Retrieves preferences and displays menu fragment.
     * @param savedInstanceState - saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get preferences
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        setTheme(sharedPref.getInt("theme", R.style.Theme_Classic));
        setContentView(R.layout.activity_main);

        // create and display menu fragment
        MenuFragment mf = new MenuFragment();
        mf.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mf);
        transaction.commit();
    }
}