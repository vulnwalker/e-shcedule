package com.eschedule.eclient.core;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.eschedule.eclient.adapter.SpinnerAdapter;
import com.eschedule.eclient.model.IndexValue;
import com.eschedule.eclient.model.Jadwal;

import org.chalup.microorm.annotations.Column;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Teguh on 17/07/2014.
 */
public class Util {

    public static Bitmap TEMP_BITMAP = null;
    public static final int TIME_DEFAULT = 10 * 1000;
    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public enum valType {
        BY_KEY,
        BY_VAL
    };

    public static void clrError(EditText editText) {
        editText.setError(null);
    }

    public static void toast(Context context, String pesan) {
        toast(context, pesan, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String pesan, int time) {
        Toast.makeText(context, pesan, time).show();
    }

    public static void copyText(Activity activity, String sumber, String pesan) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", sumber);
        clipboard.setPrimaryClip(clip);

        Vibrator x = (Vibrator) activity.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        x.vibrate(500);

        Util.toast(activity, pesan);
    }

    public static ProgressDialog progressDialog(Activity activity, String text, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(text);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

    public static ProgressDialog progressDialog(Activity activity, String text) {
        return progressDialog(activity, text, false);
    }

    public static String getFileName(String filename) {
        int slash = filename.lastIndexOf("/");
        return filename.substring(slash + 1, filename.length());
    }

    public static String getFileNameFromExt(String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }

    public static String getFileExt(String filename) {
        int dot = filename.lastIndexOf(".");
        return filename.substring(dot + 1, filename.length()).toLowerCase();
    }

    public static String val(EditText e) {
        return e.getText().toString();
    }

    public static String val(TextView e) {
        return e.getText().toString();
    }

    public static String val(Spinner s) {
        ArrayList<IndexValue> arr = ((SpinnerAdapter) s.getAdapter()).mItems;
        return arr.get(s.getSelectedItemPosition()).getValue();
    }

    public static String key(Spinner s) {
        ArrayList<IndexValue> arr = ((SpinnerAdapter) s.getAdapter()).mItems;
        return arr.get(s.getSelectedItemPosition()).getKey();
    }

    public static String getTimeStamp(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        String today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;
        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("HH:mm") : new SimpleDateFormat("dd LLL, HH:mm");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Baru saja";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Satu menit yang lalu";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " menit yang lalu";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Satu jam yang lalu";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " jam yang lalu";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Kemarin";
        } else {
            return diff / DAY_MILLIS + " hari yang lalu";
        }
    }

    public static String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String getCurrentDate() {
        return getCurrentDate("dd-mm-yyy");
    }

    public static String getSQL(String table, Class<?> cls) {
        String builder = "";
        builder += "CREATE TABLE "+table+" (";

        for(Field field : cls.getDeclaredFields()) {
            if(field.isAnnotationPresent(ColumnType.class) && field.isAnnotationPresent(org.chalup.microorm.annotations.Column.class)) {
                Column a = field.getAnnotation(org.chalup.microorm.annotations.Column.class);
                ColumnType b = field.getAnnotation(ColumnType.class);

                builder += a.value()+" "+b.type()+" "+b.property()+", ";
            }
        }

        builder = builder.substring(0, builder.length() - 2);
        builder += ")";

        return builder;
    }

    public static String getVersion(Activity activity) {
        String versi = "";
        try {
            versi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versi;
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognise a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }

        return degree;
    }

    public static int getSpinValueBySearch(valType type, Spinner spinner, String s) {
        int index = 0;

        SpinnerAdapter adapter = ((SpinnerAdapter) spinner.getAdapter());
        for(int i=0; i < adapter.getCount(); i++) {
            if(type == valType.BY_VAL) {
                if(s.trim().equals(adapter.getItemModel(i).getValue())){
                    index = i;
                }
            } else if (type == valType.BY_KEY) {
                if(s.trim().equals(adapter.getItemModel(i).getKey())){
                    index = i;
                }
            }
        }
        return index;
    }

    public static int pixel2DP(Activity activity, int value) {
        float scale = activity.getResources().getDisplayMetrics().density;
        return (int)(value * scale + 0.5f);
    }

    public static Bitmap createBitmap(ImageView imageview){
        Bitmap bitmap = null;
        imageview.setDrawingCacheEnabled(true);
        imageview.buildDrawingCache(true);
        if (bitmap != null){
            bitmap.recycle();
            bitmap = null;
        }
        bitmap = Bitmap.createBitmap(imageview.getDrawingCache());
        imageview.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public static boolean hasEmpty(EditText editText, String Pesan) {
        String text = editText.getText().toString().trim();
        editText.setError(null);
        if(text.length() == 0) {
            editText.setError(Pesan);
            return false;
        }
        return true;
    }

    public static boolean hasEmpty(EditText editText) {
        return hasEmpty(editText, "Tidak Boleh Kosong");
    }

    public static boolean hasEmpty(Spinner spin) {
        if(Util.key(spin).equals("")) {
            ((SpinnerAdapter)spin.getAdapter()).setError(spin.getSelectedView(), "Tidak Boleh Kosong");
            return false;
        }
        return true;
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static byte[] createByteFromImageView(ImageView result) {
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        Bitmap bitmapTmp = Util.createBitmap(result);
        bitmapTmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] createByteFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 20, stream);
        return stream.toByteArray();
    }

    public static boolean cekFileExists(String file) {
        return new File(file).exists();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
