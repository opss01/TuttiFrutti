package com.example.tuttifrutti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //TODO: (1) Create a welcome screen with Tutti Frutti logo
    //TODO: (2) Add a button to enter the application
    //TODO: (3) If the user is logged in, send him to GameSetup screen. If the user is not logged in, send him to GameSetup


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Navigation to Login Activity */
    public void nav_LoginActivity( View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
