package info.anth.lifecelebrated;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.client.Firebase;

import info.anth.lifecelebrated.login.LocalUserInfo;
import info.anth.lifecelebrated.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRef = new Firebase(getResources().getString(R.string.FIREBASE_BASE_REF));
    }

    public void callStep0(View view) {
        Context context = view.getContext();
        startStepActivity(context, 0);
    }

    public void startStepActivity(Context context, int stepNo){
        Intent intent = new Intent(context, StepsActivity.class);
        intent.putExtra(StepsActivity.REQUEST_CURRENT_STEP, stepNo);
        context.startActivity(intent);
    }

    public void callEditIntent(View view){
        Intent intent = new Intent(view.getContext(), EditActivity.class);
        startActivity(intent);
    }

    // *** Manage database connection START ***
    // Place login on resume
    @Override
    public void onResume() {
        super.onResume();
        if (mRef.getAuth()==null) {
            startActivityForResult(new Intent(this, LoginActivity.class), LoginActivity.RESULT_REQUEST_CODE);
        }

        // test installation
        //String deviceId = Installation.id(this);
        //Log.i("ajc", "Device ID: " + deviceId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LoginActivity.RESULT_REQUEST_CODE) {

            if(resultCode == Activity.RESULT_OK){
                //String result=data.getStringExtra("result");
                LocalUserInfo user = new LocalUserInfo(this);
                Log.i("ajc", "Login successful: " + user.toString());
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                // Login was cancelled therefore cancel this activity
                finish();
            }
        }
    }

    public void callLogout (View view) {
        // logout
        LoginActivity.logoutLoginActivity(mRef, this);
        if (mRef.getAuth() == null) {
            Log.i("ajc", "Logout successful");
            onResume();
        }
    }
    // *** Manage database connection END ***
}
