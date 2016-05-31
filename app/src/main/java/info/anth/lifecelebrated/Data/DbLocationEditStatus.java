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
    private String category;
    private String tags;
    private String description;
    private String images;
    private String location;
    private String publish;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationEditStatus() {
    }

    public DbLocationEditStatus(String picture, String name, String category, String tags,
                                String description, String images, String location, String publish) {
        this.picture = picture;
        this.name = name;
        this.category = category;
        this.tags = tags;
        this.description = description;
        this.images = images;
        this.location = location;
        this.publish = publish;
    }

    public String getPicture() { return picture; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getTags() { return tags; }
    public String getDescription() { return description; }
    public String getImages() { return images; }
    public String getLocation() { return location; }
    public String getPublish() { return publish; }

    public static class columns {

        //define columns
        public static String COLUMN_PICTURE = "picture";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_CATEGORY = "category";
        public static String COLUMN_TAGS = "tags";
        public static String COLUMN_DESCRIPTION = "description";
        public static String COLUMN_IMAGES = "images";
        public static String COLUMN_LOCATION = "location";
        public static String COLUMN_PUBLISH = "publish";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationEditStatus dbLocationEditStatus) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_PICTURE, dbLocationEditStatus.getPicture());
            fullMap.put(COLUMN_NAME, dbLocationEditStatus.getName());
            fullMap.put(COLUMN_CATEGORY, dbLocationEditStatus.getCategory());
            fullMap.put(COLUMN_TAGS, dbLocationEditStatus.getTags());
            fullMap.put(COLUMN_DESCRIPTION, dbLocationEditStatus.getDescription());
            fullMap.put(COLUMN_IMAGES, dbLocationEditStatus.getImages());
            fullMap.put(COLUMN_LOCATION, dbLocationEditStatus.getLocation());
            fullMap.put(COLUMN_PUBLISH, dbLocationEditStatus.getPublish());

            return fullMap;
        }

        @SuppressWarnings("unused")
        public static DbLocationEditStatus createBlank() {
            return new DbLocationEditStatus("","","","","","","","");
        }

        @SuppressWarnings("unused")
        public static String getColumnValue(String column, DbLocationEditStatus dbLocationEditStatus){
            String columnValue = "ic_step_default_24dp";

            if (column.equals(COLUMN_PICTURE)) columnValue = dbLocationEditStatus.getPicture();
            if (column.equals(COLUMN_NAME)) columnValue = dbLocationEditStatus.getName();
            if (column.equals(COLUMN_CATEGORY)) columnValue = dbLocationEditStatus.getCategory();
            if (column.equals(COLUMN_TAGS)) columnValue = dbLocationEditStatus.getTags();
            if (column.equals(COLUMN_DESCRIPTION)) columnValue = dbLocationEditStatus.getDescription();
            if (column.equals(COLUMN_IMAGES)) columnValue = dbLocationEditStatus.getImages();
            if (column.equals(COLUMN_LOCATION)) columnValue = dbLocationEditStatus.getLocation();
            if (column.equals(COLUMN_PUBLISH)) columnValue = dbLocationEditStatus.getPublish();

            return columnValue;
        }

        @SuppressWarnings("unused")
        public static DbLocationEditStatus createDefaults(Activity activity) {
            // string.xml needs to be in the same order as columns
            return new DbLocationEditStatus(
                    activity.getResources().getStringArray(R.array.step_tab_icon)[1],
                    activity.getResources().getStringArray(R.array.step_tab_icon)[2],
                    activity.getResources().getStringArray(R.array.step_tab_icon)[3],
                    activity.getResources().getStringArray(R.array.step_tab_icon)[4],
                    activity.getResources().getStringArray(R.array.step_tab_icon)[5],
                    activity.getResources().getStringArray(R.array.step_tab_icon)[6],
                    activity.getResources().getStringArray(R.array.step_tab_icon)[7],
                    activity.getResources().getStringArray(R.array.step_tab_icon)[8]);
        }
    }
}

