package info.anth.lifecelebrated.AddLocationSteps;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Data.DbLocationMap;
import info.anth.lifecelebrated.Helpers.Helper;
import info.anth.lifecelebrated.Helpers.WorkaroundMapFragment;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 4/6/2016.
 *
 */
public class VerifyLocationFragment extends Fragment implements
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMapLongClickListener {

    /**
     * The fragment argument representing the page number for this
     * fragment.
     */
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_FIREBASE_EDIT_STATUS_REF = "firebase_edit_status_ref";
    private static final String ARG_FIREBASE_REF = "firebase_ref";

    public static final String LOG_TAG = VerifyLocationFragment.class.getSimpleName();

    private int priorProgress = 0;
    private static View rootView;
    Firebase mDbLocationMapRef;
    private static Firebase mDbLocationStatusRef;
    private ValueEventListener valueEventListenerMap;
    Context context;
    Button mLocationUpdateButton;
    private static Double selectedLongitude = 0.0;
    private static Double selectedLatitude = 0.0;

    private GoogleMap mMap;

    public VerifyLocationFragment() { }

    /**
     * Returns a new instance of this fragment for the given page
     * number.
     */
    public static VerifyLocationFragment newInstance(int pageNumber, String firebaseEditStatus, String firebase) {
        VerifyLocationFragment fragment = new VerifyLocationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        args.putString(ARG_FIREBASE_EDIT_STATUS_REF, firebaseEditStatus);
        args.putString(ARG_FIREBASE_REF, firebase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get argument data
        int position = getArguments().getInt(ARG_PAGE_NUMBER);
        String tempString = getArguments().getString(ARG_FIREBASE_EDIT_STATUS_REF);
        if (tempString != null) mDbLocationStatusRef = new Firebase(tempString);
        tempString = getArguments().getString(ARG_FIREBASE_REF);
        if (tempString != null) mDbLocationMapRef = new Firebase(tempString);

        rootView = inflater.inflate(R.layout.fragment_al_verify_location, container, false);
        context = getContext();

        // set page parameters
        int currentPage = position + 1;
        final Resources rw = getResources();
        final int stepArrayNo = rw.getIntArray(R.array.step_array_no)[position];

        // -------
        // Process the heading
        //
        TextView headingStepTextView = (TextView) rootView.findViewById(R.id.heading_step);
        TextView headingStepNoTextView = (TextView) rootView.findViewById(R.id.heading_number);
        TextView headingStepLineTextView = (TextView) rootView.findViewById(R.id.heading_line);
        TextView headingStepHRLine1TextView = (TextView) rootView.findViewById(R.id.heading_hr_line1);
        TextView headingStepHRLine2TextView = (TextView) rootView.findViewById(R.id.heading_hr_line2);
        TextView headingTitleTextView = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingTextView = (TextView) rootView.findViewById(R.id.heading_text);

        int headingColor;

        // set the colors for the number
        try {
            headingColor = rw.getIntArray(R.array.step_text_color)[stepArrayNo];
            headingStepTextView.setTextColor(headingColor);
            headingStepNoTextView.setTextColor(headingColor);
            headingStepLineTextView.setBackgroundColor(headingColor);
            headingStepHRLine1TextView.setBackgroundColor(headingColor);
            headingStepHRLine2TextView.setBackgroundColor(headingColor);
        } catch (Exception e) {
            // do nothing at this time
        }

        // set the text values
        headingStepNoTextView.setText(String.valueOf(currentPage));
        headingTitleTextView.setText(Helper.resourceString(getContext(), R.array.step_title, stepArrayNo));
        headingTextView.setText(Html.fromHtml(Helper.resourceString(getContext(), R.array.step_text, stepArrayNo)));
        //
        // -------

        // -------
        // Process the rest of the xml for color
        //
        TextView stepHRLine1TextView = (TextView) rootView.findViewById(R.id.hr_line1);
        TextView stepHRLine2TextView = (TextView) rootView.findViewById(R.id.hr_line2);
        TextView stepHRLine3TextView = (TextView) rootView.findViewById(R.id.hr_line3);

        try {
            headingColor = rw.getIntArray(R.array.step_text_color)[stepArrayNo];
            stepHRLine1TextView.setBackgroundColor(headingColor);
            stepHRLine2TextView.setBackgroundColor(headingColor);
            stepHRLine3TextView.setBackgroundColor(headingColor);
        } catch (Exception e) {
            // do nothing at this time
        }
        //
        // -------

        //Progress bar map - setup listener for background service cancellation
        final ProgressBar mapProgressBar = (ProgressBar) rootView.findViewById(R.id.map_loading);
        mapProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> processCancel = new HashMap<>();
                processCancel.put(DbLocationMap.columns.COLUMN_CALCCANCELLED, true);
                if(mDbLocationMapRef != null) mDbLocationMapRef.updateChildren(processCancel);
                mapProgressBar.setVisibility(View.GONE);
            }
        });

        // -------
        // Dynamic Map and Scrolling
        //

        // setup button to manually set latitude and longitude
        mLocationUpdateButton = (Button) rootView.findViewById(R.id.location_button);

        mLocationUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationUpdateButton.setEnabled(false);

                DbLocationMap newLocationMap = new DbLocationMap("None", "None", "Manual Map", "User", selectedLongitude, selectedLatitude, 0.0, 0.0, 0L, 100, false);
                mDbLocationMapRef.updateChildren(DbLocationMap.columns.getFullMap(newLocationMap));
            }
        });


        // dynamically add the mapFragment so you can get nested scrolling working
        // Note: need to replace the transaction add keeps adding new on device turns, other pauses
        WorkaroundMapFragment mapFragment = new WorkaroundMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.map, mapFragment).commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                loadMap(mMap);

            }
        });

        final ScrollView parentScroll = (ScrollView) rootView.findViewById(R.id.scrollView);

        // set parent scrolling to ignore scrolling
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                parentScroll.requestDisallowInterceptTouchEvent(true);
            }
        });
        //
        // -------

        return rootView;
    }

    public void loadMap(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(selectedLatitude, selectedLongitude)));

        // setup long click listener for manual location selection
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Log.i("ajcMap", "in NEW onMapLongClick mMap: " + mMap.toString());
                mMap.clear();
                //Log.i("ajc", "point " + point);
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude, point.longitude), 16));

                mMap.addMarker(new MarkerOptions()
                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                        .position(new LatLng(latLng.latitude, latLng.longitude)));

                //Log.i("ajc2", point.toString());
                mLocationUpdateButton.setEnabled(true);
                selectedLongitude = latLng.longitude;
                selectedLatitude = latLng.latitude;
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng point) {
        //Log.i("ajcMap", "in onMapLongClick mMap: " + mMap.toString());
        mMap.clear();
        //Log.i("ajc", "point " + point);
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude, point.longitude), 16));

        mMap.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(point.latitude, point.longitude)));

        //Log.i("ajc2", point.toString());
        mLocationUpdateButton.setEnabled(true);
        selectedLongitude = point.longitude;
        selectedLatitude = point.latitude;
    }


    public void refreshScreen(DbLocationMap thisLocationMap) {
        TextView longitudeTextView = (TextView) rootView.findViewById(R.id.longitude);
        longitudeTextView.setText(String.valueOf(thisLocationMap.getLongitude()));
        TextView latitudeTextView = (TextView) rootView.findViewById(R.id.latitude);
        latitudeTextView.setText(String.valueOf(thisLocationMap.getLatitude()));
        TextView accuracyTextView = (TextView) rootView.findViewById(R.id.accuracy);
        accuracyTextView.setText(String.valueOf((int) Math.round(thisLocationMap.getAccuracy())));

        TextView otherTextView = (TextView) rootView.findViewById(R.id.other_info);
        String message = "Method: " + thisLocationMap.getMethod();
        message += " Provider: " + thisLocationMap.getProvider();
        message += "\n";
        message += "Device: " + thisLocationMap.getDeviceModel();
        message += "\n";
        message += "OS: " + thisLocationMap.getDeviceOS();
        message += "\n";
        message += "Time: " + String.valueOf(thisLocationMap.getSecondsToGPS()) + " seconds";

        otherTextView.setText(message);

        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", priorProgress, thisLocationMap.getProgressGPS());
        // see this max value coming back here, we animale towards that value
        animation.setDuration(1000); //in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        priorProgress = thisLocationMap.getProgressGPS();

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(thisLocationMap.getLatitude(), thisLocationMap.getLongitude()), 18));
        mMap.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(thisLocationMap.getLatitude(), thisLocationMap.getLongitude())));

        //Progress bar map
        ProgressBar mapProgressBar = (ProgressBar) rootView.findViewById(R.id.map_loading);
        if(thisLocationMap.getProgressGPS() == 100) mapProgressBar.setVisibility(View.GONE);
    }

    // Firebase listener for the map record data
    private void setLocationMapListener() {
        //Log.i(LOG_TAG, "In the Map Listener setup");
        valueEventListenerMap = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DbLocationMap thisLocationMap = snapshot.getValue(DbLocationMap.class);

                //Log.i(LOG_TAG, "In the Map Listener");
                if (thisLocationMap != null) {
                    // TODO update data for master
                    //refreshScreen(thisLocationMaster);
                    //Fragment mapFragment = mSectionsPagerAdapter.getItem(6);
                    //mapFragment.getClass().
                    refreshScreen(thisLocationMap);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        };

        mDbLocationMapRef.addValueEventListener(valueEventListenerMap);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDbLocationMapRef != null && valueEventListenerMap != null) {
            mDbLocationMapRef.removeEventListener(valueEventListenerMap);
        }
    }

    @Override
    public void onResume() {
        super.onStart();

        if (mDbLocationMapRef != null)
            setLocationMapListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.i(LOG_TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.i(LOG_TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onStop();
        //Log.i(LOG_TAG, "onDestroy");
    }

}
