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
public class DbLocationMaster {
    private String name;
    private String description;
    private String primaryImage;
    private Boolean imageUploaded;
    private String localImagePath;
    private String deviceID;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationMaster() {
    }

    public DbLocationMaster(String name, String description, String primaryImage, Boolean imageUploaded, String localImagePath, String deviceID) {
        this.name = name;
        this.description = description;
        this.primaryImage = primaryImage;
        this.imageUploaded = imageUploaded;
        this.localImagePath = localImagePath;
        this.deviceID = deviceID;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrimaryImage() { return primaryImage; }
    public Boolean getImageUploaded() { return imageUploaded; }
    public String getLocalImagePath() { return localImagePath; }
    public String getDeviceID() { return deviceID; }

    public static class columns {

        //define columns
        public static String COLUMN_NAME = "name";
        public static String COLUMN_DESCRIPTION = "description";
        public static String COLUMN_PRIMARY_IMAGE = "primaryImage";
        public static String COLUMN_IMAGE_UPLOADED = "imageUploaded";
        public static String COLUMN_LOCAL_IMAGE_PATH = "localImagePath";
        public static String COLUMN_DEVICE_ID = "deviceID";


        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationMaster dbLocationMaster) {
            Map<String, Object> fullMap = new HashMap<String, Object>();

            fullMap.put(COLUMN_NAME, dbLocationMaster.getName());
            fullMap.put(COLUMN_DESCRIPTION, dbLocationMaster.getDescription());
            fullMap.put(COLUMN_PRIMARY_IMAGE, dbLocationMaster.getPrimaryImage());
            fullMap.put(COLUMN_IMAGE_UPLOADED, dbLocationMaster.getImageUploaded());
            fullMap.put(COLUMN_LOCAL_IMAGE_PATH, dbLocationMaster.getLocalImagePath());
            fullMap.put(COLUMN_DEVICE_ID, dbLocationMaster.getDeviceID());

            return fullMap;
        }

        @SuppressWarnings("unused")
        public static DbLocationMaster createBlank(Context context) {
            String device = Installation.id(context);
            return new DbLocationMaster("","","",false,"", device);
        }
    }
}
