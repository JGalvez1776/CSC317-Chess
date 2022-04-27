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
        switch (sharedPref.getInt("theme", 1)) {
            case 1:
                setTheme(R.style.Theme_Classic); break;
            case 2:
                setTheme(R.style.Theme_Wooden); break;
            case 3:
                setTheme(R.style.Theme_Olive); break;
            case 4:
                setTheme(R.style.Theme_Night); break;
        }
        System.out.println(sharedPref.getInt("theme", 1));

        // create and display menu fragment
        MenuFragment mf = new MenuFragment();
        mf.setContainerActivity(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mf);
        transaction.commit();
    }
}