package com.example.tuttifrutti.gameutils;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.tuttifrutti.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

public class GameUtils {
    private static final String TAG = "GameUtils";
    private static FirebaseFirestore db = null;
    static List<String> fieldValues = new ArrayList<>();

    private final static char firstLetterOfAlphabet = 'A';
    private final static int numLettersInAlphabet = 26;

    /* TODO: DELETE */
    /*
    public final static String SCORE_KEY="score";
    public final static String PLAYER_SCORE_KEY="playerScore";
    public final static String PLAYER_NAME_KEY="name";
    */

    public final static String CURRENT_PLAYER_KEY="currentPlayer";
    public final static String OTHER_PLAYER_KEY="otherPlayer";
    public final static String CURRENT_PLAYER_SCORE_KEY="currentPlayerScore";
    public final static String OTHER_PLAYER_SCORE_KEY="otherPlayerScore";


//    GameUtils(FirebaseFirestore db) {
//        this.db = db;
//    }
    public static boolean isLoggedIn(Activity activity) {
        //COMPLETED_TODO: Return true if user is logged in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null || !GoogleSignIn.hasPermissions(account, Games.SCOPE_GAMES_LITE)) {
            return false;
        } else {
            return true;
        }
    }

    public static Object dbSetup () {
        // Access a Cloud Firestore instance from your Activity
        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
        return db;

    }


    public static int getScoreForCategory (String category, String responses) {
        //TODO: Implement a method that for a given category, validates responses in DB.
        // method should send back the number of correct responses.
        return 3;
    }

    /* TODO: DELETE */
    /*
    public static String getTopScore() {
        int score = 0;
        return "You scored " + score + " points!";
    }

    public static String getWinnerAlert() {
        int player = 1;
        return "Player " + player + "Won!";
    }
*/
    /* getCollectionFieldValues
    Input: String c = collection name, f = field value
    Output: List of all the values of that field in the collection specified. (unique)

    Note: There is some checks in case the list is empty etc.
     */
    /*
    public List<String>  getCollectionFieldValues(String c, final String f) throws InterruptedException {

        db = (FirebaseFirestore) dbSetup(); //There may be a synchronization issue.
          Task<QuerySnapshot> mtask=  db.collection( c ).get();
//          mtask.addOnSuccessListener( new OnSuccessListener(null){
//              public void onSuccess(Task<QuerySnapshot> task )  {
//                  List<String> fieldValues = new ArrayList<>();
//
//                  Log.d( TAG, "onComplete" );
//                  if (task.isSuccessful()) {
//                      for (QueryDocumentSnapshot document : task.getResult()) {
//                          Log.d( TAG, document.getId() + " => " + document.get( f ) );
////                                DocumentSnapshot documentCat = task.getResult();
////                                Log.d(TAG, document.getId() + " => " + document.getData());
//                          fieldAdds( (String) document.get( f ) );
//
//
//                      }
//                  } else {
//                      Log.w( TAG, "Error getting documents.", task.getException() );
//                  }
//              }        } )
//     ;
        boolean a = false;

        a = mtask.addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {


            @Override
            public void onComplete ( @NonNull Task<QuerySnapshot> task ) {
            List<String> fieldValues = new ArrayList<>();

            Log.d( TAG, "onComplete" );
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d( TAG, document.getId() + " => " + document.get( f ) );
            //                                DocumentSnapshot documentCat = task.getResult();
            //                                Log.d(TAG, document.getId() + " => " + document.getData());
                    fieldAdds( (String) document.get( f ) );
                }
            } else {
                Log.w( TAG, "Error getting documents.", task.getException() );
            }
            }
        } ).isSuccessful();


        return new ArrayList (new HashSet(fieldValues));
    }

    public static List<String>  getCollectionFieldValues2 (String c, final String f) throws InterruptedException {

        db = (FirebaseFirestore) dbSetup();
        Source source = Source.SERVER;
        CollectionReference coll = db.collection("categories");
        Query query = coll.whereEqualTo("category",true);
        Task<QuerySnapshot> t = query.get(source);
        Log.d(TAG, "Testing2 : "+ query.get() + " => ");
        //showLoading();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete( Task<QuerySnapshot> task) {
                Log.d(TAG,"Testing2: OnComplete");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "Testing2 : "+ document.getId() + " => " + document.toString() + "--" + document.getId());
//
                    }
                }
                else
                {
                    Log.d(TAG,"Testing2: Not Succesful");
                }
                //hideLoading();
            }
        });
//        if (t.isSuccessful()) {
//        for (QueryDocumentSnapshot document : t.getResult()) {
//            Log.d(TAG, "Testing2 : "+ document.getId() + " => " + document.getData());
////            fieldAdds( (String) document.get( f ) );
//        }
//        }
        Log.w(TAG,"Testing2 : out of loop" + query.get(source));
        return new ArrayList (new HashSet(fieldValues));
    }
    private static void fieldAdds(String f)
    {
        fieldValues.add(f);
    }
/* getSomeCategories
Input: A list of unique(preferably) categories, the number of categories you need.
Output: after shuffling the list it will pick the first ones in the list up to the number in the
second input.

Note: There is some checks in case the list is empty etc.
 */
    /*
    public static List<String> getSomeCategories ( List<String> allCats, int n ) {
        if (allCats.size() < n && allCats.size() != 0 ) { n = allCats.size();}
        else if (allCats.size() == 0) { return allCats;}
        List<String> fList = new LinkedList<String>(allCats);
        Collections.shuffle(fList);
        return fList.subList(0, n);
    }
*/
    /* Returns n number of categories based on whatever is in the database */
    public static List<String> getRandomCategories (int n) {
        //TODO: Implement getRandomCategories
        ArrayList<String> holdValues = new ArrayList<String> ();
        holdValues.add("Animals");
        holdValues.add("Places");
        return (holdValues);
    }

    public static char randomLetter (){
        Random r = new Random();
        int nextRand = r.nextInt(numLettersInAlphabet);
        return (char) (firstLetterOfAlphabet + nextRand);
        //return sequence.remove( nextRand );
    }
}
