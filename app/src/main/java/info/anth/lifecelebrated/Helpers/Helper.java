package info.anth.lifecelebrated.Helpers;

import android.content.Context;

/**
 * Created by Primary on 4/12/2016.
 *
 * Set of generic helper utilities
 */
public class Helper {
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
}
