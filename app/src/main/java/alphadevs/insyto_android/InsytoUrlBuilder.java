package alphadevs.insyto_android;

import android.net.Uri;

/**
 * Use it to create required URLs for insyto.
 */
public class InsytoUrlBuilder {
    private final static String BASE_URL = "http://159.203.0.244:3000"; // TODO change per config
    private final static String API_V1 = "v1";
    private final static String INSYTES = "insytes";
    private final static String API_KEYS = "api_keys";
    private final static String PAGE_PARAM = "page";
    private final static String NEWER_PARAM = "newer";
    private final static String LNG_PARAM = "lng";
    private final static String LAT_PARAM = "lat";
    private final static String RADIUS_PARAM = "radius";

    public static String getInsytesUrl(int pageNb)
    {
        return getInsytesBuilder()
                .appendQueryParameter(PAGE_PARAM, Integer.toString(pageNb))
                .build().toString();
    }

    public static String getAmazonApiKeysUrl()
    {
        return getV1Builder().appendEncodedPath(API_KEYS).appendEncodedPath("amazon").build().toString();
    }

    public static String getInsytesUrlGPS(int pageNb, double longitude, double latitude, double radius)
    {
        return getInsytesBuilder()
                .appendQueryParameter(PAGE_PARAM, Integer.toString(pageNb))
                .appendQueryParameter(LNG_PARAM, Double.toString(longitude))
                .appendQueryParameter(LAT_PARAM, Double.toString(latitude))
                .appendQueryParameter(RADIUS_PARAM, Double.toString(radius))
                .build().toString();
    }

    public static String getNewerInsytesUrl(long seconds)
    {
        return getInsytesBuilder()
                .appendQueryParameter(NEWER_PARAM, Long.toString(seconds))
                .build().toString();
    }

    public static String getNewerInsytesUrlGPS(long seconds, double longitude, double latitude, double radius)
    {
        return getInsytesBuilder()
                .appendQueryParameter(NEWER_PARAM, Long.toString(seconds))
                .appendQueryParameter(LNG_PARAM, Double.toString(longitude))
                .appendQueryParameter(LAT_PARAM, Double.toString(latitude))
                .appendQueryParameter(RADIUS_PARAM, Double.toString(radius))
                .build().toString();
    }

    public static String getInsytesUrl()
    {
        return getInsytesBuilder().build().toString();
    }

    public static String getInsyteUrl(Integer id)
    {
        return getInsytesBuilder().appendEncodedPath(Integer.toString(id)).build().toString();
    }

    private static Uri.Builder getV1Builder()
    {
        return new Uri.Builder().encodedPath(BASE_URL).appendEncodedPath(API_V1);
    }

    private static Uri.Builder getInsytesBuilder()
    {
        return getV1Builder().appendEncodedPath(INSYTES);
    }
}
