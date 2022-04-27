package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get preferences
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        setTheme(sharedPref.getInt("theme", R.style.Theme_Classic));

        // create and display menu fragment
        MenuFragment mf = new MenuFragment();
        mf.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mf);
        transaction.commit();
    }
}