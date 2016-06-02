package info.anth.lifecelebrated.Data;

import android.app.Activity;
import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 4/7/2016.
 *
 * Firebase Location Map data structure
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbLocationNames {
    private String familyName;
    private String firstName;
    private String birthFormat;
    private int birthYear;
    private int birthMonth;
    private int birthDay;
    private String deathFormat;
    private int deathYear;
    private int deathMonth;
    private int deathDay;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationNames() {
    }

    public DbLocationNames(String familyName, String firstName,
                           String birthFormat, int birthYear, int birthMonth, int birthDay,
                           String deathFormat, int deathYear, int deathMonth, int deathDay) {
        this.familyName = familyName;
        this.firstName = firstName;
        this.birthFormat =birthFormat;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.deathFormat =deathFormat;
        this.deathYear = deathYear;
        this.deathMonth = deathMonth;
        this.deathDay = deathDay;
    }

    public String getFamilyName() { return familyName; }
    public String getFirstName() { return firstName; }
    public String getBirthFormat() { return birthFormat; }
    public int getBirthYear() { return birthYear; }
    public int getBirthMonth() { return birthMonth; }
    public int getBirthDay() { return birthDay; }
    public String getDeathFormat() { return deathFormat; }
    public int getDeathYear() { return deathYear; }
    public int getDeathMonth() { return deathMonth; }
    public int getDeathDay() { return deathDay; }
    
    public static class columns {

        //define columns
        public static String COLUMN_FAMILY_NAME = "familyName";
        public static String COLUMN_FIRST_NAME = "firstName";
        public static String COLUMN_BIRTH_FORMAT = "birthFormat";
        public static String COLUMN_BIRTH_YEAR = "birthYear";
        public static String COLUMN_BIRTH_MONTH = "birthMonth";
        public static String COLUMN_BIRTH_DAY = "birthDay";
        public static String COLUMN_DEATH_FORMAT = "deathFormat";
        public static String COLUMN_DEATH_YEAR = "deathYear";
        public static String COLUMN_DEATH_MONTH = "deathMonth";
        public static String COLUMN_DEATH_DAY = "deathDay";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationNames dbLocationNames) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_FAMILY_NAME, dbLocationNames.getFamilyName());
            fullMap.put(COLUMN_FIRST_NAME, dbLocationNames.getFirstName());
            fullMap.put(COLUMN_BIRTH_FORMAT, dbLocationNames.getBirthFormat());
            fullMap.put(COLUMN_BIRTH_YEAR, dbLocationNames.getBirthYear());
            fullMap.put(COLUMN_BIRTH_MONTH, dbLocationNames.getBirthMonth());
            fullMap.put(COLUMN_BIRTH_DAY, dbLocationNames.getBirthDay());
            fullMap.put(COLUMN_DEATH_FORMAT, dbLocationNames.getDeathFormat());
            fullMap.put(COLUMN_DEATH_YEAR, dbLocationNames.getDeathYear());
            fullMap.put(COLUMN_DEATH_MONTH, dbLocationNames.getDeathMonth());
            fullMap.put(COLUMN_DEATH_DAY, dbLocationNames.getDeathDay());

            return fullMap;
        }

        @SuppressWarnings("unused")
        public static DbLocationNames createBlank() {
            return new DbLocationNames("","","YMD",1900,0,1,"YMD",1900,0,1);
        }
        
        @SuppressWarnings("unused")
        public static String nameToString(DbLocationNames dbLocationNames) {
            String tempStirng = dbLocationNames.getFamilyName();
            
            if (!dbLocationNames.getFirstName().isEmpty()) tempStirng += ", " + dbLocationNames.getFirstName();
            
            return tempStirng;
        }

        @SuppressWarnings("unused")
        public static String dateRangeToString(DbLocationNames dbLocationNames, Activity activity) {
            String tempBirth = "";
            String tempDeath = "";
            
            switch (dbLocationNames.getBirthFormat()) {
                case "YMD":
                    tempBirth = activity.getResources().getStringArray(R.array.month_array)[dbLocationNames.getBirthMonth()];
                    tempBirth += " " + String.valueOf(dbLocationNames.getBirthDay());
                    tempBirth += ", " + String.valueOf(dbLocationNames.getBirthYear());
                    break;
                case "YM":
                    tempBirth = activity.getResources().getStringArray(R.array.month_array)[dbLocationNames.getBirthMonth()];
                    tempBirth += ", " + String.valueOf(dbLocationNames.getBirthYear());
                    break;
                case "Y":
                    tempBirth = String.valueOf(dbLocationNames.getBirthYear());
                    break;
            }

            switch (dbLocationNames.getDeathFormat()) {
                case "YMD":
                    tempDeath = activity.getResources().getStringArray(R.array.month_array)[dbLocationNames.getDeathMonth()];
                    tempDeath += " " + String.valueOf(dbLocationNames.getDeathDay());
                    tempDeath += ", " + String.valueOf(dbLocationNames.getDeathYear());
                    break;
                case "YM":
                    tempDeath = activity.getResources().getStringArray(R.array.month_array)[dbLocationNames.getDeathMonth()];
                    tempDeath += ", " + String.valueOf(dbLocationNames.getDeathYear());
                    break;
                case "Y":
                    tempDeath = String.valueOf(dbLocationNames.getDeathYear());
                    break;
            }

            return tempBirth + " - " + tempDeath;
        }
    }
}
