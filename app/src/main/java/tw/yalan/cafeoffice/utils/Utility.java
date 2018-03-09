package tw.yalan.cafeoffice.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by Alan Ding on 2016/5/26.
 */
public class Utility {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT_WITHOUT_YEAR = new SimpleDateFormat("MM/dd");
    public static final SimpleDateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("h:mma", Locale.US);
    public static final SimpleDateFormat DEFAULT_TIME_FORMAT_WITH_SPACE = new SimpleDateFormat("h:mm a", Locale.US);

    public static final DecimalFormat doubleScoreFormat = new DecimalFormat("0.0");

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getString(Context context, String resName) {
        return getString(context, resName, "");
    }

    public static String getString(Context context, String resName, String defaultString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(resName, "string", packageName);
        if (resId != 0) {
            return context.getString(resId);
        } else {
            return defaultString;
        }
    }

    public static int getDrawableByName(Context context, String resName) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(resName, "drawable", packageName);
        return resId;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void addOnGlobalLayoutListener(final View view, final Runnable runnable) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                runnable.run();
            }
        });
    }

    /**
     * DP 轉 PX
     *
     * @param context
     * @param dp
     * @return
     */
    public static float dpToPixel(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            editText.setFocusableInTouchMode(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(true);
            editText.setFocusable(true);
        }
    }

    public static void hideKeyboardWithoutClearFocus(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    /**
     * 判斷是否處於有網路狀態
     *
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable();
    }

    public static float getBatteryLevel(Intent batteryIntent) {
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    public static boolean isNotEmptyOrNull(Object object) {
        return object != null
                && (object instanceof String ? !((String) object).isEmpty() : true)
                && (object instanceof List ? !((List) object).isEmpty() : true);
    }

    public static void startToCallPhone(Activity activity, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        activity.startActivity(intent);
    }

    public static boolean isTextEllipsized(TextView textView) {
        Layout l = textView.getLayout();
        if (l != null) {
            int lines = l.getLineCount();
            if (lines > 0)
                if (l.getEllipsisCount(lines - 1) > 0)
                    return true;
        }
        return false;
    }

    public static String formatToMacAddress(String input) {
        char divisionChar = ':';//change to '-' if you want your mac to be like 00-15-5D-03-8D-01
        String formattedMAC = input.replaceAll("(.{2})", "$1" + divisionChar).substring(0, input.length() < 12 ? (input.length() - 1) : 17);
        return formattedMAC.toUpperCase();
    }

    private static ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    public static void showDatePickerDialog(Context context, final TextView textView) {
        Calendar c = Calendar.getInstance();
        if (textView.getText() != null && textView.getText().length() > 0) {
            try {
                c.setTime(DEFAULT_DATE_FORMAT.parse(textView.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                textView.setText(year + "/" + (month + 1) + "/" + day);
            }
        }, year, month, day);
        dialog.getDatePicker().setSpinnersShown(true);
        dialog.getDatePicker().setCalendarViewShown(false);
        dialog.show();
    }

    public static SimpleDateFormat getDefaultDateFormatWithoutYear() {
        return DEFAULT_DATE_FORMAT_WITHOUT_YEAR;
    }

    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static String getKeyHash(Context context) {
        /**
         * get keyHash
         */
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", keyHash);
                return keyHash;
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return "";
    }

    public static Intent getFacebookIntent(Context context, String url) {

        PackageManager pm = context.getPackageManager();
        Uri uri = Uri.parse(url);

        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
