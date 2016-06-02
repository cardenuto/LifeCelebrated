package info.anth.lifecelebrated.Data;

import android.app.Activity;
import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.Helpers.Installation;
import info.anth.lifecelebrated.R;

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
    private String pictureMsg;
    private String nameMsg;
    private String imagesMsg;
    private String locationMsg;
    private String publishMsg;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationEditList() {
    }

    public DbLocationEditList(String editKey, String name, String primaryImage, Boolean imageUploaded, String localImagePath, String deviceID,
                              String pictureMsg, String nameMsg, String imagesMsg, String locationMsg, String publishMsg) {
        this.editKey = editKey;
        this.name = name;
        this.primaryImage = primaryImage;
        this.imageUploaded = imageUploaded;
        this.localImagePath = localImagePath;
        this.deviceID = deviceID;
        this.pictureMsg = pictureMsg;
        this.nameMsg = nameMsg;
        this.imagesMsg = imagesMsg;
        this.locationMsg = locationMsg;
        this.publishMsg = publishMsg;
    }

    public String getEditKey() { return editKey; }
    public String getName() { return name; }
    public String getPrimaryImage() { return primaryImage; }
    public Boolean getImageUploaded() { return imageUploaded; }
    public String getLocalImagePath() { return localImagePath; }
    public String getDeviceID() { return deviceID; }
    public String getPictureMsg() { return pictureMsg; }
    public String getNameMsg() { return nameMsg; }
    public String getImagesMsg() { return imagesMsg; }
    public String getLocationMsg() { return locationMsg; }
    public String getPublishMsg() { return publishMsg; }

    public static class columns {

        //define columns
        public static String COLUMN_EDIT_KEY = "editKey";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_PRIMARY_IMAGE = "primaryImage";
        public static String COLUMN_IMAGE_UPLOADED = "imageUploaded";
        public static String COLUMN_LOCAL_IMAGE_PATH = "localImagePath";
        public static String COLUMN_DEVICE_ID = "deviceID";
        public static String COLUMN_PICTURE_MSG = "pictureMsg";
        public static String COLUMN_NAME_MSG = "nameMsg";
        public static String COLUMN_IMAGES_MSG = "imagesMsg";
        public static String COLUMN_LOCATION_MSG = "locationMsg";
        public static String COLUMN_PUBLISH_MSG = "publishMsg";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationEditList dbLocationEditList) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_EDIT_KEY, dbLocationEditList.getEditKey());
            fullMap.put(COLUMN_NAME, dbLocationEditList.getName());
            fullMap.put(COLUMN_PRIMARY_IMAGE, dbLocationEditList.getPrimaryImage());
            fullMap.put(COLUMN_IMAGE_UPLOADED, dbLocationEditList.getImageUploaded());
            fullMap.put(COLUMN_LOCAL_IMAGE_PATH, dbLocationEditList.getLocalImagePath());
            fullMap.put(COLUMN_DEVICE_ID, dbLocationEditList.getDeviceID());
            fullMap.put(COLUMN_PICTURE_MSG, dbLocationEditList.getPictureMsg());
            fullMap.put(COLUMN_NAME_MSG, dbLocationEditList.getNameMsg());
            fullMap.put(COLUMN_IMAGES_MSG, dbLocationEditList.getImagesMsg());
            fullMap.put(COLUMN_LOCATION_MSG, dbLocationEditList.getLocationMsg());
            fullMap.put(COLUMN_PUBLISH_MSG, dbLocationEditList.getPublishMsg());

            return fullMap;
        }

        public static DbLocationEditList createBlank(String editKey, Context context) {
            String device = Installation.id(context);
            return new DbLocationEditList(editKey,"","",false,"",device,
                    context.getResources().getString(R.string.picture_msg_default),
                    context.getResources().getString(R.string.name_msg_default),
                    context.getResources().getString(R.string.image_msg_default),
                    context.getResources().getString(R.string.location_msg_default),
                    context.getResources().getString(R.string.publish_msg_default));
        }
    }
}
