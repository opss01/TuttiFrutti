package com.example.tuttifrutti;
import com.example.tuttifrutti.gameutils.GameUtils;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tuttifrutti.gameutils.GameUtils;
import com.google.android.gms.games.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.example.tuttifrutti.gameutils.GameUtils.getCategoryValuesFromDB;
import static com.example.tuttifrutti.gameutils.GameUtils.getRandomCategories;
import static com.example.tuttifrutti.gameutils.GameUtils.getScoreForCategory;
//import static com.example.tuttifrutti.gameutils.GameUtils.readData;

public class MainActivity extends AppCompatActivity {
    //COMPLETED_TODO: (1) Create a welcome screen
    //COMPLETED_TODO: (2) Add a button to enter the application
    //COMPLETED_TODO: (3) If the user is logged in, send him to GameSetup screen. If the user is not logged in, send him to GameSetup - Still In Progress.
    //NOTCOMPLETEDBUTWHATEVSTODO: (Cynthia) Move the database to gameutils
    private static final String TAG = "MainActivity";
    Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        GameUtils.setUpCategories();

        enterButton = (Button) findViewById( R.id.enter_button );
        enterButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick ( View v ) {
                        //COMPLETED_TODO: (3) If the user is logged in, send him to GameSetup screen.
                        //COMPLETED_TODO: (4) If the user is not logged in, send him to LoginActivity
                        if (GameUtils.isLoggedIn( MainActivity.this )) {
                            Log.d(TAG, "User already logged in, sending to PlayGame.");
                            Context ctx = MainActivity.this;
                            Intent intent = new Intent( ctx, PlayGame.class );
                            startActivity( intent );
                        } else {
                            Log.d(TAG, "User needs to log in, sending to LoginActivity.");
                            Context ctx = MainActivity.this;
                            Intent intent = new Intent( ctx, LoginActivity.class );
                            startActivity( intent );
                        }

                    }
                }


        );

//        getCategoryValuesFromDB("Animals");
        // Testing Database
//        getScoreForCategory("Colors","amarillo\r\namber\r\namethyst\r\nbo","A");

//        List<String> allCats = null;
//        try {
//            allCats = GameUtils.getCollectionFieldValues2( "categories", "category" );
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        List<String> someCats = GameUtils.getSomeCategories( allCats, 2 );
//        Log.w( TAG, "outofTestingIn in cities" + allCats );

    }

    /** Navigation to Login Activity */
//    public void nav_LoginActivity( View view) {
//        Intent i = new Intent(this, LoginActivity.class);
//        startActivity(i);
//    }
}
