package com.example.tuttifrutti;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tuttifrutti.gameutils.GameUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.Manifest.permission.READ_CONTACTS;
import static com.google.firebase.auth.FirebaseAuth.getInstance;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
        implements OnClickListener {
    //COMPLETED_TODO: Set up this screen to work with googlePlay credentials.
    //COMPLETED_TODO: Upon authentication, send user to GameSetup screen
    /* Google Sign In Fields */
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount mGoogleSignInAccount;
    // [START declare_auth]
    public FirebaseAuth mAuth;
    //View to Sign In (Google)
    TextView signInView;

    private static int RC_SIGN_IN = 9001;
    public static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        Log.d(TAG, "Starting Login to GooglePlay...");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(Games.SCOPE_GAMES_LITE)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        signInView = findViewById(R.id.sign_in_view);

        // [END initialize_auth]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener( this);
// Initialize Firebase Auth
        mAuth = getInstance();

    }

    @Override
    public void onClick(View v) {
        int flag=0;
        //if (v.getId() == R.id.sign_in_button) {
           Intent intent = mGoogleSignInClient.getSignInIntent();
           while(flag==0){ try {
                startActivityForResult(intent, RC_SIGN_IN);
                flag=1;
            }
            catch (Exception e)
            {
                continue;
            }
        }

    }

    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI (account);
        if (GoogleSignIn.hasPermissions(mGoogleSignInAccount, Games.SCOPE_GAMES_LITE)) {
            super.onStart();
            updateUI(mGoogleSignInAccount);
        }
        else {
            super.onStart();
        }

            }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
        
    private void updateUI(@NonNull Object o) {
        Context ctx = this;
        Intent intent = new Intent (ctx, PlayGame.class);
//        if(o==null)
//        {recreate();}
        String name = "Player 1";
        if(o!=null) {
            if (o.getClass().equals(GoogleSignInAccount.class)) {
                name = ((GoogleSignInAccount) o).getDisplayName();

                Log.d(TAG, "Logging into firebase.");
                /* Send credentials to Firebase  */
                /*
                AuthCredential credential = GoogleAuthProvider.getCredential(((GoogleSignInAccount) o).getIdToken(), null);
                mAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());

                                }
                            }
                        });
                /* End credentials to Firebase  */
                intent.putExtra(Intent.EXTRA_TEXT, name);
                Log.d(TAG, "Calling PlayGame Intent.");
                startActivity(intent);
                //COMPLETED_TODO: Send the account details over to fire base and check whether the database gets the user details
            } else {
                recreate();
            }
        }
    }

}