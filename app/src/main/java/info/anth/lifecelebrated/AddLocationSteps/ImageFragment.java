package info.anth.lifecelebrated.AddLocationSteps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.annotations.NotNull;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Data.DbLocationEditList;
import info.anth.lifecelebrated.Data.DbLocationMaster;
import info.anth.lifecelebrated.Data.DbLocationStatusIC;
import info.anth.lifecelebrated.Helpers.Helper;
import info.anth.lifecelebrated.Helpers.Installation;
import info.anth.lifecelebrated.Helpers.LocalImageInfo;
import info.anth.lifecelebrated.LifeCelebratedApplication;
import info.anth.lifecelebrated.MainActivity;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 4/6/2016.
 *
 *
 */
public class ImageFragment extends Fragment {

    /**
     * The fragment argument representing the page number for this
     * fragment.
     */
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_FIREBASE_EDIT_STATUS_IC_REF = "firebase_edit_status_ic_ref";
    private static final String ARG_FIREBASE_EDIT_MASTER_REF = "firebase_edit_master_ref";
    private static final String ARG_FIREBASE_EDIT_LIST_REF = "firebase_edit_list_ref";

    public static final String LOG_TAG = ImageFragment.class.getSimpleName();

    private static int RESULT_LOAD_IMAGE = 1;
    private static Context context;
    private static View rootView;

    private static Firebase mDbLocationEditListRef;
    private static Firebase mDbLocationMasterRef;
    private static Firebase mDbLocationStatusICRef;
    private ValueEventListener valueEventListener;
    private static String filePath;
    private static String imageName;

    private static String permissionsCallCode;

    public ImageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given page
     * number.
     */
    public static ImageFragment newInstance(int pageNumber, String firebaseEditStatusIC, String firebaseEditMaster, String firebaseEditList) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        args.putString(ARG_FIREBASE_EDIT_STATUS_IC_REF, firebaseEditStatusIC);
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
        String tempString = getArguments().getString(ARG_FIREBASE_EDIT_STATUS_IC_REF);
        if (tempString != null) mDbLocationStatusICRef = new Firebase(tempString);
        tempString = getArguments().getString(ARG_FIREBASE_EDIT_MASTER_REF);
        if (tempString != null) mDbLocationMasterRef = new Firebase(tempString);
        tempString = getArguments().getString(ARG_FIREBASE_EDIT_LIST_REF);
        if (tempString != null) mDbLocationEditListRef = new Firebase(tempString);

        rootView = inflater.inflate(R.layout.fragment_al_image, container, false);
        context = getContext();

        // set page parameters
        int currentPage = position + 1;
        final Resources rw = getResources();
        final int stepArrayNo = rw.getIntArray(R.array.step_array_no)[position];

        // -------
        // Process the heading
        //
        TextView headingStepTextView = (TextView) rootView.findViewById(R.id.heading_step);
        TextView headingStepNoTextView = (TextView) rootView.findViewById(R.id.heading_number);
        TextView headingStepLineTextView = (TextView) rootView.findViewById(R.id.heading_line);
        TextView headingStepHRLine1TextView = (TextView) rootView.findViewById(R.id.heading_hr_line1);
        TextView headingStepHRLine2TextView = (TextView) rootView.findViewById(R.id.heading_hr_line2);
        TextView headingTitleTextView = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingTextView = (TextView) rootView.findViewById(R.id.heading_text);

        Button cameraButton = (Button) rootView.findViewById(R.id.image_camera);
        Button loadButton = (Button) rootView.findViewById(R.id.image_load);
        Button uploadButton = (Button) rootView.findViewById(R.id.image_upload);

        int headingColor;

        // set the colors for the number
        try {
            headingColor = rw.getIntArray(R.array.step_text_color)[stepArrayNo];
            headingStepTextView.setTextColor(headingColor);
            headingStepNoTextView.setTextColor(headingColor);
            headingStepLineTextView.setBackgroundColor(headingColor);
            headingStepHRLine1TextView.setBackgroundColor(headingColor);
            headingStepHRLine2TextView.setBackgroundColor(headingColor);

            cameraButton.setBackgroundColor(headingColor);
            loadButton.setBackgroundColor(headingColor);
            uploadButton.setBackgroundColor(headingColor);

        } catch (Exception e) {
            // do nothing at this time
        }

