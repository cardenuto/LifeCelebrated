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

        Fragment namesFragmentRecycler = NamesFragmentRecycler.newInstance(mDbLocationMasterRef.getRef().toString());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_recycler, namesFragmentRecycler).commit();
        //transaction.add(R.id.fragment_recycler, namesFragmentRecycler).commit();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NamesDialog namesDialog = NamesDialog.newInstance(mDbLocationMasterNamesRef.getRef().toString(), null);
                namesDialog.show(getChildFragmentManager(),"");
            }
        });


        return rootView;
    }
}

