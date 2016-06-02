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
public class DbLocationStatusIC {
    private String picture;
    private String name;
    private String images;
    private String location;
    private String publish;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationStatusIC() {
    }

    public DbLocationStatusIC(String picture, String name, String images, String location, String publish) {
        this.picture = picture;
        this.name = name;
        this.images = images;
        this.location = location;
        this.publish = publish;
    }

    public String getPicture() { return picture; }
    public String getName() { return name; }
    public String getImages() { return images; }
    public String getLocation() { return location; }
    public String getPublish() { return publish; }

    public static class columns {

        //define columns
        public static String COLUMN_PICTURE = "picture";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_IMAGES = "images";
        public static String COLUMN_LOCATION = "location";
        public static String COLUMN_PUBLISH = "publish";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationStatusIC dbLocationEditStatus) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_PICTURE, dbLocationEditStatus.getPicture());
            fullMap.put(COLUMN_NAME, dbLocationEditStatus.getName());
            fullMap.put(COLUMN_IMAGES, dbLocationEditStatus.getImages());
            fullMap.put(COLUMN_LOCATION, dbLocationEditStatus.getLocation());
            fullMap.put(COLUMN_PUBLISH, dbLocationEditStatus.getPublish());

            return fullMap;
        }

        @SuppressWarnings("unused")
        public static DbLocationStatusIC createBlank() {
            return new DbLocationStatusIC("","","","","");
        }

        @SuppressWarnings("unused")
        public static String getColumnValue(String column, DbLocationStatusIC dbLocationEditStatus){
            String columnValue = "ic_step_default_24dp";

            if (column.equals(COLUMN_PICTURE)) columnValue = dbLocationEditStatus.getPicture();
            if (column.equals(COLUMN_NAME)) columnValue = dbLocationEditStatus.getName();
            if (column.equals(COLUMN_IMAGES)) columnValue = dbLocationEditStatus.getImages();
            if (column.equals(COLUMN_LOCATION)) columnValue = dbLocationEditStatus.getLocation();
            if (column.equals(COLUMN_PUBLISH)) columnValue = dbLocationEditStatus.getPublish();

            return columnValue;
        }

        @SuppressWarnings("unused")
        public static DbLocationStatusIC createDefaults(Activity activity) {
            // string.xml needs to be in the same order as columns
            return new DbLocationStatusIC(
                    activity.getResources().getString(R.string.picture_ic_default),
                    activity.getResources().getString(R.string.name_ic_default),
                    activity.getResources().getString(R.string.image_ic_default),
                    activity.getResources().getString(R.string.location_ic_default),
                    activity.getResources().getString(R.string.publish_ic_default));
        }
    }
}

