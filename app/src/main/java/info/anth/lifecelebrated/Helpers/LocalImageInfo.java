package info.anth.lifecelebrated.Helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by Primary on 4/11/2016.
 */
public class LocalImageInfo {
    public int width;
    public int height;
    public int orientation;
    public String path;

    public LocalImageInfo(Context context, Uri uri) {

        // Initialize data
        width = 0;
        height = 0;
        orientation = 0;
        path = "";

        path = getRealPathFromURI(context, uri);
        if(!path.equals("")) {
            try {
                orientation = getExifOrientationFromPath(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // set width and height
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        width = bmOptions.outWidth;
        height = bmOptions.outHeight;

        //Log.i("ajc", "path: " + path + " orientation: " + orientation + " width: " + width + " height: " + height);

        /*
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        // set path
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (cursor.getString(columnIndex) != null) path = cursor.getString(columnIndex);
            cursor.close();

            // set orientation
            orientation = getExifOrientation(context, uri);

            // set width and height
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bmOptions);
            if (bmOptions != null) {
                width = bmOptions.outWidth;
                height = bmOptions.outHeight;
            }

            Log.e("ajc", "path: " + path + " orientation: " + orientation + " width: " + width + " height: " + height);

            try {
                InputStream input = context.getContentResolver().openInputStream(uri);
                BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
                onlyBoundsOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
                input.close();
                //if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
                width = onlyBoundsOptions.outWidth;
                height = onlyBoundsOptions.outHeight;
                path = getRealPathFromURI(context, uri);
                //if (path != null) orientation = getExifOrientation(context, Uri.fromFile(new File(path)));

                ExifInterface exif = new ExifInterface(path);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                Log.w("ajc", "TRY path: " + path + " orientation: " + orientation + " width: " + width + " height: " + height);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        */
    }

    public LocalImageInfo(int width, int height, int orientation, String path) {
        this.width = width;
        this.height = height;
        this.orientation = orientation;
        this.path = path;
    }

    private int getExifOrientationFromPath(String localPath) throws IOException {

        ExifInterface exif = new ExifInterface(localPath);
        int orientationCalc = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientationCalc) {
            case ExifInterface.ORIENTATION_NORMAL:
                return 0;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            case 90:
                return 90;
            case 180:
                return 180;
            case 270:
                return 270;
            case -90:
                return 270;
        }

        return 0;
    }

    /*
    private int getExifOrientation(Context context, Uri uri) {

        int orientationCalc = ExifInterface.ORIENTATION_NORMAL;

        String[] orientationColumn = { MediaStore.Images.ImageColumns.ORIENTATION };
        Cursor cursor = context.getContentResolver().query(uri, orientationColumn, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                orientationCalc = cursor.getInt(0);
            }
            cursor.close();
        }

        switch (orientationCalc) {
            case ExifInterface.ORIENTATION_NORMAL:
                return 0;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            case 90:
                return 90;
            case 180:
                return 180;
            case 270:
                return 270;
            case -90:
                return 270;
        }

        return 0;
    }
    */

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        if (orientation == 0) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(orientation);

        try {
            Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return oriented;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /*
    public Bitmap rotateBitmap(Context context, Uri imageUri, Bitmap bitmap) {
        int orientation = getExifOrientation(context, imageUri);

        return rotateBitmap(bitmap, orientation);
    }

    */
    /**
     * Gets the real path from file
     */
    public static String getRealPathFromURI(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            return getPathForV19AndUp(context, uri);
        } else {
            return getPathForPreV19(context, uri);
        }
    }

    /**
     * Handles pre V19 uri's and non-document contracts (camera)
     */
    public static String getPathForPreV19(Context context, Uri uri) {

       String localPath = "";

       String[] filePathColumn = { MediaStore.Images.Media.DATA };
       Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
       if (cursor != null) {
           cursor.moveToFirst();
           int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
           if (cursor.getString(columnIndex) != null) localPath = cursor.getString(columnIndex);
           cursor.close();
        }

        return localPath;
    }

    /**
     * Handles V19 and up uri's
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathForV19AndUp(Context context, Uri uri) {

        String localPath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String selector = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                filePathColumn, selector, new String[]{id}, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (cursor.getString(columnIndex) != null) localPath = cursor.getString(columnIndex);
            cursor.close();
        }

        return localPath;
    }
    /*
    public String getRealPathFromURIold(Context context, Uri contentUri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // Will return "image:x*"
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, sel, new String[] { id }, null);
            } else {
                cursor = context.getContentResolver().query(contentUri,
                        projection, null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = null;
        try {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return path;
    }
*/
}
