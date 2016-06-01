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
import info.anth.lifecelebrated.Data.DbLocationEditStatus;
import info.anth.lifecelebrated.Data.DbLocationMap;
import info.anth.lifecelebrated.Data.DbLocationMaster;
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

    //private Firebase mFirebaseRef;
    private static Firebase mDbLocationMasterRef;
    private static Firebase mDbLocationMapRef;
    private static Firebase mDbLocationEditListRef;
    private static Firebase mDbLocationStatusRef;
    //private ValueEventListener valueEventListenerMaster;
    //private ValueEventListener valueEventListenerMap;
    private ValueEventListener valueEventListenerStatus;

    private String currentEditListItem;

    //public static int priorProgress;
    public static ProgressBar progressBar;

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
                mDbLocationStatusRef = pushRefLocation.child("status");

                DbLocationMaster newDbLocationMaster = DbLocationMaster.columns.createBlank(this);
                mDbLocationMasterRef.setValue(newDbLocationMaster);

                DbLocationMap newDbLocationMap = DbLocationMap.columns.createBlank();
                mDbLocationMapRef.setValue(newDbLocationMap);

                DbLocationEditStatus newDbLocationStatus = DbLocationEditStatus.columns.createDefaults(this);
                mDbLocationStatusRef.setValue(newDbLocationStatus);

                DbLocationEditList newListItem = DbLocationEditList.columns.createBlank(pushRefLocation.getKey(), this);
                mDbLocationEditListRef = firebaseEditListRef.push();
                mDbLocationEditListRef.setValue(newListItem);
            } else {
                mDbLocationMasterRef = firebaseEditRef.child(currentLocation).child("master");
                mDbLocationMapRef = firebaseEditRef.child(currentLocation).child("map");
                mDbLocationStatusRef = firebaseEditRef.child(currentLocation).child("status");
                mDbLocationEditListRef = firebaseEditListRef.child(currentEditListItem);
            }

            /*
            mDbLocationMasterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DbLocationMaster tempValues = dataSnapshot.getValue(DbLocationMaster.class);
                    if (tempValues!=null){
                        Log.i("ajc", "not null");
                        Log.i("ajc", "deviceId: " + tempValues.getDeviceID());
                        Log.i("ajc", "test1: " + tempValues.test[0]);
                        Log.i("ajc", "test2: " + tempValues.test[1]);
                        Log.i("ajc", "test3: " + tempValues.test[2]);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            */
            //Log.i("ajc", String.valueOf(mDbLocationMapRef));
            //Log.i("ajc", String.valueOf(mDbLocationMasterRef));

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

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                composeEmail("7 StepsActivity Towards Financial Freedom", "");
            }
        });
