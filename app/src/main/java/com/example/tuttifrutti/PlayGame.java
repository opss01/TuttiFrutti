package com.example.tuttifrutti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tuttifrutti.gameutils.GameUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class PlayGame extends AppCompatActivity implements View.OnClickListener {
    //COMPLETED_TODO: For the players established, set up a game
    //TODO: Connect to database
    //TODO: Set up categories based on what's in the database (we only have two)
    //TODO: Set up communications (chat box)
    //TODO: Allow users to fill in two EditBoxes, containing a delimited list of animals/places?
    //TODO: Keep a timer
    //TODO: When timer runs out, EvaluateResponses
    //TODO: Send to GetResults (pass the content of the editboxes)

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.screen_game, R.id.screen_main, R.id.screen_wait
    };
    int mCurrentScreen = -1;

    private static final String TAG="TuttiFrutti";
    private static final long ROLE_ANY = 0x0; // can play in any match.

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    RoomConfig mJoinedRoomConfig;
    RoomConfig mRoomConfig;
    String mRoomId = null;

    // The participants in the current game
    ArrayList<Participant> mParticipants = null;
    // My participant ID in the currently active game
    String mMyId = null;
    GoogleSignInAccount mSignedInAccount = null;
    GoogleSignInClient mGoogleSignInClient;

    // Client used to interact with the real time multiplayer system.
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;
    //Incoming invitation, if there is one
    String mIncomingInvitationId = null;

    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[3]; //2 byte messages, the first byte tells you what type of message
    // Types are: Score_Final(S), Score_Update(U); The second and third bytes contain the score
    // TODO: Think about how message is structured. Add the ability to transmit game messages.

    Button bStartQuickGame;
    Button bCheckInvites;
    Button bSignOut;
    Button bInviteFriends;
    TextView mMagicLetter;
    EditText mFillInWords1;
    EditText mFillInWords2;

    private String mPlayerId;

    // Current state of the game:
    int mSecondsLeft = -1; // how long until the game ends (seconds)
    final static int GAME_DURATION = 30; // game duration, seconds.
    int mScore = 0; // user's current score


    private OnRealTimeMessageReceivedListener mMessageReceivedHandler = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
            byte[] buf = realTimeMessage.getMessageData();
            String sender = realTimeMessage.getSenderParticipantId();
            Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);

            //TODO: Implement handler for message
        }
    };

    private RoomStatusUpdateCallback mRoomStatusCallbackHandler = new RoomStatusUpdateCallback() {

        @Override
        public void onRoomConnecting(@Nullable Room room) {
            Log.d(TAG, "Room connecting..." + room.getRoomId());
            updateRoom(room);
        }

        @Override
        public void onRoomAutoMatching(@Nullable Room room) {
            Log.d(TAG, "Room auto matching " + room.getRoomId());
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {
            //TODO: Implement allowing users to connect to friends, onPeerInvitedToRoom
            updateRoom(room);
        }

        @Override
        public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {
            //TODO: Implement allowing users to connect to friends, method onPeerDeclined
            updateRoom(room);
        }

        @Override
        public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {
            for (String p : list) {
                Log.d(TAG, "Peer joined room(" + room.getRoomId() + "): " + p);
            }

            updateRoom(room);
        }

        @Override
        public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {
            for (String p : list) {
                Log.d(TAG, "Peer left room(" + room.getRoomId() + "): " + p);
            }
            updateRoom(room);
        }

        @Override
        public void onConnectedToRoom(@Nullable Room room) {
            Log.d(TAG, "onConnectedToRoom");

            //get IDs for participants and current player
            mParticipants = room.getParticipants();
            mMyId = room.getParticipantId(mPlayerId);

            // save room ID if its not initialized in onRoomCreated()
            if (mRoomId == null) {
                mRoomId = room.getRoomId();
            }

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "<< CONNECTED TO ROOM>>");
            Log.d(TAG, "Room ID: " + mRoomId);
            Log.d(TAG, "My ID " + mMyId);
        }

        @Override
        public void onDisconnectedFromRoom(@Nullable Room room) {
            mRoomId = null;
            mRoomConfig = null;
            showError();
        }

        @Override
        public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {
            updateRoom(room);
        }

        @Override
        public void onP2PConnected(@NonNull String s) {
            //TODO: What are my supposed to do here?
        }

        @Override
        public void onP2PDisconnected(@NonNull String s) {
            //TODO: What are my supposed to do here?
        }
        void updateRoom(Room room) {
            if (room != null) {
                mParticipants = room.getParticipants();
            }
            if (mParticipants != null) {
                updateGame();
            }
        }

    };

    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback() {

        @Override
        public void onRoomCreated(int i, @Nullable Room room) {
            Log.d(TAG, "onRoomCreated(" + i + ", " + room + ")");
            if (i != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status " + i);
                showError();
                return;
            }

            // save room ID so we can leave cleanly before the game starts.
            mRoomId = room.getRoomId();

            // show the waiting room UI
            switchToScreen(R.id.screen_wait);
        }

        @Override
        public void onJoinedRoom(int i, @Nullable Room room) {
            Log.d(TAG, "onJoinedRoom(" + i+ ", " + room + ")");
            if (i != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + i);
                showError();
                return;
            }
        }

        @Override
        public void onLeftRoom(int i, @NonNull String s) {
            Log.d(TAG, "onLeftRoom(" + i+ ")");
            switchToScreen(R.id.screen_main);
        }

        @Override
        public void onRoomConnected(int i, @Nullable Room room) {
            Log.d(TAG, "onRoomConnected(" + i+ ", " + room + ")");
            if (i != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + i);
                showError();
                return;
            }

            if (room != null) {
                mParticipants = room.getParticipants();
            }
            if (mParticipants != null) {
                showResults();
            }
        }
    };

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Called when we get an invitation to play a game. We react by showing that to the user.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
            // We got an invitation to play a game! So, store it in
            // mIncomingInvitationId
            // and show the popup on the screen.
            mIncomingInvitationId = invitation.getInvitationId();
            ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                    invitation.getInviter().getDisplayName() + " " +
                            getString(R.string.incoming_invitation_text));
            switchToScreen(mCurrentScreen); // This will show the invitation popup
        }

        @Override
        public void onInvitationRemoved(@NonNull String s) {
            //TODO: Implement onInvitationRemoved which gets called when an invitation is removed
        }
    };


    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, string);
            }
        };
    };

    OnRealTimeMessageReceivedListener mOnRealTimeMessageReceivedListener = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
            byte[] buf = realTimeMessage.getMessageData();
            String sender = realTimeMessage.getSenderParticipantId();
            Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]
                    + (int) buf[2]);
            //TODO: Can we send strings to this listener? Find out.

            if (buf[0] == 'S' || buf[0] == 'U') {
                // score final or score update
                //TODO: Update scores or words on display or something
                }

                // TODO: update the words on the screen

                // if it's a final score, mark this participant as having finished
                // the game
                if ((char) buf[0] == 'F') {
                    updateGame();
                    //TODO: Send to results screen
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        bStartQuickGame = findViewById(R.id.quick_game_button);
        bCheckInvites = findViewById(R.id.check_invitations);
        bSignOut = findViewById(R.id.sign_out);
        bInviteFriends = findViewById(R.id.invite_friends);

        //Set up the buttons
        bStartQuickGame.setOnClickListener(this);
        bCheckInvites.setOnClickListener(this);
        bSignOut.setOnClickListener(this);
        bInviteFriends.setOnClickListener(this);

        //Now let's deal with GoogleSignIn so we can call sign out...

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (GoogleSignIn.hasPermissions(mSignedInAccount, Games.SCOPE_GAMES_LITE)) {
//            onSignIn(mSignedInAccount);
            Log.w(TAG, "testing" + mSignedInAccount.getDisplayName());
        }
        else
        {
            GoogleSignIn.getClient(this, gso).getSignInIntent();
        }


        if (!GoogleSignIn.hasPermissions(mSignedInAccount, Games.SCOPE_GAMES_LITE)) {
            Log.d(TAG, "User missing SCOPE_GAMES_LITE, initiating silent login.");
            mGoogleSignInClient.silentSignIn();

            /*
            mGoogleSignInClient.silentSignIn().addOnCompleteListener(
                            this,
                            task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Silent sign in successful.");
                                } else {
                                    Log.d(TAG, "Need to sign out and log back in!!");
                                    mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Sign Out Success");
                                            } else {
                                                handleException(task.getException(), "Sign Out Failed!");
                                            }
                                            Context ctx = PlayGame.this;
                                            Intent intent = new Intent( ctx, LoginActivity.class );
                                            startActivity( intent );
                                        }
                                    });
                                }
                            });
                            */
        }



        //Set up the game clients
        mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, mSignedInAccount);
        mInvitationsClient = Games.getInvitationsClient(PlayGame.this, mSignedInAccount);

        //Finally, go to the main screen
        switchToScreen(R.id.screen_main);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quick_game_button: {
                Log.d(TAG, "Let's start a quick game.");
                startQuickGame();
            }
            case R.id.invite_friends: {
                switchToScreen(R.id.screen_wait);

                // show list of invitable players
                mRealTimeMultiplayerClient.getSelectOpponentsIntent(1, 1).addOnSuccessListener(
                        new OnSuccessListener<Intent>() {
                            @Override
                            public void onSuccess(Intent intent) {
                                startActivityForResult(intent, RC_SELECT_PLAYERS);
                            }
                        }
                ).addOnFailureListener(createFailureListener("There was a problem selecting opponents."));
                break;
            }
            case R.id.check_invitations: {
                switchToScreen(R.id.screen_wait);

                Log.d(TAG, "Clicked on 'Check Invitations'!");

                // show list of pending invitations
                mInvitationsClient.getInvitationInboxIntent().addOnSuccessListener(
                        new OnSuccessListener<Intent>() {
                            @Override
                            public void onSuccess(Intent intent) {
                                startActivityForResult(intent, RC_INVITATION_INBOX);
                            }
                        }
                ).addOnFailureListener(createFailureListener("There was a problem getting the inbox."));
                break;
            }
            case R.id.sign_out: {
                Log.d(TAG, "Sign Out Requested");
                mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Sign Out Success");
                                } else {
                                    handleException(task.getException(), "Sign Out Failed!");
                                }
                                Context ctx = PlayGame.this;
                                Intent intent = new Intent( ctx, LoginActivity.class );
                                startActivity( intent );
                            }
                        });
            }
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        if (mSignedInAccount != googleSignInAccount) {

            mSignedInAccount = googleSignInAccount;

            // update the clients
            //mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleSignInAccount);
            //mInvitationsClient = Games.getInvitationsClient(PlayGame.this, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            mPlayerId = player.getPlayerId();

                            switchToScreen(R.id.screen_main);
                        }
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"));
        }

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

        // get the invitation from the connection hint
        // Retrieve the TurnBasedMatch from the connectionHint
        GamesClient gamesClient = Games.getGamesClient(PlayGame.this,
                googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            Invitation invitation =
                                    hint.getParcelable(Multiplayer.EXTRA_INVITATION);

                            if (invitation != null && invitation.getInvitationId() != null) {
                                // retrieve and cache the invitation ID
                                Log.d(TAG, "onConnected: connection hint has a room invite!");
                                acceptInviteToRoom(invitation.getInvitationId());
                            }
                        }
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the activation hint!"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room
            handleSelectPlayersResult(resultCode, data);

        } else if (requestCode == RC_INVITATION_INBOX) {
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            handleInvitationInboxResult(resultCode, data);

        } else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            if (resultCode == Activity.RESULT_OK) {
                // ready to start playing
                Log.d(TAG, "Starting game (waiting room returned OK).");
                startGame(true);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance). In our game,
                // this means leaving the room too. In more elaborate games, this could mean
                // something else (like minimizing the waiting room UI).
                leaveRoom();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startQuickGame() {
        Log.d(TAG, "startQuickGame()");
        // auto-match criteria to invite one random automatch opponent. Set MIN_OPPONENTS
        // and MAX_OPPONENTS to 1. Set the role to te only role for this game.
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, ROLE_ANY);

