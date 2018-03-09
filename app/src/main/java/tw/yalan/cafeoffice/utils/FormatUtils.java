package tw.yalan.cafeoffice.utils;

import android.net.Uri;

import java.text.DecimalFormat;

/**
 * Created by Alan Ding on 2017/3/30.
 */

public class FormatUtils {
    public static DecimalFormat DEFAULT_FORMATER = new DecimalFormat("0.0");

    public static Uri createPlacePhotoUri(String query, int width, int height) {
        return createPlacePhotoUri(query, width, height, 0);
    }

    public static Uri createPlacePhotoUri(String query, int size) {
        return createPlacePhotoUri(query, size, size, 0);
    }

    public static Uri createPlacePhotoUri(String query, int width, int height, int index) {
        return Uri.parse("place://" + query + "?width=" + width + "&height=" + height + "&index=" + index);
    }


    public static String formatCreateTime(Long time) {
        Long second = (System.currentTimeMillis() - time) / 1000;
        if (second < 60) {
            return second + "秒前";
        } else if (second < 60 * 60) {
            return (second / 60) + "分鐘前";
        } else if (second < 60 * 60 * 24) {
            return (second / (60 * 60)) + "小時前";
        } else if (second < 60 * 60 * 24 * 7) {
            return (second / (60 * 60 * 24)) + "天前";
        } else if (second < 60 * 60 * 24 * 30) {
            return (second / (60 * 60 * 24 * 7)) + "週前";
        } else if (second < 60 * 60 * 24 * 30 * 12) {
            return (second / (60 * 60 * 24 * 30)) + "月前";
        } else if (second > 60 * 60 * 24 * 30 * 12) {
            return (second / (60 * 60 * 24 * 30 * 12)) + "年前";
        }
        return "";
    }
}
