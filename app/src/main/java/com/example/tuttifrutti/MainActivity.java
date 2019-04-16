package com.example.tuttifrutti;

import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View;
import android.widget.Button;

import com.example.tuttifrutti.gameutils.GameUtils;

public class MainActivity extends AppCompatActivity {
    //COMPLETED_TODO: (1) Create a welcome screen
    //COMPLETED_TODO: (2) Add a button to enter the application
    //TODO: (1) Create a welcome screen with Tutti Frutti logo - Done
    //TODO: (2) Add a button to enter the application - Done
    //TODO: (3) If the user is logged in, send him to GameSetup screen. If the user is not logged in, send him to GameSetup - Still In Progress.

    Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterButton = (Button) findViewById(R.id.enter_button);
        enterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //COMPLETED_TODO: (3) If the user is logged in, send him to GameSetup screen.
                        //COMPLETED_TODO: (4) If the user is not logged in, send him to LoginActivity
                        if (GameUtils.isLoggedIn(MainActivity.this)) {
                            Context ctx = MainActivity.this;
                            Intent intent = new Intent(ctx, GameSetup.class);
                            startActivity(intent);
                        } else {
                            Context ctx = MainActivity.this;
                            Intent intent = new Intent(ctx, LoginActivity.class);
                            startActivity(intent);
                        }

                    }
                }



        );
    }

    /** Navigation to Login Activity */
    public void nav_LoginActivity( View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
