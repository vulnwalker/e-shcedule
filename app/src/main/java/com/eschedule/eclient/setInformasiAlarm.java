package com.eschedule.eclient;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.model.Data;
import com.eschedule.eclient.model.Informasi;
import com.eschedule.eclient.model.Jadwal;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;


/**
 * Created by Kang Juy on 11/3/2016.
 */

public class setInformasiAlarm extends Service {
    DB dbHelper;
    private App app2;
    Context context;
    int connection_status = 1;
    Timer timer = new Timer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
        timer.schedule(new DelayedTask(), 0, 120000);
    }

    private class DelayedTask extends TimerTask {

        @Override
        public void run() {
            Log.v(TAG,"alarm set");

            File dbExist = getApplicationContext().getDatabasePath(DB.DB_NAME);
            if(dbExist.exists()) {
                pagi1();
                pagi2();
                siang1();
                siang2();
                sore1();
                sore2();
                malam1();
                malam2();
            }

        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void pagi1(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,8);
        skr.set(Calendar.MINUTE,0);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","pagi1");
        PendingIntent PI = PendingIntent.getBroadcast(this, 1 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }

    private void pagi2(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,8);
        skr.set(Calendar.MINUTE,30);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","pagi2");
        PendingIntent PI = PendingIntent.getBroadcast(this, 2 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }
    private void siang1(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,12);
        skr.set(Calendar.MINUTE,00);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","siang1");
        PendingIntent PI = PendingIntent.getBroadcast(this, 3 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }
    private void siang2(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,12);
        skr.set(Calendar.MINUTE,30);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","siang2");
        PendingIntent PI = PendingIntent.getBroadcast(this, 4 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }
    private void sore1(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,15);
        skr.set(Calendar.MINUTE,0);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","sore1");
        PendingIntent PI = PendingIntent.getBroadcast(this, 5 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }
    private void sore2(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,15);
        skr.set(Calendar.MINUTE,30);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","sore2");
        PendingIntent PI = PendingIntent.getBroadcast(this, 6 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }
    private void malam1(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,19);
        skr.set(Calendar.MINUTE,0);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","malam1");
        PendingIntent PI = PendingIntent.getBroadcast(this, 7 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }
    private void malam2(){
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.HOUR_OF_DAY,19);
        skr.set(Calendar.MINUTE,30);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this, AlarmInformasi.class);
        intent1.putExtra("alarm","malam2");
        PendingIntent PI = PendingIntent.getBroadcast(this, 8 ,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
    }
}