        // set the text values
        headingStepNoTextView.setText(String.valueOf(currentPage));
        headingTitleTextView.setText(Helper.resourceString(getContext(), R.array.step_title, stepArrayNo));
        headingTextView.setText(Html.fromHtml(Helper.resourceString(getContext(), R.array.step_text, stepArrayNo)));
        //
        // -------

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageCamera();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFile();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startUpload(filePath);
            }
        });

        return rootView;
    }

    public void getImageCamera() {
        // Check permissions
        permissionsCallCode = "camera";
        checkPermissionExternal();
    }

    public void getImageCameraAllowed() {
        // camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void getImageFile() {
        // Check permissions
        permissionsCallCode = "load";
        checkPermissionExternal();
    }

    public void getImageFileAllowed() {
        // internet
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            // get the path of the file
            Uri selectedImage = data.getData();
            //Log.i("ajc", "Old URI: " + selectedImage.toString());
            //// TODO: 4/12/2016 update the database not load image
            //loadLocalImage(selectedImage);

            LocalImageInfo bitmapInfo = new LocalImageInfo(context, selectedImage);

            // get this device code
            String device = Installation.id(context);

            // Update the database
            if (mDbLocationMasterRef != null) {
                Map<String, Object> textFields = new HashMap<>();
                textFields.put(DbLocationMaster.columns.COLUMN_PRIMARY_IMAGE, selectedImage.toString());
                textFields.put(DbLocationMaster.columns.COLUMN_LOCAL_IMAGE_PATH, bitmapInfo.path);
                textFields.put(DbLocationMaster.columns.COLUMN_DEVICE_ID, device);
                mDbLocationMasterRef.updateChildren(textFields);
            }
            // Update Edit List
            if (mDbLocationEditListRef != null) {
                Map<String, Object> textFields = new HashMap<>();
                textFields.put(DbLocationEditList.columns.COLUMN_PRIMARY_IMAGE, selectedImage.toString());
                textFields.put(DbLocationEditList.columns.COLUMN_LOCAL_IMAGE_PATH, bitmapInfo.path);
                textFields.put(DbLocationEditList.columns.COLUMN_DEVICE_ID, device);
                textFields.put(DbLocationEditList.columns.COLUMN_PICTURE_MSG, "Upload Main Picture");
                mDbLocationEditListRef.updateChildren(textFields);
            }

            if (mDbLocationStatusICRef != null) {
                Map<String, Object> textFields = new HashMap<>();
                textFields.put(DbLocationStatusIC.columns.COLUMN_PICTURE, "ic_step_cloud_upload_24dp");
                mDbLocationStatusICRef.updateChildren(textFields);
            }
        }
    }

    private void loadLocalImage(Uri imageUri) {

        int targetW;
        //int targetH;

        LocalImageInfo bitmapInfo = new LocalImageInfo(context, imageUri);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.main_image);
        View parent = (View) imageView.getParent();

        //Log.i("ajc", "--------------------------------------");
        //Log.i("ajc", imageUri.toString());

        //calculate scaleFactor
        targetW = parent.getWidth();
        //targetH = imageView.getHeight();
        //Log.i("ajc", "width: " + targetW + " height: " + targetH);

        // default to size of screen
        int scaleFactor = 1;
        if (targetW > 0) scaleFactor = (bitmapInfo.orientation == 90 || bitmapInfo.orientation == 270) ? bitmapInfo.height / targetW : bitmapInfo.width / targetW;

        //Log.i("ajc", "Photo: " + bitmapInfo.path + " width: " + bitmapInfo.width + " height: " + bitmapInfo.height + " scale: " + scaleFactor + " orientation: " + bitmapInfo.orientation);

        // Decode the image file into a Bitmap sized to fill the View
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap tempBitmap = BitmapFactory.decodeFile(bitmapInfo.path, bmOptions);
        Bitmap bitmap = LocalImageInfo.rotateBitmap(tempBitmap, bitmapInfo.orientation);

        imageView.setImageBitmap(bitmap);
        //savedScaleFactor = scaleFactor;
        //tempBitmap.recycle();
        //Log.i("ajc", "NEW width: " + imageView.getWidth() + " height: " + imageView.getHeight());
    }

    private void loadCloudinaryImage(DbLocationMaster dbLocationMater) {
        // // TODO: 4/12/2016 this crashed because getInstance returned null
        // // TODO: 4/12/2016 change transform to reflect the size of the screen
        Cloudinary cloudinary = LifeCelebratedApplication.getInstance(getContext()).getCloudinary();
        String url_string = cloudinary.url().transformation(new Transformation().width(600)).generate(dbLocationMater.getPrimaryImage());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.main_image);
        final ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.loading);

        //imageView.setImageURI(Uri.parse(url_string));

        //Log.i("ajc", url_string);

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
                        spinner.setVisibility(View.VISIBLE);
                        //pictureButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
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
                        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                        //pictureButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        spinner.setVisibility(View.GONE);
                    }
                });

    }

    private void startUpload(final String filePath) {
        //Log.i("ajc", "in StartUpload");
        Button uploadButton = (Button) rootView.findViewById(R.id.image_upload);
        uploadButton.setVisibility(View.GONE);

        ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.loading);
        spinner.setVisibility(View.VISIBLE);

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... paths) {
                //Log.i("ajc", "Running upload task");

                Cloudinary cloudinary = new Cloudinary();
                imageName = null;
                //String testPath = gimageUri.getPath();
                //testPath = "/storage/emulated/0/Pictures/JPEG_20160319_140404_773191082.jpg";

                File myFile = new File(filePath);
                if (myFile.exists()) {
                    //Log.i("ajc", "file exist");
                    //Log.i("ajc", "onLoadComplete uri path: " + testPath);
                    Map cloudinaryResult;
                    try {
                        cloudinaryResult = cloudinary.uploader().unsignedUpload(myFile, "unw122ps", ObjectUtils.asMap("cloud_name", "dqeqimfy5"));
                        imageName = cloudinaryResult.get("public_id") + "." + cloudinaryResult.get("format");
                        //Log.i("ajc", "Uploaded file: " + cloudinaryResult.toString() + " Name: " + imageName);
                        //Iterator resultsIterator = cloudinaryResult.keySet().iterator();
                    } catch (RuntimeException e) {
                        //Log.e("ajc", "Error uploading file: " + e.toString());
                        return "Error uploading file: " + e.toString();
                    } catch (IOException e) {
                        //Log.e("ajc", "Error uploading file: " + e.toString());
                        return "Error uploading file: " + e.toString();
                    }
                    //Log.i("ajc", "File: " + cloudinaryResult.get("public_id") + "." + cloudinaryResult.get("format"));
                } else {
                    Log.i("ajc", "file does NOT exist");
                }

                return null;
            }

            protected void onPostExecute(String error) {
                if (error == null) {
                    // update stone To Be Done database saying picture was taken
                    if (imageName != null) {
                        Map<String, Object> imageData = new HashMap<>();
                        imageData.put(DbLocationMaster.columns.COLUMN_IMAGE_UPLOADED, true);
                        imageData.put(DbLocationMaster.columns.COLUMN_PRIMARY_IMAGE, imageName);
                        mDbLocationMasterRef.updateChildren(imageData);

                        // Update Edit List
                        Map<String, Object> imageDataEditList = new HashMap<>();
                        imageDataEditList.put(DbLocationEditList.columns.COLUMN_IMAGE_UPLOADED, true);
                        imageDataEditList.put(DbLocationEditList.columns.COLUMN_PRIMARY_IMAGE, imageName);
                        imageDataEditList.put(DbLocationEditList.columns.COLUMN_PICTURE_MSG, "");
                        mDbLocationEditListRef.updateChildren(imageDataEditList);

                        // Update Status Icon
                        Map<String, Object> icon = new HashMap<>();
                        icon.put(DbLocationStatusIC.columns.COLUMN_PICTURE, "ic_step_check_24dp");
                        mDbLocationStatusICRef.updateChildren(icon);
                    } else {
                        Log.e("ajc", "Name in Error: it is null.");
                    }
                } else {
                    Log.e("ajc", "Is Error?: " + error);
                }
                //Log.i("ajc", "onPostExecution!");
            }
        };
        task.execute(filePath);
    }

    public void refreshScreen(final DbLocationMaster thisLocationMaster) {
        //Log.i(LOG_TAG, "In Refresh: " + thisLocationMaster.getPrimaryImage());
        // Get Button and Image Views
        Button cameraButton = (Button) rootView.findViewById(R.id.image_camera);
        Button loadButton = (Button) rootView.findViewById(R.id.image_load);
        final Button uploadButton = (Button) rootView.findViewById(R.id.image_upload);
        ImageView mainImageView = (ImageView) rootView.findViewById(R.id.main_image);
        TextView mainImageTextView = (TextView) rootView.findViewById(R.id.main_image_text);

        filePath = thisLocationMaster.getLocalImagePath();
        final Uri fileUri = Uri.parse(thisLocationMaster.getPrimaryImage());

        // initialize the main image text
        mainImageTextView.setVisibility(View.GONE);
        mainImageView.setVisibility(View.VISIBLE);

        //Log.e("ajc","filePath: " + filePath + " getImageUploaded: " + thisLocationMaster.getImageUploaded() );
        if(thisLocationMaster.getImageUploaded()) {
            //Log.i(LOG_TAG, "In getImageUploaded");
            loadButton.setVisibility(View.GONE);
            cameraButton.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);

            mainImageView.post(new Runnable() {
                @Override
                public void run() {
                    loadCloudinaryImage(thisLocationMaster);
               }
            });
        } else if (!filePath.equals("")) {
            //Log.i(LOG_TAG, "In fileLocationString");
            loadButton.setVisibility(View.GONE);
            cameraButton.setVisibility(View.GONE);

            // check correct device
            String deviceId = Installation.id(context);
            if (thisLocationMaster.getDeviceID().equals(deviceId)) {
                // check for local file - may have been deleted
                File file = new File(filePath);
                if (file.exists()) {
                    //Log.i(LOG_TAG, "In exists");
                    //Load the file
                    mainImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            //Log.i(LOG_TAG, "In post");
                            loadLocalImage(fileUri);
                            uploadButton.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    cameraButton.setVisibility(View.VISIBLE);
                    uploadButton.setVisibility(View.VISIBLE);
                    uploadButton.setVisibility(View.GONE);
                    mainImageView.setImageResource(R.drawable.ic_cloud_off_24dp);
                }
            } else {
                mainImageTextView.setVisibility(View.VISIBLE);
                uploadButton.setVisibility(View.GONE);
                mainImageView.setVisibility(View.GONE);
            }
        }
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

        if (mDbLocationMasterRef != null && valueEventListener != null) {
            //Log.i(LOG_TAG, "before remove");
            mDbLocationMasterRef.removeEventListener(valueEventListener);
        }
    }

    @Override
    public void onResume() {
        super.onStart();

        if (mDbLocationMasterRef != null)
            setDBListener();

    }

    // ------------------------------------
    // Permissions
    // ------------------------------------

    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1492;

    // in an activity you can call ActivityCompat in fragment needs to be requestPermissions in order to call callback
    public void checkPermissionExternal() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this,
                requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }
        processCallCode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        Log.i("ajc2","PermissionResults before");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.i("ajc2","PermissionResults Granted");
                    processCallCode();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void processCallCode() {
        Log.i("ajc2","processCallCode");
        switch (permissionsCallCode) {
            case "camera":
                getImageCameraAllowed();
                break;
            case "load":
                getImageFileAllowed();
                break;
        }

    }

    // Permissions end
    // ------------------------------------


}
