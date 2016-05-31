package info.anth.lifecelebrated.Helpers;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import info.anth.lifecelebrated.Data.DbLocationEditList;
import info.anth.lifecelebrated.Data.DbLocationMaster;
import info.anth.lifecelebrated.R;
import info.anth.lifecelebrated.login.LocalUserInfo;

/**
 * Created by Primary on 4/18/2016.
 */
public class TempDBLoaders {
/*    public static void loadEditList(Context context) {
        String firebasePath = context.getResources().getString(R.string.FIREBASE_BASE_REF);
        // add location
        firebasePath += "/" + context.getResources().getString(R.string.FIREBASE_CHILD_LOCATION);
        // add application user id (AUID)
        LocalUserInfo userInfo = new LocalUserInfo(context.getApplication());
        firebasePath += "/" + userInfo.auid;

        Firebase mDbLocationEditRef = new Firebase(firebasePath).child(context.getResources().getString(R.string.FIREBASE_CHILD_EDIT));
        final Firebase mDbLocationEditListRef = new Firebase(firebasePath).child(context.getResources().getString(R.string.FIREBASE_CHILD_EDIT_LIST));




        mDbLocationEditRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // edit level
                for (DataSnapshot keySnapshot: dataSnapshot.getChildren()) {
                    Log.i("ajc", "key: " + keySnapshot.getKey());
                    String key = keySnapshot.getKey();
                    for (DataSnapshot childSnapshot: keySnapshot.getChildren()) {
                        //Log.i("ajc", "child key: " + childSnapshot.getKey());
                        if (childSnapshot.getKey() == "master") {
                            DbLocationMaster thisMaster = childSnapshot.getValue(DbLocationMaster.class);
                            DbLocationEditList newListItem = new DbLocationEditList(key,thisMaster.getName(),thisMaster.getPrimaryImage()
                                    ,thisMaster.getImageUploaded(),thisMaster.getLocalImagePath());
                            mDbLocationEditListRef.push().setValue(newListItem);
                            //mDbLocationMasterRef = pushRefLocation.child("master");
                            //DbLocationMap newDbLocationMap = DbLocationMap.columns.createBlank();
                            //mDbLocationMapRef.setValue(newDbLocationMap);
                            Log.i("ajc", "In Master name:  " + thisMaster.getName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
   */
}
