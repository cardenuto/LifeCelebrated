package info.anth.lifecelebrated.login;

import info.anth.lifecelebrated.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;
import com.google.android.gms.auth.api.Auth;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends FirebaseLoginBaseActivity {

    public static final int RESULT_REQUEST_CODE = 1;

    public static final String LOG_TAG = LoginActivity.class.getSimpleName();
    public static final Boolean LOG_SHOW = false;

    private Firebase mRef;
    private static String pathUserMap;
    private static String pathUsers;
    private static String pathUserInfo;
    private info.anth.lifecelebrated.login.LoginRegisterDialog loginRegisterDialog;
    private Context context;
    private Boolean creatingAccount;

    private View mRootView;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRootView = findViewById(R.id.login_root);

        mRef = new Firebase(getResources().getString(R.string.FIREBASE_BASE_REF));
        pathUserMap = getResources().getString(R.string.FIREBASE_USER_MAP);
        pathUsers = getResources().getString(R.string.FIREBASE_USERS);
        pathUserInfo = getResources().getString(R.string.FIREBASE_USER_INFO);
        context = this;
        mActivity = this;

        // set login button listener
        Button buttonLogin = (Button) findViewById(R.id.activity_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickLogin();
            }
        });

        // set cancel button listener
        Button buttonCancel = (Button) findViewById(R.id.activity_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickCancel();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        logoutLoginActivity(mRef,this);

        //TODO: (Optional) set password authentication providers (example set for google and password)
        //setEnabledAuthProvider(AuthProviderType.FACEBOOK);
        //setEnabledAuthProvider(AuthProviderType.TWITTER);
        setEnabledAuthProvider(AuthProviderType.GOOGLE);
        setEnabledAuthProvider(AuthProviderType.PASSWORD);

        creatingAccount = false;
        showFirebaseLoginPrompt();
        loginRegisterDialog = new info.anth.lifecelebrated.login.LoginRegisterDialog();
    }

    @Override
    public void onFirebaseLoggedIn(AuthData authData) {
        View loginContentView = mRootView.findViewById(R.id.login_content);
        ProgressBar loginProgressBar = (ProgressBar) mRootView.findViewById(R.id.login_progressbar);

        loginContentView.setVisibility(View.GONE);
        loginProgressBar.setVisibility(View.VISIBLE);

        if(LOG_SHOW) Log.i(LOG_TAG, "Logged in using " + authData.getProvider());

        // returned data
        if(LOG_SHOW) Log.i(LOG_TAG, "Login data: " + authData.toString());

        // This code fires when a user is newly created using the LoginRegisterDialog
        // because it contains an Auth listener. User creation is handled by the dialog code
        //
        // If the account is not being created: check to see if the user exists
        // if not, add user to database, if it does completeLogin (called from
        // checkForUser because of listener)
        if(!creatingAccount) checkForUser(authData);
    }

    /**
     * Split out completeLogin from the onFirebaseLoggedIn code
     *
     * This was due to a timing issue caused by checkForUser
     * checkForUser has a listener that is needs to wait for, this code should execute when it is done.
     * Also called from LoginRegisterDialog when the newly registered user is logged in
     *
    **/
    public void completeLogin() {
        // Check for intent for result (if called by startActivity getCallingActivity is null, called by startActivityForResult is NOT null)
        if (getCallingActivity() != null) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
        }
        finish();
    }

    @Override
    public void onFirebaseLoggedOut() {
        if(LOG_SHOW) Log.i(LOG_TAG, "Logged out");
        // Reset login shared preferences
        //clearLoginSharedPreferences(this);
    }

    @Override
    public void onFirebaseLoginProviderError(FirebaseLoginError firebaseError) {
        if(LOG_SHOW) Log.e(LOG_TAG, "Login provider error: " + firebaseError.toString());
        resetFirebaseLoginPrompt();
    }

    @Override
    public void onFirebaseLoginUserError(FirebaseLoginError firebaseError) {
        if(LOG_SHOW) Log.e(LOG_TAG, "Login user error: " + firebaseError.toString());


        Context context = getApplicationContext();
        CharSequence text = firebaseError.message;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        resetFirebaseLoginPrompt();
    }

    @Override
    public Firebase getFirebaseRef() {
        return mRef;
    }

    // Create a new account
    public void createAccount(View view){
        creatingAccount = true;
        dismissFirebaseLoginPrompt();
        loginRegisterDialog.show(getFragmentManager(), "");
    }

    // Reset Password
    public void resetPassword(View view){
        // Get email account - reset view is inside a linear layout before it shares a parent with email
        View viewParent = (View) view.getParent().getParent();
        EditText emailView = (EditText) viewParent.findViewById(R.id.email);
        if(LOG_SHOW) Log.i(LOG_TAG, "viewParent value: " + String.valueOf(viewParent));

        if (emailView != null) {
            String email = emailView.getText().toString();
            if(LOG_SHOW) Log.i(LOG_TAG, "Email view value: " + email);

            if (!email.equals("")) {
                // request reset email be sent
                mRef.resetPassword(email, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        Context context = getApplicationContext();
                        CharSequence text = getResources().getString(R.string.reset_email_sent);
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Context context = getApplicationContext();
                        CharSequence text = firebaseError.getMessage();
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            } else {
                Context context = getApplicationContext();
                CharSequence text = getResources().getString(R.string.reset_email_required);
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }


    // Return to login dialog when back button is pressed
    public void clickLogin(){
        creatingAccount = false;
        showFirebaseLoginPrompt();
    }

    // Cancel activity - no login
    public void clickCancel(){
        // Check for intent for result (if called by startActivity getCallingActivity is null, called by startActivityForResult is NOT null)
        if (getCallingActivity() != null) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }

    public void checkForUser(final AuthData authData) {
        final String[] auid = new String[1];

        // Check to see if they exist
        mRef.child(pathUserMap).child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    // user does not exist - create user
                    auid[0] = addUserInfo(authData);
                } else {
                    auid[0] = (String) dataSnapshot.getValue();
                }
                // Set the full path for local data
                Firebase refFull;
                if (pathUserInfo.isEmpty()) {
                    refFull = mRef.child(pathUsers).child(auid[0]);
                } else {
                    refFull = mRef.child(pathUsers).child(auid[0]).child(pathUserInfo);
                }
                // Populate the local data
                populateDataLocally(refFull, authData.getUid(), auid[0], mActivity);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // there was an error
                CharSequence text = firebaseError.getMessage();
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public String addUserInfo(AuthData authData) {
        //TODO: (Optional) Update for any new fields added to DbUserInfo class
        // set user id
        String uid = authData.getUid();

        // create record
        String provider = authData.getProvider();
        String email = getResources().getString(R.string.missing_user_data);
        String profileImageUrl = getResources().getString(R.string.missing_user_data);
        String displayName = getResources().getString(R.string.missing_user_data);

        if(authData.getProviderData().containsKey("email")) email = authData.getProviderData().get("email").toString();
        if(authData.getProviderData().containsKey("profileImageURL")) profileImageUrl = authData.getProviderData().get("profileImageURL").toString();
        if(authData.getProviderData().containsKey("displayName")) displayName = authData.getProviderData().get("displayName").toString();

        // define users
        DbUserInfo newUserInfo = new DbUserInfo(provider, email, profileImageUrl, displayName);
        Firebase pushUser = mRef.child(pathUsers).push();
        // If the user added a grouping path add it
        if (pathUserInfo.isEmpty()) {
            // no path
            pushUser.setValue(newUserInfo);
        } else {
            // path added
            pushUser.child(pathUserInfo).setValue(newUserInfo);
        }
        // define userMap
        populateUserMap(mRef, uid, pushUser.getKey());

        return pushUser.getKey();
    }

    /**
     * populateUserMap - populates the userMap node of the Firebase database.
     *
     * @param ref - generic so it can be called by other programs (LoginRegisterDialog)
     * @param uid - the delivered user ID from Firebase
     * @param auid - Application user ID.
     *
     */
    public static void populateUserMap(Firebase ref, String uid, String auid) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(uid, auid);
        ref.child(pathUserMap).updateChildren(updateMap);
    }

    public void populateDataLocally(Firebase refFull, final String uid, final String auid, final Activity activity) {
        // Reset login shared preferences
        refFull.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DbUserInfo dbUserInfo = dataSnapshot.getValue(DbUserInfo.class);

                if (dbUserInfo != null) {
                    LocalUserInfo localUserInfo = new LocalUserInfo(uid,auid,dbUserInfo.getEmail(),dbUserInfo.getDisplayName(),dbUserInfo.getProfileImageUrl());
                    localUserInfo.saveValues(activity);
                } else {
                    LocalUserInfo localUserInfo = new LocalUserInfo(uid,auid,null,getString(R.string.preference_missing_error),null);
                    localUserInfo.saveValues(activity);
                }
                // finish the login process once the listener is done
                completeLogin();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // there was an error
                LocalUserInfo localUserInfo = new LocalUserInfo(uid,auid,null,firebaseError.getMessage(),null);
                localUserInfo.saveValues(activity);
                // finish the login process once the listener is done
                completeLogin();
            }
        });
    }

    public static void logoutLoginActivity(Firebase ref, Activity activity) {
        ref.unauth();
        LocalUserInfo localUserInfo = new LocalUserInfo(activity);
        localUserInfo.clearValues(activity);
    }
}