/*
        // show list of invitable players
        mRealTimeMultiplayerClient.getSelectOpponentsIntent(1, 1).addOnSuccessListener(
                new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                }
        ).addOnFailureListener(createFailureListener("There was a problem selecting opponents."));
*/
        // build the room config:
        RoomConfig roomConfig =
                RoomConfig.builder(mRoomUpdateCallback)
                        .setOnMessageReceivedListener(mMessageReceivedHandler)
                        .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
                        .setAutoMatchCriteria(autoMatchCriteria)
                        .build();

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Save the roomConfig so we can use it if we call leave().
        mJoinedRoomConfig = roomConfig;

        // create room:
        mSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient
                (this, mSignedInAccount);
        mRealTimeMultiplayerClient.create(roomConfig);

    }

    // Accept an invitation.
    void acceptInviteToRoom(String invitationId) {
        //TODO: FIX THIS acceptInviteToRoom

        // accept invitation
        Log.d(TAG, "Accepting invitation: " + invitationId);

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
                .build();

        switchToScreen(R.id.screen_wait);
        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRealTimeMultiplayerClient.join(mRoomConfig)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Joined room successfully!");
                    }
                });
    }

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurrentScreen = screenId;

        // Now handle invitations...
        boolean showInvitation;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvitation = false;
        } else {
            // you've got an invite!!
            showInvitation = (mCurrentScreen == R.id.screen_main);
        }
        findViewById(R.id.invitation_popup)
                .setVisibility(showInvitation ? View.VISIBLE : View.GONE);
    }

    /* Handle the result of the invitation inbox UI, where the player can pick an invitation
     * to accept. We react by accepting the selected invitation, if any.
     */
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            switchToScreen(R.id.screen_main);
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        if (invitation != null) {
            acceptInviteToRoom(invitation.getInvitationId());
        }
    }

    /* Handle the result of the "Select players UI" we launched when the user clicked the
     * "Invite friends" button. We react by creating a room with those players.
     */
    private void handleSelectPlayersResult(int response, Intent data) {
        //TODO: Implement player selection
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            switchToScreen(R.id.screen_main);
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        switchToScreen(R.id.screen_wait);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .addPlayersToInvite(invitees)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
                .setAutoMatchCriteria(autoMatchCriteria).build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }


    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        String errorString = null;
        switch (status) {
            case GamesCallbackStatusCodes.OK: //Everything is good, yay!
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                errorString = getString(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                errorString = getString(R.string.internal_error);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                errorString = getString(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                errorString = getString(R.string.match_error_locally_modified);
                break;
            default:
                errorString = getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
                break;
        }

        if (errorString == null) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(PlayGame.this)
                .setTitle("Error")
                .setMessage(message + "\n" + errorString)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    // Show error message about game being cancelled and return to main screen.
    void showError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create();

        switchToScreen(R.id.screen_main);
    }

    void showResults() {
        //TODO: Send user to results screen
    }

    void updateGame() {
        //TODO: Implement updateScores
    }

    void broadcastMessage() {
        //TODO: Implement broadcasting of messages between players
    }


    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        mSecondsLeft = GAME_DURATION;
        mScore = 0;
        //mParticipantScore.clear();
        //mFinishedParticipants.clear();
        //TODO: Implement scoring
    }

    /* Game Play implemented here */
    void startGame(boolean multiplayer) {

        /*Set up game*/
        resetGameVars();
        switchToScreen(R.id.screen_game);

        findViewById(R.id.fill_in_words1).setVisibility(View.VISIBLE);
        findViewById(R.id.fill_in_words2).setVisibility(View.VISIBLE);
        findViewById(R.id.magic_letter).setVisibility(View.VISIBLE);

        mMagicLetter = findViewById(R.id.magic_letter);
        mMagicLetter.setText(GameUtils.randomLetter());

        // run the gameTick() method every second to update the game.
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= 0) {
                    return;
                }
                gameTick();
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

    /* Game tick -- update countdown, check if game ended. */
    void gameTick() {
        if (mSecondsLeft > 0) {
           --mSecondsLeft;
        }

        // update countdown
        ((TextView) findViewById(R.id.countdown)).setText("0:" +
                    (mSecondsLeft < 10 ? "0" : "") + String.valueOf(mSecondsLeft));

        if (mSecondsLeft <= 0) {
            // finish game
            findViewById(R.id.fill_in_words1).setVisibility(View.GONE);
            findViewById(R.id.fill_in_words2).setVisibility(View.GONE);
            broadcastMessage();
        }
    }

    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        mSecondsLeft = 0;
        // prevent screen from sleeping during handshake
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (mRoomId != null) {
            mRealTimeMultiplayerClient.leave(mRoomConfig, mRoomId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRoomId = null;
                            mRoomConfig = null;
                        }
                    });
            switchToScreen(R.id.screen_wait);
        } else {
            switchToScreen(R.id.screen_main);
        }
    }



}
