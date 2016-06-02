package info.anth.lifecelebrated.AddLocationSteps;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

import info.anth.lifecelebrated.Data.DbLocationMaster;
import info.anth.lifecelebrated.Data.DbLocationNames;
import info.anth.lifecelebrated.Helpers.Helper;
import info.anth.lifecelebrated.R;
import info.anth.lifecelebrated.login.LoginActivity;
import info.anth.lifecelebrated.login.LoginRegisterDialog;

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
    private Context context;
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
        context = getActivity().getApplicationContext();
        editTextFamilyName = (EditText) mView.findViewById(R.id.family_name);
        editTextFirstName = (EditText) mView.findViewById(R.id.first_name);

        editTextBirthYear = (EditText) mView.findViewById(R.id.birth_year);
        editTextBirthDay = (EditText) mView.findViewById(R.id.birth_day);
        spinnerBirthMonth = (Spinner) mView.findViewById(R.id.birth_month);

        RadioButton radioButtonBirthYMD = (RadioButton) mView.findViewById(R.id.birth_date_format_ymd);
        radioButtonBirthYMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextBirthDay.getVisibility() == View.GONE) editTextBirthDay.setText("");
                editTextBirthDay.setVisibility(View.VISIBLE);
                spinnerBirthMonth.setVisibility(View.VISIBLE);
                birthFormat = "YMD";
            }
        });

        RadioButton radioButtonBirthYM = (RadioButton) mView.findViewById(R.id.birth_date_format_ym);
        radioButtonBirthYM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextBirthDay.setVisibility(View.GONE);
                editTextBirthDay.setText("1");
                spinnerBirthMonth.setVisibility(View.VISIBLE);
                birthFormat = "YM";
            }
        });

        RadioButton radioButtonBirthY = (RadioButton) mView.findViewById(R.id.birth_date_format_y);
        radioButtonBirthY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextBirthDay.setVisibility(View.GONE);
                editTextBirthDay.setText("1");
                spinnerBirthMonth.setVisibility(View.GONE);
                spinnerBirthMonth.setSelection(0);
                birthFormat = "Y";
                //Log.i("ajc2", String.valueOf(spinnerBirthMonth.getSelectedItem()) + " position: " + String.valueOf(spinnerBirthMonth.getSelectedItemPosition()));
            }
        });
        
        editTextDeathYear = (EditText) mView.findViewById(R.id.death_year);
        editTextDeathDay = (EditText) mView.findViewById(R.id.death_day);
        spinnerDeathMonth = (Spinner) mView.findViewById(R.id.death_month);
        
        RadioButton radioButtonDeathYMD = (RadioButton) mView.findViewById(R.id.death_date_format_ymd);
        radioButtonDeathYMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextDeathDay.getVisibility() == View.GONE) editTextDeathDay.setText("");
                editTextDeathDay.setVisibility(View.VISIBLE);
                spinnerDeathMonth.setVisibility(View.VISIBLE);
                deathFormat = "YMD";
            }
        });

        RadioButton radioButtonDeathYM = (RadioButton) mView.findViewById(R.id.death_date_format_ym);
        radioButtonDeathYM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextDeathDay.setVisibility(View.GONE);
                editTextDeathDay.setText("1");
                spinnerDeathMonth.setVisibility(View.VISIBLE);
                deathFormat = "YM";
            }
        });

        RadioButton radioButtonDeathY = (RadioButton) mView.findViewById(R.id.death_date_format_y);
        radioButtonDeathY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextDeathDay.setVisibility(View.GONE);
                editTextDeathDay.setText("1");
                spinnerDeathMonth.setVisibility(View.GONE);
                spinnerDeathMonth.setSelection(0);
                deathFormat = "Y";
                //Log.i("ajc2", String.valueOf(spinnerDeathMonth.getSelectedItem()) + " position: " + String.valueOf(spinnerDeathMonth.getSelectedItemPosition()));
            }
        });
        
        /*
        InputFilter filterDay = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                int input = Integer.parseInt(source.toString());
                if(input > 0 && input < 32 ) return null;
                //for (int i = start; i < end; i++) {
                //    if (!Character.isLetterOrDigit(source.charAt(i))) { // Accept only letter & digits ; otherwise just return
                //        Toast.makeText(context,"Invalid Input",Toast.LENGTH_SHORT).show();
                //        return "";
                //    }
                //}

                return "0";
            }

        };
        editTextBirthDay.setFilters(new InputFilter[]{filterDay});
        */

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NamesDialog.this.getDialog().cancel();
                    }
                });

        mDialog = builder.create();

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
                //saveData();
                //mDialog.dismiss();
            }
        });

        Log.i("ajc2", " Ref: " + mDbLocationMasterNamesRef.getRef().toString() + " key: " + mFirebaseItemKey);
    }

    public void populateData(){
        mDbLocationMasterNamesRef.child(mFirebaseItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DbLocationNames dbLocationNames = dataSnapshot.getValue(DbLocationNames.class);
                if (dbLocationNames != null) {
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

    public void dateFormat (String type) {
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
    
    public void saveData(int yearBirth, int monthBirth, int dayBirth, int yearDeath, int monthDeath, int dayDeath) {
        DbLocationNames dbLocationNames = new DbLocationNames(
                editTextFamilyName.getText().toString(),
                editTextFirstName.getText().toString(),
                birthFormat, yearBirth, monthBirth, dayBirth,
                deathFormat, yearDeath, monthDeath, dayDeath);

        Firebase tempRef = (mFirebaseItemKey == null) ? mDbLocationMasterNamesRef.push() : mDbLocationMasterNamesRef.child(mFirebaseItemKey);
        tempRef.setValue(dbLocationNames);
    }

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

        //Log.i("ajc2", "year: " + editTextBirthYear.getText().toString() + " month: " + monthBirth + " day: " + editTextBirthDay.getText().toString());
        //Log.i("ajc2", "year: " + yearBirth + " month: " + monthBirth + " day: " + dayBirth);

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
        
        //Log.i("ajc2", String.valueOf(Helper.validDateYMD(yearBirth, monthBirth, dayBirth)));

        if (noErrors && !Helper.validDateYMD(yearBirth, monthBirth, dayBirth)) {
            editTextBirthDay.setError("Invalid date");
            noErrors = false;
        }
        // *** Birth End ***

        // *** Death Start ***
        int yearDeath = Helper.stringToIntDefault0(editTextDeathYear.getText().toString());
        int monthDeath = spinnerDeathMonth.getSelectedItemPosition();
        int dayDeath = Helper.stringToIntDefault0(editTextDeathDay.getText().toString());

        //Log.i("ajc2", "year: " + editTextDeathYear.getText().toString() + " month: " + monthDeath + " day: " + editTextDeathDay.getText().toString());
        //Log.i("ajc2", "year: " + yearDeath + " month: " + monthDeath + " day: " + dayDeath);

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

        //Log.i("ajc2", String.valueOf(Helper.validDateYMD(yearDeath, monthDeath, dayDeath)));

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
