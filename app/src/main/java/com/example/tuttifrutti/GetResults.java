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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class GetResults extends AppCompatActivity {
    //COMPLETEDTODO: Display score
    //COMPLETEDTODO: Declare Winner
    //COMPLETEDTODO: Give option to start a new game
    //Debugging Tag
    private static final String TAG="GetResults";
    //All Objects init
    TextView txt_winneralert;
    TextView txt_scorealert;
    Button startNewGame;
    Map<String, Integer> scoreBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //COMPLETED_TODO: Pull Score from PlayGame and display it

        super.onCreate(savedInstanceState);
        //Get Layout
        setContentView(R.layout.activity_get_results);
        //Get objects
        txt_winneralert = (TextView) findViewById(R.id.txt_winner);
        txt_scorealert = (TextView) findViewById(R.id.txt_score);
        startNewGame = (Button) findViewById( R.id.btn_new_game );

        //Assign values TextView objects
        String currentPlayer = this.getIntent().getStringExtra(GameUtils.CURRENT_PLAYER_KEY);
        String otherPlayer = this.getIntent().getStringExtra(GameUtils.OTHER_PLAYER_KEY);
        String curPScoreString = this.getIntent()
                .getStringExtra(GameUtils.CURRENT_PLAYER_SCORE_KEY);
        String otPScoreString = this.getIntent()
                .getStringExtra(GameUtils.OTHER_PLAYER_SCORE_KEY);

        Log.d(TAG, "Current Player (" + currentPlayer + ") Score: " + curPScoreString);
        Log.d(TAG, "Other Player (" + otherPlayer + ") Score: " + otPScoreString);

        int currentPlayerScore = Integer.parseInt(curPScoreString.trim());
        int otherPlayerScore = Integer.parseInt(otPScoreString.trim());

        String winner = "";
        int topScore = 0;
        boolean tie = false;

        if (currentPlayerScore > otherPlayerScore) {
            winner = currentPlayer;
            topScore = currentPlayerScore;
        } else if (otherPlayerScore > currentPlayerScore) {
            winner = otherPlayer;
            topScore = otherPlayerScore;
        } else {
            tie = true;
            topScore = currentPlayerScore;
        }

        if (tie) { txt_scorealert.setText("There was a tie!!"); }
        else { txt_scorealert.setText(winner + " won with " + topScore + " points!"); }

        txt_winneralert.setText("You scored " + currentPlayerScore + " points");


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
