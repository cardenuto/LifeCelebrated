package info.anth.lifecelebrated.Data;

import android.app.Activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 5/16/2016.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbLocationEditStatus {
    private String picture;
    private String name;
    private String images;
    private String location;
    private String publish;
    private String pictureMsg;
    private String nameMsg;
    private String imagesMsg;
    private String locationMsg;
    private String publishMsg;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationEditStatus() {
    }

    public DbLocationEditStatus(String picture, String name, String images, String location, String publish,
                                String pictureMsg, String nameMsg, String imagesMsg, String locationMsg, String publishMsg) {
        this.picture = picture;
        this.name = name;
        this.images = images;
        this.location = location;
        this.publish = publish;
        this.pictureMsg = pictureMsg;
        this.nameMsg = nameMsg;
        this.imagesMsg = imagesMsg;
        this.locationMsg = locationMsg;
        this.publishMsg = publishMsg;
    }

    public String getPicture() { return picture; }
    public String getName() { return name; }
    public String getImages() { return images; }
    public String getLocation() { return location; }
    public String getPublish() { return publish; }
    public String getPictureMsg() { return pictureMsg; }
    public String getNameMsg() { return nameMsg; }
    public String getImagesMsg() { return imagesMsg; }
    public String getLocationMsg() { return locationMsg; }
    public String getPublishMsg() { return publishMsg; }

    public static class columns {

        //define columns
        public static String COLUMN_PICTURE = "picture";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_IMAGES = "images";
        public static String COLUMN_LOCATION = "location";
        public static String COLUMN_PUBLISH = "publish";
        public static String COLUMN_PICTURE_MSG = "pictureMsg";
        public static String COLUMN_NAME_MSG = "nameMsg";
        public static String COLUMN_IMAGES_MSG = "imagesMsg";
        public static String COLUMN_LOCATION_MSG = "locationMsg";
        public static String COLUMN_PUBLISH_MSG = "publishMsg";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationEditStatus dbLocationEditStatus) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_PICTURE, dbLocationEditStatus.getPicture());
            fullMap.put(COLUMN_NAME, dbLocationEditStatus.getName());
            fullMap.put(COLUMN_IMAGES, dbLocationEditStatus.getImages());
            fullMap.put(COLUMN_LOCATION, dbLocationEditStatus.getLocation());
            fullMap.put(COLUMN_PUBLISH, dbLocationEditStatus.getPublish());
            fullMap.put(COLUMN_PICTURE_MSG, dbLocationEditStatus.getPictureMsg());
            fullMap.put(COLUMN_NAME_MSG, dbLocationEditStatus.getNameMsg());
            fullMap.put(COLUMN_IMAGES_MSG, dbLocationEditStatus.getImagesMsg());
            fullMap.put(COLUMN_LOCATION_MSG, dbLocationEditStatus.getLocationMsg());
            fullMap.put(COLUMN_PUBLISH_MSG, dbLocationEditStatus.getPublishMsg());

            return fullMap;
        }

        @SuppressWarnings("unused")
        public static DbLocationEditStatus createBlank() {
            return new DbLocationEditStatus("","","","","","","","","","");
        }

        @SuppressWarnings("unused")
        public static String getColumnValue(String column, DbLocationEditStatus dbLocationEditStatus){
            String columnValue = "ic_step_default_24dp";

            if (column.equals(COLUMN_PICTURE)) columnValue = dbLocationEditStatus.getPicture();
            if (column.equals(COLUMN_NAME)) columnValue = dbLocationEditStatus.getName();
            if (column.equals(COLUMN_IMAGES)) columnValue = dbLocationEditStatus.getImages();
            if (column.equals(COLUMN_LOCATION)) columnValue = dbLocationEditStatus.getLocation();
            if (column.equals(COLUMN_PUBLISH)) columnValue = dbLocationEditStatus.getPublish();

            if (column.equals(COLUMN_PICTURE_MSG)) columnValue = dbLocationEditStatus.getPictureMsg();
            if (column.equals(COLUMN_NAME_MSG)) columnValue = dbLocationEditStatus.getNameMsg();
            if (column.equals(COLUMN_IMAGES_MSG)) columnValue = dbLocationEditStatus.getImagesMsg();
            if (column.equals(COLUMN_LOCATION_MSG)) columnValue = dbLocationEditStatus.getLocationMsg();
            if (column.equals(COLUMN_PUBLISH_MSG)) columnValue = dbLocationEditStatus.getPublishMsg();

            return columnValue;
        }

        @SuppressWarnings("unused")
        public static DbLocationEditStatus createDefaults(Activity activity) {
            // string.xml needs to be in the same order as columns
            return new DbLocationEditStatus(
                    activity.getResources().getString(R.string.picture_ic_default),
                    activity.getResources().getString(R.string.name_ic_default),
                    activity.getResources().getString(R.string.image_ic_default),
                    activity.getResources().getString(R.string.location_ic_default),
                    activity.getResources().getString(R.string.publish_ic_default),
                    activity.getResources().getString(R.string.picture_msg_default),
                    activity.getResources().getString(R.string.name_msg_default),
                    activity.getResources().getString(R.string.image_msg_default),
                    activity.getResources().getString(R.string.location_msg_default),
                    activity.getResources().getString(R.string.publish_msg_default));
        }
    }
}

