package info.anth.lifecelebrated;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import info.anth.lifecelebrated.AddLocationSteps.BlankFragment;
import info.anth.lifecelebrated.AddLocationSteps.EditBoxFragment;
import info.anth.lifecelebrated.AddLocationSteps.EditBoxFragment2;
import info.anth.lifecelebrated.AddLocationSteps.ImageFragment;
import info.anth.lifecelebrated.AddLocationSteps.NamesFragment;
import info.anth.lifecelebrated.AddLocationSteps.VerifyLocationFragment;
import info.anth.lifecelebrated.Data.DbLocationEditList;
import info.anth.lifecelebrated.Data.DbLocationMap;
import info.anth.lifecelebrated.Data.DbLocationMaster;
import info.anth.lifecelebrated.Data.DbLocationStatusIC;
import info.anth.lifecelebrated.Services.ObtainGPSDataService;
import info.anth.lifecelebrated.login.LocalUserInfo;

public class StepsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String REQUEST_CURRENT_STEP = "current_step";
    public static final String REQUEST_CURRENT_LOCATION = "current_location";
    public static final String REQUEST_CURRENT_EDIT_LIST_ITEM = "edit_list_item";

    public static final String LOG_TAG = StepsActivity.class.getSimpleName();

    int currentStep;
    int priorStep;
    Stack<Integer> pageHistory;
    boolean saveToHistory;
    private static Context context;

    private static Firebase mDbLocationMasterRef;
    private static Firebase mDbLocationMapRef;
    private static Firebase mDbLocationEditListRef;
    private static Firebase mDbLocationStatusICRef;
    private ValueEventListener valueEventListenerStatus;

    //private static Boolean locationNeeded = false;

    private String currentEditListItem;

    //public static ProgressBar progressBar;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        context = this;

        /*
        // Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.overall_progress);

        // Set listener
        progressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int calcPage = (int) event.getX() / (progressBar.getWidth() / getResources().getInteger(R.integer.step_count));
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.i("ajc","Touch coordinates - x: " +
                            String.valueOf(event.getX()) + " y: " + String.valueOf(event.getY())
                            + " width: " + String.valueOf(progressBar.getWidth())
                            + " page: " + String.valueOf(calcPage)
                    );
                    mViewPager.setCurrentItem(calcPage);
                }
                return false;
            }
        });
        */

        //getDbCount();
        String currentLocation = getIntent().getStringExtra(REQUEST_CURRENT_LOCATION);
        currentEditListItem = getIntent().getStringExtra(REQUEST_CURRENT_EDIT_LIST_ITEM);

        Log.i("ajc2","mDbLocationMasterRef: " + String.valueOf(mDbLocationMasterRef) +
                " mDbLocationMapRef: " + String.valueOf(mDbLocationMapRef) +
                " mDbLocationStatusICRef: " + String.valueOf(mDbLocationStatusICRef) +
                " mDbLocationEditListRef: " + String.valueOf(mDbLocationEditListRef) +
                " savedInstanceState: " + String.valueOf(savedInstanceState));

        // data was not saved so no need for savedInstanceState logic
        if (savedInstanceState == null) {

            // ------------------------------------
            // Manage database
            //
            String firebasePath = getResources().getString(R.string.FIREBASE_BASE_REF);
            // add path to users
            firebasePath += "/" + getResources().getString(R.string.FIREBASE_USERS);
            // add application user id (AUID)
            LocalUserInfo userInfo = new LocalUserInfo(this);
            firebasePath += "/" + userInfo.auid;

            Firebase firebaseEditRef = new Firebase(firebasePath).child(getResources().getString(R.string.FIREBASE_CHILD_EDIT_DETAILS));
            Firebase firebaseEditListRef = new Firebase(firebasePath).child(getResources().getString(R.string.FIREBASE_CHILD_EDIT_LIST));


            if (currentLocation == null || currentEditListItem == null) {
                Firebase pushRefLocation = firebaseEditRef.push();
                mDbLocationMasterRef = pushRefLocation.child("master");
                mDbLocationMapRef = pushRefLocation.child("map");
                mDbLocationStatusICRef = pushRefLocation.child("statusIC");

                DbLocationMaster newDbLocationMaster = DbLocationMaster.columns.createBlank(this);
                mDbLocationMasterRef.setValue(newDbLocationMaster);

                DbLocationMap newDbLocationMap = DbLocationMap.columns.createBlank();
                mDbLocationMapRef.setValue(newDbLocationMap);

                DbLocationStatusIC newDbLocationStatusIC = DbLocationStatusIC.columns.createDefaults(this);
                mDbLocationStatusICRef.setValue(newDbLocationStatusIC);

                DbLocationEditList newListItem = DbLocationEditList.columns.createBlank(pushRefLocation.getKey(), this);
                mDbLocationEditListRef = firebaseEditListRef.push();
                mDbLocationEditListRef.setValue(newListItem);
            } else {
                mDbLocationMasterRef = firebaseEditRef.child(currentLocation).child("master");
                mDbLocationMapRef = firebaseEditRef.child(currentLocation).child("map");
                mDbLocationStatusICRef = firebaseEditRef.child(currentLocation).child("statusIC");
                mDbLocationEditListRef = firebaseEditListRef.child(currentEditListItem);
            }

            //
            // ------------------------------------

            // ------------------------------------
            // Call Location Service
            //
            if (currentLocation == null) findLocation();
            // HERE
            //if (currentLocation == null) locationNeeded = true;
            //
            // ------------------------------------
        // end save instance state logic - commented out
        }
        //checkPermissionExternal();
        finishCreate();
    }

    protected void finishCreate() {
        /*
        if (savedInstanceState == null) {
            // ------------------------------------
            // Call Location Service
            //
            if (currentLocation == null) findLocation(this);
            //
            // ------------------------------------
        }
        */

        // new
        /*
        if (locationNeeded) {
            locationNeeded = false;
            findLocation(this);
        }
        */
        // end new

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
            setLocationStatusListener();
        }

        currentStep = getIntent().getIntExtra(REQUEST_CURRENT_STEP, 0);
        mViewPager.setCurrentItem(currentStep);

        pageHistory = new Stack<>();
        saveToHistory = true;
        // setup the value on creation of the activity
        priorStep = currentStep;

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i("ajcScroll", "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i("ajcScroll", "onPageSelected");
                if (saveToHistory) {
                    pageHistory.push(priorStep);
                    priorStep = position;
                }
                //progressBar.setProgress(position+1);
                /*
                ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", priorStep+1, position+1 );

                // see this max value coming back here, we animate towards that value
                animation.setDuration(1000); //in milliseconds
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
                //priorProgress = position;
                */
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.i("ajcScroll", "onPageScrollStateChanged");
                //if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    //if (mViewPager.getCurrentItem() == 0)
                    {
                        // Hide the keyboard.
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
                    }
                }
            }
        });

    }

    // ------------------------------------
    // Database functions
    // ------------------------------------

    public void setLocationStatusListener() {
        valueEventListenerStatus = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DbLocationStatusIC thisLocationStatus = snapshot.getValue(DbLocationStatusIC.class);

                if (thisLocationStatus != null) {
                    if(mTabLayout != null) {
                        int stepsArray[] = getResources().getIntArray(R.array.step_array_no);
                        for ( int i = 0; i < mTabLayout.getTabCount(); i++)
                        {
                            String columnName = getResources().getStringArray(R.array.step_db_column)[stepsArray[i]];
                            TabLayout.Tab tab = mTabLayout.getTabAt(i);
                            if (tab != null) {
                                // set default icon using database value
                                String drawableName = DbLocationStatusIC.columns.getColumnValue(columnName, thisLocationStatus);
                                int drawableInt = getResources().getIdentifier(drawableName,"drawable",getPackageName());
                                if (drawableInt != 0) tab.setIcon(drawableInt);
                            }
                        }
                    }
                } else {
                    // TODO remove temp code (including function)
                    tempAddBlank();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        };

        mDbLocationStatusICRef.addValueEventListener(valueEventListenerStatus);
    }

    // Temp to clean up past records
    private void tempAddBlank() {
        DbLocationStatusIC newDbLocationStatusIC = DbLocationStatusIC.columns.createDefaults(this);
        mDbLocationStatusICRef.setValue(newDbLocationStatusIC);
    }

    // End Database
    // ------------------------------------

    // ------------------------------------
    // Location functions
    // ------------------------------------

    // wrapper to check permission
    protected void findLocation() {
        checkPermissionLocation();
    }

    // wrapper to check settings
    protected void findLocationAllowed() {
        checkLocationSettings();
    }

    // Call Location Service
    protected static void findLocationAllowedSettings() {
        Intent myIntent = new Intent(context, ObtainGPSDataService.class);
        myIntent.putExtra(ObtainGPSDataService.REQUEST_REF_LOCATION_MAP, mDbLocationMapRef.getRef().toString());
        context.startService(myIntent);
    }

    // End Location
    // ------------------------------------

    // ------------------------------------
    // Pager code
    // ------------------------------------

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Resources rw = getResources();
            int stepArrayNo = rw.getIntArray(R.array.step_array_no)[position];
            String stepFragment = rw.getStringArray(R.array.step_fragment)[stepArrayNo];

            switch (stepFragment) {
                case "BlankFragment":
                    return BlankFragment.newInstance(position);
                case "EditBoxFragment":
                    return EditBoxFragment.newInstance(position, mDbLocationStatusICRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "EditBoxFragment2":
                    return EditBoxFragment2.newInstance(position, mDbLocationStatusICRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "ImageFragment":
                    return ImageFragment.newInstance(position, mDbLocationStatusICRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "NamesFragment":
                    return NamesFragment.newInstance(position, mDbLocationStatusICRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "VerifyLocationFragment":
                    return VerifyLocationFragment.newInstance(position, mDbLocationStatusICRef.getRef().toString(),
                            mDbLocationMapRef.getRef().toString());
            }

            return BlankFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show total pages.
            return getResources().getInteger(R.integer.step_count);
        }
    }

    // End Pager
    // ------------------------------------

    // ------------------------------------
    // Action Bar / Options menu code
    // ------------------------------------

    // Create submenu for steps
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_steps, menu);
        SubMenu temp = menu.addSubMenu(0,700,10,"Jump to a Step");
        int stepsArray[] = getResources().getIntArray(R.array.step_array_no);
        for ( int i = 0; i <= stepsArray.length - 1; i++)
        {
            int j = i+1;
            String message = "Step " + j + ": " + getResources().getStringArray(R.array.step_title)[stepsArray[i]];
            temp.add(0,i,i,message);
        }
        return true;
    }

    // Override return to manifest declared parent.
    // Activity used for ADD and EDIT
    // Add - returns to Main page (manifest default)
    // Edit - needs to return to edit page
    //
    // items < 5 (or step count) jump to that step
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.i("ajc", "selected: " + item.getItemId());
        if (item.getItemId() < getResources().getInteger(R.integer.step_count)) {
            mViewPager.setCurrentItem(item.getItemId());
            return true;
        } else {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    if (currentEditListItem != null) {
                        // finish works here because the entry point is the EditActivity
                        finish();
                        return true;
                    } else {
                        return super.onOptionsItemSelected(item);
                    }
            }
            return super.onOptionsItemSelected(item);
        }
    }

    // End Action option menu
    // ------------------------------------

    // ------------------------------------
    // Other Code
    // ------------------------------------

    // Override the onBackPress to get the system to return to prior step if swipe navigated
    @Override
    public void onBackPressed() {
        if(pageHistory.empty())
            super.onBackPressed();
        else {
            saveToHistory = false;
            int makeCurrentStep = pageHistory.pop();
            mViewPager.setCurrentItem(makeCurrentStep);
            saveToHistory = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient != null) mGoogleApiClient.connect();

        Log.i("ajc2","onResume mDbLocationMasterRef: " + String.valueOf(mDbLocationMasterRef) +
                " mDbLocationMapRef: " + String.valueOf(mDbLocationMapRef) +
                " mDbLocationStatusICRef: " + String.valueOf(mDbLocationStatusICRef) +
                " mDbLocationEditListRef: " + String.valueOf(mDbLocationEditListRef));
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient != null) mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDbLocationStatusICRef != null && valueEventListenerStatus != null) mDbLocationStatusICRef.removeEventListener(valueEventListenerStatus);
    }

    // End other
    // ------------------------------------

    // ------------------------------------
    // Check location setting
    // ------------------------------------

    protected static GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    public static final int REQUEST_CHECK_SETTINGS = 1210;

    protected void checkLocationSettings() {
        Log.i("ajc2","checkLocationSettings before");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                //.addConnectionCallbacks(this)
                //.addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(ObtainGPSDataService.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(ObtainGPSDataService.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // //// start check settings section

        // Setup to ask for current location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // Check to see if settings are met
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.i("ajc2","checkLocationSettings SUCCESS");
                        findLocationAllowedSettings();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        Log.i("ajc2","checkLocationSettings RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    StepsActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        Log.i("ajc2","checkLocationSettings SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("ajc2","onActivityResult before");
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        findLocationAllowedSettings();//FINALLY YOUR OWN METHOD TO GET YOUR USER LOCATION HERE
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to

                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("ajc2","in onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("ajc2","in onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ajc2","in onConnectionFailed");
    }
    // End check location setting
    // ------------------------------------


    // ------------------------------------
    // Permissions
    // ------------------------------------

    //public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1492;
    //public static final int PERMISSIONS_REQUEST_MULTIPLE_PERMISSIONS = 1592;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1692;

    // in an activity you can call ActivityCompat in fragment needs to be requestPermissions in order to call callback
    /*
    public void checkPermissionExternal() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final Activity activity = this;

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Contacts");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                                        PERMISSIONS_REQUEST_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                    PERMISSIONS_REQUEST_MULTIPLE_PERMISSIONS);
            return;
        }

        processCallCode();

    }
    */

    // in an activity you can call ActivityCompat in fragment needs to be requestPermissions in order to call callback
    // ActivityCompat.requestPermissions(this, ...
    // requestPermissions( ...
    public void checkPermissionLocation() {
        final Activity activity = this;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageContinueCancel("Knowing your locations gives us the ability to mark the grave with a GPS location. We will need permission to access location on your device.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        findLocationAllowed();
    }


    /*
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    */

    private void showMessageContinueCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Continue", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("ajc2","PermissionResults before");
        switch (requestCode) {
            /*
            case PERMISSIONS_REQUEST_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    processCallCode();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            */
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.i("ajc2","Location PermissionResults Granted");
                    findLocationAllowed();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Location access denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        /*
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.i("ajc2","PermissionResults Granted");
                    processCallCode();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        */
    }

    /*
    private void processCallCode() {
        Log.i("ajc2","processCallCode");
        //finishCreate();
    }
    */

    // Permissions end
    // ------------------------------------

}
