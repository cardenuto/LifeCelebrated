package info.anth.lifecelebrated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

import info.anth.lifecelebrated.Data.DbLocationEditList;
import info.anth.lifecelebrated.Helpers.Installation;
import info.anth.lifecelebrated.login.LocalUserInfo;

/**
 * Created by Primary on 4/12/2016.
 *
 */
public class EditActivityFragment extends Fragment {

    public static final String LOG_TAG = EditActivityFragment.class.getSimpleName();

    //private Firebase mFirebaseRef;
    private static Firebase mDbLocationEditListRef;
    //private ValueEventListener valueEventListenerMaster;
    FirebaseRecyclerAdapter mAdapter;

    Context context;

    public EditActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            // ------------------------------------
            // Manage database
            //
            String firebasePath = getResources().getString(R.string.FIREBASE_BASE_REF);
            // add path to users
            firebasePath += "/" + getResources().getString(R.string.FIREBASE_USERS);
            // add application user id (AUID)
            LocalUserInfo userInfo = new LocalUserInfo(getActivity());
            firebasePath += "/" + userInfo.auid;

            mDbLocationEditListRef = new Firebase(firebasePath).child(getResources().getString(R.string.FIREBASE_CHILD_EDIT_LIST));


            //mDbLocationEditListRef = firebaseRef.push();
            //mDbLocationMasterRef = pushRefLocation.child("master");
            //
            // ------------------------------------


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        context = getContext();

        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.myList);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //recycler.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));

        mAdapter = new FirebaseRecyclerAdapter<DbLocationEditList, LocationEditListMessageViewHolder>
                (DbLocationEditList.class, R.layout.x_edit_list, LocationEditListMessageViewHolder.class, mDbLocationEditListRef) {
            @Override
            public void populateViewHolder(LocationEditListMessageViewHolder locationEditListMessageViewHolder
                    , DbLocationEditList dbLocationEditList, int position) {

                // define the text fields
                locationEditListMessageViewHolder.editListTitle.setText(dbLocationEditList.getName());
                //locationEditListMessageViewHolder.editListText.setText(dbLocationEditList.getDescription());

                loadImage(dbLocationEditList, locationEditListMessageViewHolder.editListImage, locationEditListMessageViewHolder.editListImageText);

            }
            /**
             * Ability to click the holder?
             */
            @Override
            public void onBindViewHolder(final LocationEditListMessageViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);

                holder.mItem = (DbLocationEditList) mAdapter.getItem(position);
                holder.mEditListKey = mAdapter.getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i("ajc", "Click location: " + holder.mItem.getEditKey() + " edit list key: " + holder.mEditListKey);

                        Intent intent = new Intent(context, Steps.class);
                        intent.putExtra(Steps.REQUEST_CURRENT_STEP, 0);
                        intent.putExtra(Steps.REQUEST_CURRENT_LOCATION, holder.mItem.getEditKey());
                        intent.putExtra(Steps.REQUEST_CURRENT_EDIT_LIST_ITEM, holder.mEditListKey);
                        context.startActivity(intent);


                        /*
                        Context context = v.getContext();
                        Intent intent = new Intent(context, StoneActivity.class);
                        intent.putExtra(StoneActivity.REQUEST_CURRENT_STEP, StoneTBD.columns.getCurrentStep(holder.mItem));
                        intent.putExtra(StoneActivityFragment.ARGUMENT_STONEID, holder.mItem.getStoneID());
                        context.startActivity(intent);
                        */
                    }
                });
            }
        };
        recycler.setAdapter(mAdapter);

        return rootView;

    }

    public static class LocationEditListMessageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView editListTitle;
        TextView editListImageText;
        ImageView editListImage;
        public DbLocationEditList mItem;
        public String mEditListKey;

        public LocationEditListMessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            editListTitle = (TextView)itemView.findViewById(R.id.edit_list_title);
            editListImageText = (TextView)itemView.findViewById(R.id.edit_list_image_text);
            editListImage = (ImageView)itemView.findViewById(R.id.edit_list_image);
        }
    }

    private void loadImage(DbLocationEditList dbLocationEditList, final ImageView imageView, TextView textView) {
        // // TODO: 4/12/2016 this crashed because getInstance returned null
        // // TODO: 4/12/2016 change transform to reflect the size of the screen

        String url_string = "";
        Boolean displayImage = true;
        // make sure the image text view is hidden
        textView.setVisibility(View.GONE);

        if (dbLocationEditList.getImageUploaded()) {
            Cloudinary cloudinary = LifeCelebratedApplication.getInstance(getContext()).getCloudinary();
            url_string = cloudinary.url().transformation(new Transformation().width(150).height(100)).generate(dbLocationEditList.getPrimaryImage());
        } else {
            String deviceId = Installation.id(context);
            if (!dbLocationEditList.getLocalImagePath().equals("")) {
                // There is a local file
                if (dbLocationEditList.getDeviceID().equals(deviceId)) {
                    // The file is on this device
                    //url_string = dbLocationEditList.getPrimaryImage();
                    url_string = Uri.fromFile(new File(dbLocationEditList.getLocalImagePath())).toString();
                    // Added to correct file paths with spaces
                    url_string = Uri.decode(url_string);
                } else {
                    // The file is on another device
                    textView.setVisibility(View.VISIBLE);
                    displayImage = false;
                }
            }
            // Else there isn't an image chosen, blank paths will show the camera
        }

        //ImageView imageView = (ImageView) rootView.findViewById(R.id.main_image);
        //final ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.loading);

        //imageView.setImageURI(Uri.parse(url_string));

        //Log.i("ajc", url_string);
        if (displayImage) {

            // define options UIL library
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_camera_alt_24dp)
                    .showImageOnFail(R.drawable.ic_cancel_24dp)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();

            // process image loader UIL Library
            ImageLoader.getInstance().displayImage(url_string, imageView, options,
                    // code to add new functions
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            //Log.w("ajc", "in onloadingStarted uri: " + imageUri);
                            //spinner.setVisibility(View.VISIBLE);
                            //pictureButton.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Log.i(LOG_TAG, message);
                            //Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                            //spinner.setVisibility(View.GONE);
                            //pictureButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            //spinner.setVisibility(View.GONE);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    }
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
