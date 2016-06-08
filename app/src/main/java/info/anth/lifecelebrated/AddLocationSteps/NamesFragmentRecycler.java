package info.anth.lifecelebrated.AddLocationSteps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Data.DbLocationEditList;
import info.anth.lifecelebrated.Data.DbLocationNames;
import info.anth.lifecelebrated.Data.DbLocationStatusIC;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 5/31/2016.
 */
public class NamesFragmentRecycler extends Fragment {

    private static final String ARG_FIREBASE_EDIT_MASTER_REF = "firebase_edit_master_ref";
    private static final String ARG_FIREBASE_EDIT_LIST_REF = "firebase_edit_list_ref";
    private static final String ARG_FIREBASE_EDIT_STATUS_IC_REF = "firebase_edit_status_ic_ref";

    //private static int currentPage;
    private static Firebase mDbLocationMasterNamesRef;
    private static Firebase mDbLocationEditListRef;
    private static Firebase mDbLocationStatusICRef;

    private ValueEventListener valueEventListener;

    FirebaseRecyclerAdapter mChildAdapter;
    RecyclerView recycler;

    public NamesFragmentRecycler() {}

    /**
     * Returns a new instance of this fragment for the given page
     * number.
     */
    public static NamesFragmentRecycler newInstance(String firebaseEditMaster, String firebaseEditList, String firebaseEditStatusIC) {
        NamesFragmentRecycler fragment = new NamesFragmentRecycler();
        Bundle args = new Bundle();
        args.putString(ARG_FIREBASE_EDIT_MASTER_REF, firebaseEditMaster);
        args.putString(ARG_FIREBASE_EDIT_LIST_REF, firebaseEditList);
        args.putString(ARG_FIREBASE_EDIT_STATUS_IC_REF, firebaseEditStatusIC);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recycler_step_names, container, false);
        //View rootView = super.onCreateView(inflater, container, savedInstanceState);

        // set Names sub reference
        String tempString = getArguments().getString(ARG_FIREBASE_EDIT_MASTER_REF);
        Firebase dbLocationMasterRef = null;
        if (tempString != null) dbLocationMasterRef = new Firebase(tempString);
        if (dbLocationMasterRef == null) {
            return rootView;
        }
        mDbLocationMasterNamesRef = dbLocationMasterRef.child(getResources().getString(R.string.FIREBASE_CHILD_MASTER_NAMES));

        tempString = getArguments().getString(ARG_FIREBASE_EDIT_LIST_REF);
        if (tempString != null) mDbLocationEditListRef = new Firebase(tempString);
        tempString = getArguments().getString(ARG_FIREBASE_EDIT_STATUS_IC_REF);
        if (tempString != null) mDbLocationStatusICRef = new Firebase(tempString);


        recycler = (RecyclerView) rootView.findViewById(R.id.namesList);
        //Log.i("ajc2", "0 Recycler : " + String.valueOf(recycler) + " dbref: " + String.valueOf(mDbLocationMasterNamesRef) );
        //recycler.setHasFixedSize(true);
        /*recycler.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }});*/
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //NamesRecyclerAdapter myRecyclerViewAdapter = new NamesRecyclerAdapter(getContext(), mDbLocationMasterNamesRef);

