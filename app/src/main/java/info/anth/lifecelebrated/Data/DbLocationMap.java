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
public class DbLocationMap {
    private String deviceModel;
    private String deviceOS;
    private String method;
    private String provider;
    private Double longitude;
    private Double latitude;
    private Double accuracy;
    private Double altitude;
    private Long secondsToGPS;
    private int progressGPS;
    private Boolean calcCancelled;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private DbLocationMap() {
    }

    public DbLocationMap(String deviceModel, String deviceOS, String method, String provider,
                            Double longitude, Double latitude, Double accuracy,
                            Double altitude, Long secondsToGPS, int progressGPS,
                            Boolean calcCancelled) {
        this.deviceModel = deviceModel;
        this.deviceOS = deviceOS;
        this.method = method;
        this.provider = provider;
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.secondsToGPS = secondsToGPS;
        this.progressGPS = progressGPS;
        this.calcCancelled = calcCancelled;
    }

    public String getDeviceModel() { return deviceModel; }
    public String getDeviceOS() { return deviceOS; }
    public String getMethod() { return method; }
    public String getProvider() { return provider; }
    public Double getLongitude() { return longitude; }
    public Double getLatitude() { return latitude; }
    public Double getAccuracy() { return accuracy; }
    public Double getAltitude() { return altitude; }
    public Long getSecondsToGPS() { return secondsToGPS; }
    public int getProgressGPS() { return progressGPS; }
    public Boolean getCalcCancelled() { return calcCancelled; }

    public static class columns {

        //define columns
        public static String COLUMN_DEVICEMODEL = "deviceModel";
        public static String COLUMN_DEVICEOS = "deviceOS";
        public static String COLUMN_METHOD = "method";
        public static String COLUMN_PROVIDER = "provider";
        public static String COLUMN_LONGITUDE = "longitude";
        public static String COLUMN_LATITUDE = "latitude";
        public static String COLUMN_ACCURACY = "accuracy";
        public static String COLUMN_ALTITUDE = "altitude";
        public static String COLUMN_SECONDSTOGPS = "secondsToGPS";
        public static String COLUMN_PROGRESSGPS = "progressGPS";
        public static String COLUMN_CALCCANCELLED = "calcCancelled";

        @SuppressWarnings("unused")
        public static Map<String, Object> getFullMap(DbLocationMap dbLocationMap) {
            Map<String, Object> fullMap = new HashMap<>();

            fullMap.put(COLUMN_DEVICEMODEL, dbLocationMap.getDeviceModel());
            fullMap.put(COLUMN_DEVICEOS, dbLocationMap.getDeviceOS());
            fullMap.put(COLUMN_METHOD, dbLocationMap.getMethod());
            fullMap.put(COLUMN_PROVIDER, dbLocationMap.getProvider());
            fullMap.put(COLUMN_LONGITUDE, dbLocationMap.getLongitude());
            fullMap.put(COLUMN_LATITUDE, dbLocationMap.getLatitude());
            fullMap.put(COLUMN_ACCURACY, dbLocationMap.getAccuracy());
            fullMap.put(COLUMN_ALTITUDE, dbLocationMap.getAltitude());
            fullMap.put(COLUMN_SECONDSTOGPS, dbLocationMap.getSecondsToGPS());
            fullMap.put(COLUMN_PROGRESSGPS, dbLocationMap.getProgressGPS());
            fullMap.put(COLUMN_CALCCANCELLED, dbLocationMap.getCalcCancelled());

            return fullMap;
        }

        @SuppressWarnings("unused")
        public static Uri geoUri(DbLocationMap dbLocationMap, String label) {
            String uriBegin = "geo:" + String.valueOf(dbLocationMap.getLatitude()) + "," + String.valueOf(dbLocationMap.getLongitude());
            String query = String.valueOf(dbLocationMap.getLatitude()) + "," + String.valueOf(dbLocationMap.getLongitude()) + "(" + label + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=23";
            return Uri.parse(uriString);
        }

        @SuppressWarnings("unused")
        public static DbLocationMap createBlank() {
            return new DbLocationMap("","","","",0.0,0.0,0.0,0.0,0L,0,false);
        }
    }
}
