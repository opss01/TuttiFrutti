package com.example.tuttifrutti.gameutils;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GameUtils {

    public static boolean isLoggedIn(Activity activity) {
        //TODO: Return true if user is logged in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null) {
            return false;
        } else {
            return true;
        }
    }
}