*/
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
                progressBar.setProgress(position+1);
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
       /*
    @Override
    public void onResume() {
        super.onResume();
        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        if(mTabLayout != null) {
            int stepsArray[] = getResources().getIntArray(R.array.step_array_no);
            for ( int i = 0; i < mTabLayout.getTabCount(); i++)
            {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                //if (tab != null) tab.setText(String.valueOf(i+1));
                //if (tab != null) tab.setText(getResources().getStringArray(R.array.step_title_short)[stepsArray[i]]);
                if (tab != null) {
                    String drawableName = getResources().getStringArray(R.array.step_title_icon)[stepsArray[i]];
                    int drawableInt = getResources().getIdentifier(drawableName,"drawable",getPackageName());
                    if (drawableInt != 0) tab.setIcon(drawableInt);
                }

                        //getResources().getDrawable(
                        //
                //);
                //int j = i+1;
                //String message = "Step " + j + ": " + getResources().getStringArray(R.array.step_title)[stepsArray[i]];
                //Log.i("ajc", "step: " + stepsArray[i] + " i: " + i + " message: " + message);
                //temp.add(0,i,i,message);
            }
        }

        //TabLayout.Tab tab2 = tabLayout.getTabAt(2);
        //tab2.setIcon(R.drawable.ic_camera_alt_24dp);

        //TabWidget vTabs = tabLayout.getTabWidget();
        //RelativeLayout rLayout = (RelativeLayout) vTabs.getChildAt(tabIndex);
        //((TextView) rLayout.getChildAt(textIndex)).setText("NewTabText");
    }
*/
    // ------------------------------------
    // Database functions
    // ------------------------------------

    public void setLocationStatusListener() {
        /*
        // Set the default tab icons
        if(mTabLayout != null) {
            int stepsArray[] = getResources().getIntArray(R.array.step_array_no);
            for ( int i = 0; i < mTabLayout.getTabCount(); i++)
            {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null) {
                    String drawableName = getResources().getStringArray(R.array.step_tab_icon)[stepsArray[i]];
                    int drawableInt = getResources().getIdentifier(drawableName,"drawable",getPackageName());
                    if (drawableInt != 0) tab.setIcon(drawableInt);
                }
            }
        }
        */
        valueEventListenerStatus = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DbLocationEditStatus thisLocationStatus = snapshot.getValue(DbLocationEditStatus.class);

                if (thisLocationStatus != null) {
                    if(mTabLayout != null) {
                        int stepsArray[] = getResources().getIntArray(R.array.step_array_no);
                        for ( int i = 0; i < mTabLayout.getTabCount(); i++)
                        {
                            String columnName = getResources().getStringArray(R.array.step_db_column)[stepsArray[i]];
                            TabLayout.Tab tab = mTabLayout.getTabAt(i);
                            if (tab != null) {
                                // set default icon
                                //String drawableName = getResources().getStringArray(R.array.step_tab_icon)[stepsArray[i]];
                                // Change to database value
                                String drawableName = DbLocationEditStatus.columns.getColumnValue(columnName, thisLocationStatus);
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

        mDbLocationStatusRef.addValueEventListener(valueEventListenerStatus);
    }

    private void tempAddBlank() {
        DbLocationEditStatus newDbLocationStatus = DbLocationEditStatus.columns.createDefaults(this);
        mDbLocationStatusRef.setValue(newDbLocationStatus);
    }

    /*

    // Firebase listener for the master record data
    private void setLocationMasterListener() {
        valueEventListenerMaster = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DbLocationMaster thisLocationMaster = snapshot.getValue(DbLocationMaster.class);

                if (thisLocationMaster != null) {
                    // TODO update data for master
                    //refreshScreen(thisLocationMaster);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        };

        mDbLocationMasterRef.addValueEventListener(valueEventListenerMaster);
    }

    // Firebase listener for the map record data
    private void setLocationMapListener() {
        valueEventListenerMap = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DbLocationMap thisLocationMap = snapshot.getValue(DbLocationMap.class);

                if (thisLocationMap != null) {
                    // TODO update data for master
                    //refreshScreen(thisLocationMaster);
                    //Fragment mapFragment = mSectionsPagerAdapter.getItem(6);
                    //mapFragment.getClass().
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        };

        mDbLocationMapRef.addValueEventListener(valueEventListenerMap);
    }
*/
/*
    // http://stackoverflow.com/questions/15982215/firebase-count-online-users
    public void getDbCount() {

        Firebase listRef = new Firebase("https://shining-inferno-6812.firebaseio.com/presence/");
        final Firebase userRef = listRef.push();

        // Add ourselves to presence list when online.
        Firebase presenceRef = new Firebase("https://shining-inferno-6812.firebaseio.com/.info/connected");

        ValueEventListener myPresence = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Remove ourselves when we disconnect.
                userRef.onDisconnect().removeValue();
                userRef.setValue(true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("DBCount", "The read failed: " + firebaseError.getMessage());
            }
        };

        presenceRef.addValueEventListener(myPresence);

        // Number of online users is the number of objects in the presence list.
        ValueEventListener myList = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Remove ourselves when we disconnect.
                Log.i("DBCount", "# of online users = " + String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("DBCount", "The read failed: " + firebaseError.getMessage());
            }
        };

        listRef.addValueEventListener(myList);
    }
*/
    //
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

    //
    // ------------------------------------

    /*
    static public void setProgressBar(int progress){

        Log.i("ajc","here");
        //if (priorProgress == 1) priorProgress = 0;
        //ProgressBar progressBar = (ProgressBar) getContext().findViewById(R.id.overall_progress);
        progressBar.setProgress(progress);

        //ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", priorProgress*10/R.integer.step_count,progress*10/R.integer.step_count );

        // see this max value coming back here, we animate towards that value
        //animation.setDuration(1000); //in milliseconds
        //animation.setInterpolator(new DecelerateInterpolator());
        //animation.start();
        //priorProgress = progress;
    }

    public void composeEmail(String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:cardenuto@yahoo.com")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    */

    /*
        Override the onBackPress to get the system to return to prior step if swipe navigated
     */
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

        //mDbLocationMapRef.onDisconnect();
        //mDbLocationMasterRef.onDisconnect();

        // Remove Firebase event listeners
        //mDbLocationMasterRef.removeEventListener(valueEventListenerMaster);
        //mDbLocationMapRef.removeEventListener(valueEventListenerMap);
        mDbLocationStatusRef.removeEventListener(valueEventListenerStatus);

        //mViewPager.clearOnPageChangeListeners();
        //mViewPager.destroyDrawingCache();
        Log.i("ajc", "On Destroy");
        //Runtime.getRuntime().gc();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_steps, menu);
        //menu.getItem(R.id.menu_steps).getSubMenu()
        SubMenu temp = menu.addSubMenu(0,700,10,"Jump to a Step");
        int stepsArray[] = getResources().getIntArray(R.array.step_array_no);
        for ( int i = 0; i <= stepsArray.length - 1; i++)
        {
            int j = i+1;
            String message = "Step " + j + ": " + getResources().getStringArray(R.array.step_title)[stepsArray[i]];
            //Log.i("ajc", "step: " + stepsArray[i] + " i: " + i + " message: " + message);
            temp.add(0,i,i,message);
        }
        //temp.add(0,0,10,"test1");
        //temp.add(0,0,10,"test2");
        //menu.add(0,0,10,"test1");
        //menu.getItem(R.id.menu_steps).getSubMenu().add(0,0,20,"test2");
        return true;
    }
/*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(isStarted) {
            menu.removeItem(1);
            menu.add(0, 0, 0, "Stop");
        } else {
            menu.removeItem(0);
            menu.add(0, 1, 0, "Start");
        }

        return super.onPrepareOptionsMenu(menu);
    }
    */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
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

            // Set the default tab icon
            // problem is timing -- this only populates when page is loaded - recycled
            /*
            if(mTabLayout != null) {
                TabLayout.Tab tab = mTabLayout.getTabAt(position);
                if (tab != null) {
                    String drawableName = getResources().getStringArray(R.array.step_tab_icon)[stepArrayNo];
                    int drawableInt = getResources().getIdentifier(drawableName,"drawable",getPackageName());
                    if (drawableInt != 0) tab.setIcon(drawableInt);
                }
            }
            */

            switch (stepFragment) {
                case "BlankFragment":
                    return BlankFragment.newInstance(position);
                case "EditBoxFragment":
                    return EditBoxFragment.newInstance(position, mDbLocationStatusRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "EditBoxFragment2":
                    return EditBoxFragment2.newInstance(position, mDbLocationStatusRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "ImageFragment":
                    return ImageFragment.newInstance(position, mDbLocationStatusRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "NamesFragment":
                    return NamesFragment.newInstance(position, mDbLocationStatusRef.getRef().toString(),
                            mDbLocationMasterRef.getRef().toString(), mDbLocationEditListRef.getRef().toString());
                case "VerifyLocationFragment":
                    return VerifyLocationFragment.newInstance(position, mDbLocationStatusRef.getRef().toString(),
                            mDbLocationMapRef.getRef().toString());
            }

            return BlankFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 8 total pages.
            return getResources().getInteger(R.integer.step_count);
        }
/*
        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position + 1);
        }*/
    }

    // ------------------------------------
    // Action Bar code
    // ------------------------------------

    // Override return to manifest declared parent.
    // Activity used for ADD and EDIT
    // Add - returns to Main page (manifest default)
    // Edit - needs to return to edit page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("ajc", "selected: " + item.getItemId());
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
    //
    // ------------------------------------
}
