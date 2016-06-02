package info.anth.lifecelebrated;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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

public class StepsActivity extends AppCompatActivity {

    public static final String REQUEST_CURRENT_STEP = "current_step";
    public static final String REQUEST_CURRENT_LOCATION = "current_location";
    public static final String REQUEST_CURRENT_EDIT_LIST_ITEM = "edit_list_item";

    public static final String LOG_TAG = StepsActivity.class.getSimpleName();


    int currentStep;
    int priorStep;
    Stack<Integer> pageHistory;
    boolean saveToHistory;

    private static Firebase mDbLocationMasterRef;
    private static Firebase mDbLocationMapRef;
    private static Firebase mDbLocationEditListRef;
    private static Firebase mDbLocationStatusICRef;
    private ValueEventListener valueEventListenerStatus;

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
            if (currentLocation == null) findLocation(this);
            //
            // ------------------------------------
        }

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
                        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
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

    // Call Location Service
    protected static void findLocation(Context context) {
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
    public void onDestroy() {
        super.onDestroy();
        mDbLocationStatusICRef.removeEventListener(valueEventListenerStatus);
    }

    // End other
    // ------------------------------------
}
