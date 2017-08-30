package com.eschedule.eclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.informasi.InformasiDetailActivity;
import com.eschedule.eclient.jadwal.JadwalDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kang Juy on 1/7/2017.
 */

public class AlarmInformasi extends BroadcastReceiver {
    DB dbHelper;
    private App app2;
    Uri qqq ;
    Cursor cursor;
    Context context;
    Calendar rightNow = Calendar.getInstance();
    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
    int minute = rightNow.get(Calendar.MINUTE);
    String sm = "";
    String qqs = "";
    @Override
    public void onReceive(Context context2, Intent intent) {
        context = context2;
        if (minute < 10 ) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        qqs = String.valueOf(hour) + sm  ;
        int tempe = 1;
                dbHelper = new DB(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                cursor = db.rawQuery("SELECT * FROM inner_info where tanggal_mulai <= '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-","") + "' AND tanggal_akhir >= '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-","") + "'", null);
            if (cursor.moveToFirst()) {
                do {
                    Log.v("SUUU " , String.valueOf(tempe) + "   " + qqs);
                    tempe = tempe + 1;
                    selectInfo(cursor.getString(cursor.getColumnIndex("informasi_id")));
                } while (cursor.moveToNext());
            }


    }

    private void selectInfo(String informasi_id){
        Cursor cursor2 ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor2 = db.rawQuery("SELECT * FROM mob_informasi where informasi_id = '" + informasi_id + "' ", null);
        if (cursor2.moveToFirst()) {
            do {
               if (cursor2.getString(cursor2.getColumnIndex("waktu_pagi")).equals("1")){
                   if (cursor2.getString(cursor2.getColumnIndex("durasi_pagi")).equals("1")){
                       if (qqs.equals("800")){
                           NotifyInfo(10000 + Integer.parseInt(informasi_id),"Pagi");
                       }
                   }
                   if (cursor2.getString(cursor2.getColumnIndex("durasi_pagi")).equals("2")){
                       if (qqs.equals("830")){
                           NotifyInfo(10000 + Integer.parseInt(informasi_id),"Pagi");
                       }
                   }
               }
                if (cursor2.getString(cursor2.getColumnIndex("waktu_siang")).equals("1")){
                    if (cursor2.getString(cursor2.getColumnIndex("durasi_siang")).equals("1")){
                        if (qqs.equals("1200")){
                            NotifyInfo(10000 + Integer.parseInt(informasi_id),"Siang");
                        }
                    }
                    if (cursor2.getString(cursor2.getColumnIndex("durasi_siang")).equals("2")){
                        if (qqs.equals("1230")){
                            NotifyInfo(10000 + Integer.parseInt(informasi_id),"Siang");
                        }
                    }
                }
                if (cursor2.getString(cursor2.getColumnIndex("waktu_sore")).equals("1")){
                    if (cursor2.getString(cursor2.getColumnIndex("durasi_sore")).equals("1")){
                        if (qqs.equals("1500")){
                            NotifyInfo(10000 + Integer.parseInt(informasi_id),"Sore");
                        }
                    }
                    if (cursor2.getString(cursor2.getColumnIndex("durasi_sore")).equals("2")){
                        if (qqs.equals("1530")){
                            NotifyInfo(10000 + Integer.parseInt(informasi_id),"Sore");
                        }
                    }
                }
                if (cursor2.getString(cursor2.getColumnIndex("waktu_malam")).equals("1")){
                    if (cursor2.getString(cursor2.getColumnIndex("durasi_malam")).equals("1")){
                        if (qqs.equals("1900")){
                            NotifyInfo(10000 + Integer.parseInt(informasi_id),"Malam");
                        }
                    }
                    if (cursor2.getString(cursor2.getColumnIndex("durasi_malam")).equals("2")){
                        if (qqs.equals("1930")){
                            NotifyInfo(10000 + Integer.parseInt(informasi_id),"Malam");
                        }
                    }
                }

            } while (cursor2.moveToNext());
        }
    }

    private void NotifyInfo(Integer id, String dlmRangka){
        Cursor cursor3;
        String mateeeeeri = "";
        app2 = App.getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor3 = db.rawQuery("SELECT * FROM mob_informasi where informasi_id = '" + String.valueOf(id - 10000) + "'", null);
        if (cursor3.moveToFirst()) {
            do {
                mateeeeeri =cursor3.getString(cursor3.getColumnIndex("materi")) ;
                Intent resultIntent = new Intent(context, InformasiDetailActivity.class);
                resultIntent.putExtra("id",String.valueOf(id - 10000));
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPending = stackBuilder
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(1)// notification icon
                        .setContentTitle("Pemberitahuan Informasi " + dlmRangka) // main title of the notification
                        .setContentText(mateeeeeri)
                        .setAutoCancel(true); // notification text
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    mBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    mBuilder.setSmallIcon( R.mipmap.ic_launcher);
                    mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                }
                if (app2.getDB().getUser().suara == null || app2.getDB().getUser().suara.equals("default")){
                    qqq = Uri.parse("android.resource://" + context.getPackageName() + "/"
                            + R.raw.sound);
                } else if (app2.getDB().getUser().suara.equals("sound1")){
                    qqq = Uri.parse("android.resource://" + context.getPackageName() + "/"
                            + R.raw.sound1);
                } else if (app2.getDB().getUser().suara.equals("sound2")){
                    qqq = Uri.parse("android.resource://" + context.getPackageName() + "/"
                            + R.raw.sound2);
                } else if (app2.getDB().getUser().suara.equals("sound3")){
                    qqq = Uri.parse("android.resource://" + context.getPackageName() + "/"
                            + R.raw.sound3);
                } else if (app2.getDB().getUser().suara.equals("sound4")){
                    qqq = Uri.parse("android.resource://" + context.getPackageName() + "/"
                            + R.raw.sound4);
                } else if (app2.getDB().getUser().suara.equals("sound5")){
                    qqq = Uri.parse("android.resource://" + context.getPackageName() + "/"
                            + R.raw.sound5);
                }
                mBuilder.setSound(qqq);
                mBuilder.setContentIntent(resultPending); // notification intent
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(id, mBuilder.build());
            } while (cursor3.moveToNext());
        }
        db.close();
    }

}
