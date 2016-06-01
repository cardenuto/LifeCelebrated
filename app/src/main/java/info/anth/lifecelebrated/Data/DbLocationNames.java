package info.anth.lifecelebrated.Data;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Primary on 4/7/2016.
 *
 * Firebase Location Map data structure
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbLocationNames {
    private String familyName;
    private String firstName;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationNames() {
    }

    public DbLocationNames(String familyName, String firstName) {
        this.familyName = familyName;
        this.firstName = firstName;
    }

    public String getFamilyName() { return familyName; }
    public String getFirstName() { return firstName; }

    public static class columns {

        //define columns
        public static String COLUMN_FAMILY_NAME = "familyName";
        public static String COLUMN_FIRST_NAME = "firstName";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationNames dbLocationNames) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_FAMILY_NAME, dbLocationNames.getFamilyName());
            fullMap.put(COLUMN_FIRST_NAME, dbLocationNames.getFirstName());

            return fullMap;
        }

        @SuppressWarnings("unused")
        public static DbLocationNames createBlank() {
            return new DbLocationNames("","");
        }
    }
}