        //recycler.setAdapter(myRecyclerViewAdapter);
/*
        //insert dummy items
        myRecyclerViewAdapter.add(0, new DbLocationNames("Smith", "Eric"));
        myRecyclerViewAdapter.add(1, new DbLocationNames("Smith", "Android"));
        myRecyclerViewAdapter.add(0, new DbLocationNames("Smith", "Android-er"));
        myRecyclerViewAdapter.add(2, new DbLocationNames("Smith", "Google"));
        myRecyclerViewAdapter.add(3, new DbLocationNames("Smith", "android dev"));
        myRecyclerViewAdapter.add(0, new DbLocationNames("Smith", "android-er.blogspot.com"));
        myRecyclerViewAdapter.add(4, new DbLocationNames("Smith", "Peter"));
        myRecyclerViewAdapter.add(4, new DbLocationNames("Smith", "Paul"));
        myRecyclerViewAdapter.add(4, new DbLocationNames("Smith", "Mary"));
        myRecyclerViewAdapter.add(4, new DbLocationNames("Smith", "May"));
        myRecyclerViewAdapter.add(4, new DbLocationNames("Smith", "Divid"));
        myRecyclerViewAdapter.add(4, new DbLocationNames("Smith", "Frankie"));

        //recycler.setAdapter(new BasicListAdapter(this));
*/
        mChildAdapter = new FirebaseRecyclerAdapter<DbLocationNames, LocationNamesViewHolder>
                (DbLocationNames.class, R.layout.x_step_names_list, LocationNamesViewHolder.class, mDbLocationMasterNamesRef) {
            @Override
            public void populateViewHolder(LocationNamesViewHolder locationNamesViewHolder
                    , DbLocationNames dbLocationNames, int position) {

                //Log.i("ajc2", "in populateViewHolder");
                // define the text fields
                //locationNamesViewHolder.editFamilyName.setText(dbLocationNames.getFamilyName());
                //locationNamesViewHolder.editFirstName.setText(dbLocationNames.getFirstName());

                //String tempString = dbLocationNames.getFamilyName() + ", " + dbLocationNames.getFirstName();
                //locationNamesViewHolder.textName.setText(tempString);
                //tempString = "June 12, 1944 - May 3, 2010";
                //locationNamesViewHolder.textDates.setText(tempString);


                locationNamesViewHolder.textName.setText(DbLocationNames.columns.nameToString(dbLocationNames));
                locationNamesViewHolder.textDates.setText(DbLocationNames.columns.dateRangeToString(dbLocationNames, getActivity()));

                //Log.i("ajc2", "in populateViewHolder : " + dbLocationNames.getFamilyName() );
            }
            @Override
            public void onBindViewHolder(final LocationNamesViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                //Log.i("ajc2", "in onBindViewHolder");
                holder.mItem = (DbLocationNames) mChildAdapter.getItem(position);
                holder.mNameKey = mChildAdapter.getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i("ajc2", "Click position: " + String.valueOf(holder.getAdapterPosition()) + " key: " + holder.mNameKey);
                        NamesDialog namesDialog = NamesDialog.newInstance(mDbLocationMasterNamesRef.getRef().toString(), holder.mNameKey);
                        namesDialog.show(getChildFragmentManager(),"");
                    }
                });
            }

        };

        //Log.i("ajc2","adapter count:  " + String.valueOf(mChildAdapter.getItemCount()));
        recycler.setAdapter(mChildAdapter);



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

       // recycler.setAdapter(mChildAdapter);
        //recycler.refreshDrawableState();
        //Log.i("ajc2","in onResume mChildAdapter: " + String.valueOf(mChildAdapter.hasObservers()) + " : " + String.valueOf(mChildAdapter.getItemCount()) + " recycler: " + recycler.getChildCount());
        addDBListenerUpdateEditList();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDbLocationMasterNamesRef != null && valueEventListener != null) {
            //Log.i(LOG_TAG, "before remove");
            mDbLocationMasterNamesRef.removeEventListener(valueEventListener);
        }
    }

    public static class LocationNamesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        //EditText editFamilyName;
        //EditText editFirstName;
        TextView textName;
        TextView textDates;
        public DbLocationNames mItem;
        public String mNameKey;

        public LocationNamesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //editFamilyName = (EditText)itemView.findViewById(R.id.entered_text_family);
            //editFirstName = (EditText)itemView.findViewById(R.id.entered_text_first);
            textName = (TextView) itemView.findViewById(R.id.textview_person);
            textDates = (TextView) itemView.findViewById(R.id.textview_dates);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ajc2","in onDestroy ");
        mChildAdapter.cleanup();
    }

    public void addDBListenerUpdateEditList() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    // Update Status Icon
                    Map<String, Object> icon = new HashMap<>();
                    icon.put(DbLocationStatusIC.columns.COLUMN_NAME, "ic_step_check_24dp");
                    mDbLocationStatusICRef.updateChildren(icon);

                    // Update EditList Msg field
                    Map<String, Object> msg = new HashMap<>();
                    msg.put(DbLocationEditList.columns.COLUMN_NAME_MSG, "");
                    mDbLocationEditListRef.updateChildren(msg);
                } else {
                    // Update Status Icon
                    Map<String, Object> icon = new HashMap<>();
                    icon.put(DbLocationStatusIC.columns.COLUMN_NAME, getResources().getString(R.string.name_ic_default));
                    mDbLocationStatusICRef.updateChildren(icon);

                    // Update EditList Msg field in case it was deleted
                    Map<String, Object> msg = new HashMap<>();
                    msg.put(DbLocationEditList.columns.COLUMN_NAME_MSG, getResources().getString(R.string.name_msg_default));
                    mDbLocationEditListRef.updateChildren(msg);
                }
                Log.i("ajc2", "Children: " + String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        mDbLocationMasterNamesRef.addValueEventListener(valueEventListener);
    }
}
