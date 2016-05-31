package info.anth.lifecelebrated.Services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Data.DbLocationMap;

public class ObtainGPSDataService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final String REQUEST_REF_LOCATION_MAP = "location_map";

    public static final String LOG_TAG = ObtainGPSDataService.class.getSimpleName();
    private static final Boolean LOG_ON = true;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static double lastLongitude;
    private static double lastLatitude;
    private static int countInRow = 3;
    private static int currentCount;

    private static int maxChecks = 5;
    private static int currentCheck;

    private static double bestAccuracy;
    private static double bestLongitude;
    private static double bestLatitude;
    private static double bestAltitude;
    private static String bestProvider;

    private static Date startDate;

    private Firebase mFirebaseRef;
    private ValueEventListener valueEventListener;

    public ObtainGPSDataService() {
        super("ObtainGPSDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String refFirebase = intent.getStringExtra(REQUEST_REF_LOCATION_MAP);

            // Create an instance of GoogleAPIClient.
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }

            mGoogleApiClient.connect();
            mFirebaseRef = new Firebase(refFirebase);
            setDBListener();
        }

    }

    /**
     * GoogleApiClient.ConnectionCallbacks abstact method
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (LOG_ON) Log.i(LOG_TAG, "Connection established");
        findLocation();
    }

    /**
     * GoogleApiClient.ConnectionCallbacks abstact method
     * Runs when a GoogleApiClient object is temporarily in a disconnected state.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        if (LOG_ON) Log.i(LOG_TAG, "Connection suspended");
    }

    /**
     * GoogleApiClient.OnConnectionFailedListener abstact method
     * Runs when there is an error connection the client to the service.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    // On location change
    @Override
    public void onLocationChanged(Location location) {
        if (LOG_ON) Log.i(LOG_TAG, "onLocationChanged count: " + currentCount + " Accuracy: " + String.valueOf(location.getAccuracy()));

        currentCheck++;
        int progress;

        if(currentCheck >= maxChecks) {
            progress = 90;
        } else {
            progress = (int) Math.round(((double) currentCheck / (double) maxChecks) * 100.0);
        }

        if (LOG_ON) Log.i(LOG_TAG, "progress: " + String.valueOf(progress) + " currentCheck: " + String.valueOf(currentCheck) + " maxChecks: " + String.valueOf(maxChecks));

        Map<String, Object> progressGPS = new HashMap<String, Object>();
        progressGPS.put(DbLocationMap.columns.COLUMN_PROGRESSGPS, progress);
        progressGPS.put(DbLocationMap.columns.COLUMN_LONGITUDE, location.getLongitude());
        progressGPS.put(DbLocationMap.columns.COLUMN_LATITUDE, location.getLatitude());
        mFirebaseRef.updateChildren(progressGPS);

        if(bestAccuracy >= location.getAccuracy()) {
            bestAccuracy = (double) location.getAccuracy();
            bestLatitude = location.getLatitude();
            bestLongitude = location.getLongitude();
            bestAltitude = location.getAltitude();
            bestProvider = location.getProvider();
        }

        if (lastLatitude == location.getLatitude() && lastLongitude == location.getLongitude()) {
            currentCount++;
        } else {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            currentCount = 0;
        }

        // Finished
        // // TODO: 4/7/2016 add timer - if it taked too long kill
        if ((countInRow == currentCount) || (currentCheck >= maxChecks && currentCount == 0)) {
            updateDB(location);
        }
       /* if (countInRow == currentCount) {
            stopLocationUpdates();

            // Setup our Firebase mFirebaseRef
            String deviceModel = Build.MANUFACTURER + " : " + Build.MODEL;
            String deviceOS = "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT);
            String method = "Consistency";
            Long seconds = (new Date().getTime() - startDate.getTime())/1000;

            DbLocationMap newLocationMap = new DbLocationMap(deviceModel, deviceOS, method, location.getProvider(), location.getLongitude(), location.getLatitude(), (double) location.getAccuracy(), location.getAltitude(), seconds, 100);

            mFirebaseRef.updateChildren(DbLocationMap.columns.getFullMap(newLocationMap));
            mFirebaseRef.onDisconnect();
        }
        else if (currentCheck >= maxChecks && currentCount == 0) {
            stopLocationUpdates();

            String deviceModel = Build.MANUFACTURER + " : " + Build.MODEL;
            String deviceOS = "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT);
            String method = "Accuracy";
            Long seconds = (new Date().getTime() - startDate.getTime())/1000;

            DbLocationMap newLocationMap = new DbLocationMap(deviceModel, deviceOS, method, location.getProvider(), bestLongitude, bestLatitude, bestAccuracy,
                    bestAltitude, seconds, 100);

            mFirebaseRef.updateChildren(DbLocationMap.columns.getFullMap(newLocationMap));
            mFirebaseRef.onDisconnect();
        }*/
    }

    protected void updateDB(Location location) {

        mFirebaseRef.child(DbLocationMap.columns.COLUMN_CALCCANCELLED).removeEventListener(valueEventListener);
        stopLocationUpdates();

        // Setup our Firebase mFirebaseRef
        String deviceModel = Build.MANUFACTURER + " : " + Build.MODEL;
        String deviceOS = "Android OS: " + Build.VERSION.RELEASE + " : sdk=" + String.valueOf(Build.VERSION.SDK_INT);
        Long seconds = (new Date().getTime() - startDate.getTime())/1000;
        String method = "Accuracy";

        if (countInRow == currentCount) {
            method = "Consistency";
            bestLongitude = location.getLongitude();
            bestLatitude = location.getLatitude();
            bestAccuracy = (double) location.getAccuracy();
            bestAltitude = location.getAltitude();
            bestProvider = location.getProvider();
        }

        DbLocationMap newLocationMap = new DbLocationMap(deviceModel, deviceOS, method, bestProvider, bestLongitude, bestLatitude, bestAccuracy, bestAltitude, seconds, 100, false);

        mFirebaseRef.updateChildren(DbLocationMap.columns.getFullMap(newLocationMap));
        mFirebaseRef.onDisconnect();
    }

    // call location requests
    protected void findLocation() {
        // initialize the variables
        lastLongitude = 0;
        lastLatitude = 0;
        currentCount = 0;
        currentCheck = 0;
        bestAccuracy = 1000;
        bestLongitude = 0;
        bestLatitude = 0;
        bestAltitude = 0;
        startDate = new Date();

        // create a location request and start updating location data
        createLocationRequest();
        startLocationUpdates();
    }

    // setup location request
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Location updates started
    protected void startLocationUpdates() {
        if (LOG_ON) Log.i(LOG_TAG, "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(LOG_TAG, "Access denied to call");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    // location updates stopped
    public void stopLocationUpdates() {
        if (LOG_ON) Log.i(LOG_TAG, "stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    // ------------------------------------
    // Database functions
    // ------------------------------------

    // Firebase listener for the master record data
    private void setDBListener() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Boolean cancelCalled = snapshot.getValue(Boolean.class);

                if (cancelCalled) {
                    // cancel the calls
                    //Log.i("ajc", "cancelling");
                    mFirebaseRef.child(DbLocationMap.columns.COLUMN_CALCCANCELLED).removeEventListener(valueEventListener);
                    stopLocationUpdates();
                    mFirebaseRef.onDisconnect();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        };

        mFirebaseRef.child(DbLocationMap.columns.COLUMN_CALCCANCELLED).addValueEventListener(valueEventListener);
    }
    //
    // ------------------------------------

}
