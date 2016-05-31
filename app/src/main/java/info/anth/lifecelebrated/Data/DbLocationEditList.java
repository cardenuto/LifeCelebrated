package info.anth.lifecelebrated.Data;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Helpers.Installation;

/**
 * Created by Primary on 4/7/2016.
 *
 * Firebase Location Master data structure
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbLocationEditList {
    private String editKey;
    private String name;
    private String primaryImage;
    private Boolean imageUploaded;
    private String localImagePath;
    private String deviceID;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationEditList() {
    }

    public DbLocationEditList(String editKey, String name, String primaryImage, Boolean imageUploaded, String localImagePath, String deviceID) {
        this.editKey = editKey;
        this.name = name;
        this.primaryImage = primaryImage;
        this.imageUploaded = imageUploaded;
        this.localImagePath = localImagePath;
        this.deviceID = deviceID;
    }

    public String getEditKey() { return editKey; }
    public String getName() { return name; }
    public String getPrimaryImage() { return primaryImage; }
    public Boolean getImageUploaded() { return imageUploaded; }
    public String getLocalImagePath() { return localImagePath; }
    public String getDeviceID() { return deviceID; }

    public static class columns {

        //define columns
        public static String COLUMN_EDIT_KEY = "editKey";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_PRIMARY_IMAGE = "primaryImage";
        public static String COLUMN_IMAGE_UPLOADED = "imageUploaded";
        public static String COLUMN_LOCAL_IMAGE_PATH = "localImagePath";
        public static String COLUMN_DEVICE_ID = "deviceID";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationEditList dbLocationEditList) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_EDIT_KEY, dbLocationEditList.getEditKey());
            fullMap.put(COLUMN_NAME, dbLocationEditList.getName());
            fullMap.put(COLUMN_PRIMARY_IMAGE, dbLocationEditList.getPrimaryImage());
            fullMap.put(COLUMN_IMAGE_UPLOADED, dbLocationEditList.getImageUploaded());
            fullMap.put(COLUMN_LOCAL_IMAGE_PATH, dbLocationEditList.getLocalImagePath());
            fullMap.put(COLUMN_DEVICE_ID, dbLocationEditList.getDeviceID());

            return fullMap;
        }

        public static DbLocationEditList createBlank(String editKey, Context context) {
            String device = Installation.id(context);
            return new DbLocationEditList(editKey,"","",false,"",device);
        }
    }
}
