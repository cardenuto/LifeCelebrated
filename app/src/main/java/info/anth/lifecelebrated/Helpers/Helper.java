package info.anth.lifecelebrated.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Primary on 4/12/2016.
 *
 * Set of generic helper utilities
 */
public class Helper {

    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1492;

    public static String resourceString(Context context, int identifier, int currentPage){
        String message;

        if (currentPage < 0) {
            try {
                message = context.getResources().getString(identifier);
            } catch (Exception e) {
                // return blank message
                message = "";
            }
        } else {
            try {
                message = context.getResources().getStringArray(identifier)[currentPage];
            } catch (Exception e) {
                // return blank message
                message = "";
            }
        }
        return message;
    }

    public static int stringToIntDefault0 (String string) {
        int tempInt = 0;

        try {
            tempInt = Integer.parseInt(string);
            return tempInt;
        } catch(NumberFormatException nfe) {
            return tempInt;
        }
    }

    public static boolean validDateYMD (int year, int month, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setLenient(false);
        calendar.set(year, month, day);

        try {
            Date testDate = calendar.getTime();
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public static void checkPermissionExternal(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
}
