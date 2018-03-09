package tw.yalan.cafeoffice.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import tw.yalan.cafeoffice.Config;

/**
 * Created by Alan Ding on 2016/3/2.
 */
public class MapUtils {
    public static SupportMapFragment getMapFragment(FragmentActivity activity, int mapResId) {
        SupportMapFragment fragment = (SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(mapResId);
        return fragment;
    }

    /**
     * 產生GoogleApiClient
     *
     * @param context
     * @param connectionCallbacks
     * @param connectionFailedListener
     * @return
     */
    public static synchronized GoogleApiClient buildGoogleApiClient(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks
            , GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * GPS是否啟用
     *
     * @param context
     * @return
     */
    public static Boolean checkGPSEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 計算兩點距離
     *
     * @param latLng1
     * @param latLng2
     * @return result[0]:公尺/result[1]:公里
     */
    public static String[] getDistance(LatLng latLng1, LatLng latLng2) {
        float[] results = new float[1];
        Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results);
        String[] stirngResult = new String[2];
        stirngResult[0] = String.valueOf((int) (results[0] * 1.609344));
        BigDecimal m = new BigDecimal((results[0] * 1.609344));
        stirngResult[1] = String.valueOf(m.divide(new BigDecimal(1000f), 2, BigDecimal.ROUND_HALF_UP));
        return stirngResult;
    }

    /**
     * 經緯度轉換地址
     *
     * @param context
     * @param lat
     * @param lon
     * @return
     */
    public static Address convertToAddress(Context context, Double lat, Double lon) {
        Geocoder geocoder = new Geocoder(context, Locale.TRADITIONAL_CHINESE);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            Log.e(Config.TAG, null, e);
        }
        if (addresses != null && !addresses.isEmpty()) {
            return addresses.get(0);
        }
        return null;
    }

    public static LatLng parseToLatLng(@NonNull Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    // 功能：判断点是否在多边形内
    // 方法：求解通过该点的水平线与多边形各边的交点
    // 结论：单边交点为奇数，成立!
    //参数：
    // POINT p   指定的某个点
    // LPPOINT ptPolygon 多边形的各个顶点坐标（首末点可以不一致）
    public static boolean ptInPolygon(LatLng point, List<LatLng> APoints) {
        int nCross = 0;
        for (int i = 0; i < APoints.size(); i++)   {
            LatLng p1 = APoints.get(i);
            LatLng p2 = APoints.get((i + 1) % APoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if ( p1.longitude == p2.longitude)      // p1p2 与 y=p0.y平行
                continue;
            if ( point.longitude <  Math.min(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            if ( point.longitude >= Math.max(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标 --------------------------------------------------------------
            double x = (double)(point.longitude - p1.longitude) * (double)(p2.latitude - p1.latitude) / (double)(p2.longitude - p1.longitude) + p1.latitude;
            if ( x > point.latitude )
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }
}
