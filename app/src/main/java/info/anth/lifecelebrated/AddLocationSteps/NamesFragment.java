package info.anth.lifecelebrated.AddLocationSteps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Data.DbLocationEditList;
import info.anth.lifecelebrated.Data.DbLocationMaster;
import info.anth.lifecelebrated.Data.DbLocationNames;
import info.anth.lifecelebrated.Helpers.Helper;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 4/8/2016.
 *
 */
public class NamesFragment extends Fragment {

    /**
     * The fragment argument representing the page number for this
     * fragment.
     */
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_FIREBASE_EDIT_STATUS_REF = "firebase_edit_status_ref";
    private static final String ARG_FIREBASE_EDIT_MASTER_REF = "firebase_edit_master_ref";
    private static final String ARG_FIREBASE_EDIT_LIST_REF = "firebase_edit_list_ref";

    public static final String LOG_TAG = NamesFragment.class.getSimpleName();

    //private static int currentPage;
    private static Firebase mDbLocationEditListRef;
    private static Firebase mDbLocationMasterRef;
    private static Firebase mDbLocationStatusRef;
    private static Firebase mDbLocationMasterNamesRef;
    private ValueEventListener valueEventListener;
    private String stepTitle;

    private static View rootView;
    private Boolean databaseRefreshedScreen = false;

    FirebaseRecyclerAdapter mChildAdapter;

    public NamesFragment() { }

    /**
     * Returns a new instance of this fragment for the given page
     * number.
     */
    public static NamesFragment newInstance(int pageNumber, String firebaseEditStatus, String firebaseEditMaster, String firebaseEditList) {
        NamesFragment fragment = new NamesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        args.putString(ARG_FIREBASE_EDIT_STATUS_REF, firebaseEditStatus);
        args.putString(ARG_FIREBASE_EDIT_MASTER_REF, firebaseEditMaster);
        args.putString(ARG_FIREBASE_EDIT_LIST_REF, firebaseEditList);
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
        tempString = getArguments().getString(ARG_FIREBASE_EDIT_MASTER_REF);
        if (tempString != null) mDbLocationMasterRef = new Firebase(tempString);
        tempString = getArguments().getString(ARG_FIREBASE_EDIT_LIST_REF);
        if (tempString != null) mDbLocationEditListRef = new Firebase(tempString);

        // set Names sub reference
        mDbLocationMasterNamesRef = mDbLocationMasterRef.child(getResources().getString(R.string.FIREBASE_CHILD_MASTER_NAMES));

        //View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        rootView = inflater.inflate(R.layout.fragment_al_names, container, false);

        // set page parameters
        int currentPage = position + 1;
        final Resources rw = getResources();
        final int stepArrayNo = rw.getIntArray(R.array.step_array_no)[position];

        // -------
        // Process the heading
        //

        // define the views
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
        stepTitle = Helper.resourceString(getContext(), R.array.step_title, stepArrayNo);

        headingStepNoTextView.setText(String.valueOf(currentPage));
        headingTitleTextView.setText(stepTitle);
        headingTextView.setText(Html.fromHtml(Helper.resourceString(getContext(), R.array.step_text, stepArrayNo)));

        //
        // -------

        /*
        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.namesList);
        Log.i("ajc2", "Recycler : " + String.valueOf(recycler) );
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mChildAdapter = new FirebaseRecyclerAdapter<DbLocationNames, LocationNamesViewHolder>
                (DbLocationNames.class, R.layout.x_step_names_list, LocationNamesViewHolder.class, mDbLocationMasterNamesRef) {
            @Override
            public void populateViewHolder(LocationNamesViewHolder locationNamesViewHolder
                    , DbLocationNames dbLocationNames, int position) {

                // define the text fields
                locationNamesViewHolder.editFamilyName.setText(dbLocationNames.getFamilyName());
                locationNamesViewHolder.editFirstName.setText(dbLocationNames.getFirstName());

                Log.i("ajc2", "in populateViewHolder : " + dbLocationNames.getFamilyName() );
            }

            @Override
            public void onBindViewHolder(final LocationNamesViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);

                holder.mItem = (DbLocationNames) mChildAdapter.getItem(position);
                holder.mNameKey = mChildAdapter.getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("ajc2", "Click position: " + String.valueOf(holder.getAdapterPosition()) + " key: " + holder.mNameKey);
                    }
                });
            }
        };
        recycler.setAdapter(mChildAdapter);
        */

        Fragment namesFragmentRecycler = NamesFragmentRecycler.newInstance(mDbLocationMasterRef.getRef().toString());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_recycler, namesFragmentRecycler).commit();
        //transaction.add(R.id.fragment_recycler, namesFragmentRecycler).commit();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DbLocationNames newDbLocationNames = new DbLocationNames("Smitty", "My");
                //DbLocationNames newDbLocationNames = DbLocationNames.columns.createBlank();
                //mDbLocationMasterNamesRef.push().setValue(newDbLocationNames);
                NamesDialog namesDialog = NamesDialog.newInstance(mDbLocationMasterNamesRef.getRef().toString(), null);
                namesDialog.show(getChildFragmentManager(),"");
            }
        });


        return rootView;
    }
/*
    public static class LocationNamesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        EditText editFamilyName;
        EditText editFirstName;
        public DbLocationNames mItem;
        public String mNameKey;

        public LocationNamesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            editFamilyName = (EditText)itemView.findViewById(R.id.entered_text_family);
            editFirstName = (EditText)itemView.findViewById(R.id.entered_text_first);
        }
    }
*/
    public void refreshScreen(final DbLocationMaster thisLocationMaster) {
        /*EditText editText = (EditText) rootView.findViewById(R.id.entered_text);
        TextView textView = (TextView) rootView.findViewById(R.id.show_text);

        String enteredText="";

        enteredText = thisLocationMaster.getName();

        editText.setText(enteredText);
        textView.setText(enteredText);
        */
        databaseRefreshedScreen = true;

    }

    // ------------------------------------
    // Database functions
    // ------------------------------------

    // Firebase listener for the master record data
    private void setDBListener() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DbLocationMaster thisLocationMaster = snapshot.getValue(DbLocationMaster.class);

                if (thisLocationMaster != null) {
                    Log.i("ajc","addValueEventListener name: " + thisLocationMaster.getName() + " descr: " + thisLocationMaster.getDescription());
                    refreshScreen(thisLocationMaster);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        };

        mDbLocationMasterRef.addValueEventListener(valueEventListener);
    }
    //
    // ------------------------------------

    @Override
    public void onPause() {
        super.onPause();

        Log.i("ajc","onPause: " + stepTitle);
        if (mDbLocationMasterRef != null && valueEventListener != null) {
            //Log.i(LOG_TAG, "before remove");
            mDbLocationMasterRef.removeEventListener(valueEventListener);
        }
    }

    @Override
    public void onResume() {
        super.onStart();

        Log.i("ajc","onResume: " + stepTitle);
        if (mDbLocationMasterRef != null)
            setDBListener();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // mChildAdapter.cleanup();
    }
}

