package info.anth.lifecelebrated.AddLocationSteps;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import info.anth.lifecelebrated.Data.DbLocationNames;
import info.anth.lifecelebrated.Helpers.Helper;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 6/1/2016.
 *
 *
 */
public class NamesDialog  extends DialogFragment {

    private static final String ARG_FIREBASE_REF = "firebase_reference";
    private static final String ARG_FIREBASE_ITEM_KEY = "firebase_item_key";

    public static final String LOG_TAG = NamesDialog.class.getSimpleName();

    private static View mView;
    private static AlertDialog mDialog;
    private String birthFormat = "YMD";
    private String deathFormat = "YMD";

    private static EditText editTextFamilyName;
    private static EditText editTextFirstName;
    private static EditText editTextBirthYear;
    private static EditText editTextBirthDay;
    private static Spinner spinnerBirthMonth;
    private static EditText editTextDeathYear;
    private static EditText editTextDeathDay;
    private static Spinner spinnerDeathMonth;

    
    private static Firebase mDbLocationMasterNamesRef;
    private static String mFirebaseItemKey;

    public static NamesDialog newInstance(String firebaseRef, String firebaseItemKey) {
        NamesDialog namesDialog = new NamesDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FIREBASE_REF, firebaseRef);
        args.putString(ARG_FIREBASE_ITEM_KEY, firebaseItemKey);
        namesDialog.setArguments(args);
        return namesDialog;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String tempString = getArguments().getString(ARG_FIREBASE_REF);
        if (tempString != null) mDbLocationMasterNamesRef = new Firebase(tempString);
        mFirebaseItemKey = getArguments().getString(ARG_FIREBASE_ITEM_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.dialog_fragment_al_names, null);
        editTextFamilyName = (EditText) mView.findViewById(R.id.family_name);
        editTextFirstName = (EditText) mView.findViewById(R.id.first_name);

        editTextBirthYear = (EditText) mView.findViewById(R.id.birth_year);
        editTextBirthDay = (EditText) mView.findViewById(R.id.birth_day);
        spinnerBirthMonth = (Spinner) mView.findViewById(R.id.birth_month);

