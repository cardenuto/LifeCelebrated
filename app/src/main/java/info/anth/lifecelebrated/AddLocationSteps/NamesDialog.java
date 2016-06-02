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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import info.anth.lifecelebrated.Data.DbLocationMaster;
import info.anth.lifecelebrated.Data.DbLocationNames;
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
            }
        });

        RadioButton radioButtonBirthYM = (RadioButton) mView.findViewById(R.id.birth_date_format_ym);
        radioButtonBirthYM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextBirthDay.setVisibility(View.GONE);
                editTextBirthDay.setText("1");
                spinnerBirthMonth.setVisibility(View.VISIBLE);
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
            }
        });

        RadioButton radioButtonDeathYM = (RadioButton) mView.findViewById(R.id.death_date_format_ym);
        radioButtonDeathYM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextDeathDay.setVisibility(View.GONE);
                editTextDeathDay.setText("1");
                spinnerDeathMonth.setVisibility(View.VISIBLE);
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
                saveData();
                mDialog.dismiss();
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
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void saveData() {
        DbLocationNames dbLocationNames = new DbLocationNames(
                editTextFamilyName.getText().toString(),
                editTextFirstName.getText().toString());

        Firebase tempRef = (mFirebaseItemKey == null) ? mDbLocationMasterNamesRef.push() : mDbLocationMasterNamesRef.child(mFirebaseItemKey);
        tempRef.setValue(dbLocationNames);
    }

    public void clickBirthFormatYMD (View view) {
        editTextBirthDay.setVisibility(View.VISIBLE);
        spinnerBirthMonth.setVisibility(View.VISIBLE);
    }

    public void clickBirthFormatYM (View view) {
        editTextBirthDay.setVisibility(View.GONE);
        editTextBirthDay.setText("1");
        spinnerBirthMonth.setVisibility(View.VISIBLE);
    }

    public void clickBirthFormatY (View view) {
        editTextBirthDay.setVisibility(View.GONE);
        editTextBirthDay.setText("1");
        spinnerBirthMonth.setVisibility(View.GONE);
        spinnerBirthMonth.setId(0);
    }
}
