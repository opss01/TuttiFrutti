package com.example.tuttifrutti;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tuttifrutti.gameutils.GameUtils;

import org.w3c.dom.Text;

public class GetResults extends AppCompatActivity {
    //TODO: Pull content of EditBoxes, calculate it and display them to both users
    //COMPLETEDTODO: Display score
    //COMPLETEDTODO: Declare Winner
    //COMPLETEDTODO: Give option to start a new game
    //Debugging Tag
    private static final String TAG="GetResults";
    //All Objects init
    TextView txt_winneralert;
    TextView txt_scorealert;
    Button startNewGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Layout
        setContentView(R.layout.activity_get_results);
        //Get objects
        txt_winneralert = (TextView) findViewById(R.id.txt_winner);
        txt_scorealert = (TextView) findViewById(R.id.txt_score);
        startNewGame = (Button) findViewById( R.id.btn_new_game );
        //Assign values TextView objects
        txt_scorealert.setText(GameUtils.getTopScore());
        txt_winneralert.setText(GameUtils.getWinnerAlert());


        startNewGame.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick ( View v ) {

                    Log.d(TAG, "Let's play again!");
                    Context ctx = GetResults.this;
                    Intent intent = new Intent( ctx, PlayGame.class );
                    startActivity( intent );
                }
            }
        );

    }
}