        editTextDeathYear = (EditText) mView.findViewById(R.id.death_year);
        editTextDeathDay = (EditText) mView.findViewById(R.id.death_day);
        spinnerDeathMonth = (Spinner) mView.findViewById(R.id.death_month);

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NamesDialog.this.getDialog().cancel();
                    }
                });

        mDialog = builder.create();

        RadioGroup radioGroupBirth = (RadioGroup) mView.findViewById(R.id.radio_group_birth);
        radioGroupBirth.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.birth_date_format_ymd:
                        birthFormat = "YMD";
                        break;
                    case R.id.birth_date_format_ym:
                        birthFormat = "YM";
                        break;
                    case R.id.birth_date_format_y:
                        birthFormat = "Y";
                        break;
                }
                dateFormat("birth");
            }
        });

        RadioGroup radioGroupDeath = (RadioGroup) mView.findViewById(R.id.radio_group_death);
        radioGroupDeath.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.death_date_format_ymd:
                        deathFormat = "YMD";
                        break;
                    case R.id.death_date_format_ym:
                        deathFormat = "YM";
                        break;
                    case R.id.death_date_format_y:
                        deathFormat = "Y";
                        break;
                }
                dateFormat("death");
            }
        });

        if (mFirebaseItemKey != null) populateData();
        return mDialog;
    }

    @Override
    public void onStart(){
        super.onStart();
        mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    public void populateData(){
        mDbLocationMasterNamesRef.child(mFirebaseItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DbLocationNames dbLocationNames = dataSnapshot.getValue(DbLocationNames.class);
                if (dbLocationNames != null) {

                    // Names
                    editTextFamilyName.setText(dbLocationNames.getFamilyName());
                    editTextFirstName.setText(dbLocationNames.getFirstName());

                    // Birth
                    editTextBirthYear.setText(String.valueOf(dbLocationNames.getBirthYear()));
                    spinnerBirthMonth.setSelection(dbLocationNames.getBirthMonth());
                    editTextBirthDay.setText(String.valueOf(dbLocationNames.getBirthDay()));

                    birthFormat = dbLocationNames.getBirthFormat();
                    RadioGroup radioGroupBirth = (RadioGroup) mView.findViewById(R.id.radio_group_birth);
                    switch (birthFormat) {
                        case "YMD":
                            radioGroupBirth.check(R.id.birth_date_format_ymd);
                            break;
                        case "YM":
                            radioGroupBirth.check(R.id.birth_date_format_ym);
                            break;
                        case "Y":
                            radioGroupBirth.check(R.id.birth_date_format_y);
                            break;
                    }
                    dateFormat("birth");

                    // Death
                    editTextDeathYear.setText(String.valueOf(dbLocationNames.getDeathYear()));
                    spinnerDeathMonth.setSelection(dbLocationNames.getDeathMonth());
                    editTextDeathDay.setText(String.valueOf(dbLocationNames.getDeathDay()));

                    deathFormat = dbLocationNames.getDeathFormat();
                    RadioGroup radioGroupDeath = (RadioGroup) mView.findViewById(R.id.radio_group_death);
                    switch (deathFormat) {
                        case "YMD":
                            radioGroupDeath.check(R.id.death_date_format_ymd);
                            break;
                        case "YM":
                            radioGroupDeath.check(R.id.death_date_format_ym);
                            break;
                        case "Y":
                            radioGroupDeath.check(R.id.death_date_format_y);
                            break;
                    }
                    dateFormat("death");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    // **************
    // Used to format the date input fields
    // 
    public void dateFormat (String type) {
        // Birth       
        if (type.equals("birth")) {
            switch (birthFormat) {
                case "YMD":
                    if (editTextBirthDay.getVisibility() == View.GONE) editTextBirthDay.setText("");
                    editTextBirthDay.setVisibility(View.VISIBLE);
                    spinnerBirthMonth.setVisibility(View.VISIBLE);
                    break;
                case "YM":
                    editTextBirthDay.setVisibility(View.GONE);
                    editTextBirthDay.setText("1");
                    spinnerBirthMonth.setVisibility(View.VISIBLE);
                    break;
                case "Y":
                    editTextBirthDay.setVisibility(View.GONE);
                    editTextBirthDay.setText("1");
                    spinnerBirthMonth.setVisibility(View.GONE);
                    spinnerBirthMonth.setSelection(0);
                    break;
            }
        }

        // Death
        if (type.equals("death")) {
            switch (deathFormat) {
                case "YMD":
                    if (editTextDeathDay.getVisibility() == View.GONE) editTextDeathDay.setText("");
                    editTextDeathDay.setVisibility(View.VISIBLE);
                    spinnerDeathMonth.setVisibility(View.VISIBLE);
                    break;
                case "YM":
                    editTextDeathDay.setVisibility(View.GONE);
                    editTextDeathDay.setText("1");
                    spinnerDeathMonth.setVisibility(View.VISIBLE);
                    break;
                case "Y":
                    editTextDeathDay.setVisibility(View.GONE);
                    editTextDeathDay.setText("1");
                    spinnerDeathMonth.setVisibility(View.GONE);
                    spinnerDeathMonth.setSelection(0);
                    break;
            }
        }
    }


    // **************
    // Save data into Firebase
    // 
    public void saveData(int yearBirth, int monthBirth, int dayBirth, int yearDeath, int monthDeath, int dayDeath) {
        DbLocationNames dbLocationNames = new DbLocationNames(
                editTextFamilyName.getText().toString(),
                editTextFirstName.getText().toString(),
                birthFormat, yearBirth, monthBirth, dayBirth,
                deathFormat, yearDeath, monthDeath, dayDeath);

        Firebase tempRef = (mFirebaseItemKey == null) ? mDbLocationMasterNamesRef.push() : mDbLocationMasterNamesRef.child(mFirebaseItemKey);
        tempRef.setValue(dbLocationNames);
    }

    // **************
    // Data validation 
    //
    public void validateData() {
        Boolean noErrors = true;

        if (editTextFamilyName.getText().toString().isEmpty()) {
            editTextFamilyName.setError(getString(R.string.error_field_required));
            noErrors = false;
        }

        // *** Birth Start ***
        int yearBirth = Helper.stringToIntDefault0(editTextBirthYear.getText().toString());
        int monthBirth = spinnerBirthMonth.getSelectedItemPosition();
        int dayBirth = Helper.stringToIntDefault0(editTextBirthDay.getText().toString());

        if (yearBirth == 0) {
            editTextBirthYear.setError(getString(R.string.error_field_required));
            noErrors = false;
        } else {
            if (yearBirth < 1300 || yearBirth > 2100) {
                editTextBirthYear.setError("Invalid date range");
                noErrors = false;
            }
        }

        if (dayBirth == 0) {
            editTextBirthDay.setError(getString(R.string.error_field_required));
            noErrors = false;
        } else {
            if (dayBirth < 1 || dayBirth > 31) {
                editTextBirthDay.setError("Invalid entry");
                noErrors = false;
            }
        }
        
        if (noErrors && !Helper.validDateYMD(yearBirth, monthBirth, dayBirth)) {
            editTextBirthDay.setError("Invalid date");
            noErrors = false;
        }
        // *** Birth End ***

        // *** Death Start ***
        int yearDeath = Helper.stringToIntDefault0(editTextDeathYear.getText().toString());
        int monthDeath = spinnerDeathMonth.getSelectedItemPosition();
        int dayDeath = Helper.stringToIntDefault0(editTextDeathDay.getText().toString());

        if (yearDeath == 0) {
            editTextDeathYear.setError(getString(R.string.error_field_required));
            noErrors = false;
        } else {
            if (yearDeath < 1300 || yearDeath > 2100) {
                editTextDeathYear.setError("Invalid date range");
                noErrors = false;
            }
        }

        if (dayDeath == 0) {
            editTextDeathDay.setError(getString(R.string.error_field_required));
            noErrors = false;
        } else {
            if (dayDeath < 1 || dayDeath > 31) {
                editTextDeathDay.setError("Invalid entry");
                noErrors = false;
            }
        }

        if (noErrors && !Helper.validDateYMD(yearDeath, monthDeath, dayDeath)) {
            editTextDeathDay.setError("Invalid date");
            noErrors = false;
        }
        // *** Death End ***

        if (noErrors) {
            saveData(yearBirth, monthBirth, dayBirth, yearDeath, monthDeath, dayDeath);
            mDialog.dismiss();
        }
    }
}
