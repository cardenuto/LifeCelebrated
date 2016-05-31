package info.anth.lifecelebrated.AddLocationSteps;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Data.DbLocationEditList;
import info.anth.lifecelebrated.Data.DbLocationMaster;
import info.anth.lifecelebrated.Helpers.Helper;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 4/8/2016.
 *
 */
public class EditBoxFragment extends Fragment {

    /**
     * The fragment argument representing the page number for this
     * fragment.
     */
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_FIREBASE_EDIT_STATUS_REF = "firebase_edit_status_ref";
    private static final String ARG_FIREBASE_EDIT_MASTER_REF = "firebase_edit_master_ref";
    private static final String ARG_FIREBASE_EDIT_LIST_REF = "firebase_edit_list_ref";

    public static final String LOG_TAG = EditBoxFragment.class.getSimpleName();

    //private static int currentPage;
    private static Firebase mDbLocationEditListRef;
    private static Firebase mDbLocationMasterRef;
    private static Firebase mDbLocationStatusRef;
    private ValueEventListener valueEventListener;
    private String stepTitle;

    private static View rootView;
    private Boolean databaseRefreshedScreen = false;

    public EditBoxFragment() { }

    /**
     * Returns a new instance of this fragment for the given page
     * number.
     */
    public static EditBoxFragment newInstance(int pageNumber, String firebaseEditStatus, String firebaseEditMaster, String firebaseEditList) {
        EditBoxFragment fragment = new EditBoxFragment();
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

        //View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        rootView = inflater.inflate(R.layout.fragment_al_editbox, container, false);

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


        final EditText editText = (EditText) rootView.findViewById(R.id.entered_text);

        // set hint for editbox
        if (stepTitle.equals(rw.getStringArray(R.array.step_title)[2])) editText.setHint(R.string.hint_name);
        if (stepTitle.equals(rw.getStringArray(R.array.step_title)[5])) editText.setHint(R.string.hint_description);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.i("ajc", "Lost focus: " + stepTitle);
                    //TextView textView = (TextView) rootView.findViewById(R.id.show_text);
                    //textView.setText(Html.fromHtml(editText.getText().toString()));
                    //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    // Update the database if defined and screen is refreshed with database data
                    if (mDbLocationMasterRef != null && databaseRefreshedScreen) {

                        // Determine field being updated
                        //String stepTitle = Helper.resourceString(getContext(), R.array.step_title, stepArrayNo);
                        String column = null;
                        String text = editText.getText().toString();

                        // Assign column - name field
                        if (stepTitle.equals(rw.getStringArray(R.array.step_title)[2])) {
                            // Update EditList for name
                            if (mDbLocationEditListRef != null) {
                                Map<String, Object> textFields = new HashMap<>();
                                textFields.put(DbLocationEditList.columns.COLUMN_NAME, text);
                                mDbLocationEditListRef.updateChildren(textFields);
                            }
                            column = DbLocationMaster.columns.COLUMN_NAME;
                        }

                        // Assign column - description field
                        if (stepTitle.equals(rw.getStringArray(R.array.step_title)[5])) column = DbLocationMaster.columns.COLUMN_DESCRIPTION;

                        // Update Master data - description and name
                        if (column != null) {
                            Map<String, Object> textFields = new HashMap<>();
                            textFields.put(column, text);
                            mDbLocationMasterRef.updateChildren(textFields);
                        }
                    }

                } else {
                    Log.i("ajc", "Has focus: " + stepTitle);
                    //super.onFocusChange(v, hasFocus);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        return rootView;
    }

    public void refreshScreen(final DbLocationMaster thisLocationMaster) {
        EditText editText = (EditText) rootView.findViewById(R.id.entered_text);
        TextView textView = (TextView) rootView.findViewById(R.id.show_text);

        String enteredText="";

        //Log.i("ajc", "column: " + stepTitle);
        if (stepTitle.equals(getResources().getStringArray(R.array.step_title)[2])) {
            // Name
            //String textName = thisLocationMaster.getName();
            //editText.setText(textName);
            //textView.setText(Html.fromHtml(textName));
            enteredText = thisLocationMaster.getName();
           // Log.i("ajc", "column ... inside name: " + enteredText);
        }
        if (stepTitle.equals(getResources().getStringArray(R.array.step_title)[5])) {
            //Log.i("ajc", "column ... inside description");
            // Description
            //String textDescription = thisLocationMaster.getDescription();
            //editText.setText(textDescription);
            //textView.setText(Html.fromHtml(textDescription));
            enteredText = thisLocationMaster.getDescription();
            //Log.i("ajc", "column ... inside description: " + enteredText);
        }

        editText.setText(enteredText);
        textView.setText(enteredText);
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

}

