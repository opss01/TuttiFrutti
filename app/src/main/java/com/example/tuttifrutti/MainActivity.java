package com.example.tuttifrutti;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tuttifrutti.gameutils.GameUtils;

public class MainActivity extends AppCompatActivity {
    //COMPLETED_TODO: (1) Create a welcome screen
    //COMPLETED_TODO: (2) Add a button to enter the application

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
                        if (GameUtils.isLoggedIn()) {
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
}
