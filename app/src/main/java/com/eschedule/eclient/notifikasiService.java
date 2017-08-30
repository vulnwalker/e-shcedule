package com.eschedule.eclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;

public class notifikasiService extends BroadcastReceiver {
    DB dbHelper;
    private App app2;
    Uri qqq ;
    Cursor cursor;
    Context context;
    @Override
    public void onReceive(Context context2, Intent intent) {
        context = context2;
        if (intent.getStringExtra("type").equals("jadwal")) {
            Notify_Jadwal(intent.getStringExtra("title"), intent.getStringExtra("acara"), Integer.parseInt(intent.getStringExtra("id")));
        }
        if (intent.getStringExtra("type").equals("jadwal_menit")) {
            Notify_Jadwal_Menit(intent.getStringExtra("title"), intent.getStringExtra("acara"), Integer.parseInt(intent.getStringExtra("id")));
        }
    }
    private void Notify_Jadwal(String title, String acara, Integer id){
        app2 = App.getInstance();
        dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM mob_jadwal where jadwal_id = '" + String.valueOf(id - 1000) + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Intent resultIntent = new Intent(context, JadwalDetailActivity.class);
                resultIntent.putExtra("kepada",cursor.getString(cursor.getColumnIndex("untuk_s")));
                resultIntent.putExtra("tanggal_kegiatan", cursor.getString(cursor.getColumnIndex("tgl_fusion")));
                resultIntent.putExtra("jam_kegiatan", cursor.getString(cursor.getColumnIndex("jam_fusion")));
                resultIntent.putExtra("acara", cursor.getString(cursor.getColumnIndex("acara")));
                resultIntent.putExtra("tempat", cursor.getString(cursor.getColumnIndex("tempat")));
                resultIntent.putExtra("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                resultIntent.putExtra("sumber", cursor.getString(cursor.getColumnIndex("sumber")));
                resultIntent.putExtra("sumber_no", cursor.getString(cursor.getColumnIndex("sumber_no")));
                resultIntent.putExtra("sumber_tgl", cursor.getString(cursor.getColumnIndex("sumber_tgl")));
                resultIntent.putExtra("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                resultIntent.putExtra("oleh", cursor.getString(cursor.getColumnIndex("user_name")));
                resultIntent.putExtra("id", String.valueOf(id - 1000));
                resultIntent.putExtra("dalam_rangka", "kosong ae");
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPending = stackBuilder
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(1)// notification icon
                        .setContentTitle(title) // main title of the notification
                        .setContentText(acara)
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
            } while (cursor.moveToNext());
        }

    }

    private void Notify_Jadwal_Menit(String title, String acara, Integer id){
        app2 = App.getInstance();
        dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM mob_jadwal where jadwal_id = '" + String.valueOf(id - 5000) + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Intent resultIntent = new Intent(context, JadwalDetailActivity.class);
                resultIntent.putExtra("kepada",cursor.getString(cursor.getColumnIndex("untuk_s")));
                resultIntent.putExtra("tanggal_kegiatan", cursor.getString(cursor.getColumnIndex("tgl_fusion")));
                resultIntent.putExtra("jam_kegiatan", cursor.getString(cursor.getColumnIndex("jam_fusion")));
                resultIntent.putExtra("acara", cursor.getString(cursor.getColumnIndex("acara")));
                resultIntent.putExtra("tempat", cursor.getString(cursor.getColumnIndex("tempat")));
                resultIntent.putExtra("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                resultIntent.putExtra("sumber", cursor.getString(cursor.getColumnIndex("sumber")));
                resultIntent.putExtra("sumber_no", cursor.getString(cursor.getColumnIndex("sumber_no")));
                resultIntent.putExtra("sumber_tgl", cursor.getString(cursor.getColumnIndex("sumber_tgl")));
                resultIntent.putExtra("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                resultIntent.putExtra("oleh", cursor.getString(cursor.getColumnIndex("user_name")));
                resultIntent.putExtra("id", String.valueOf(id - 5000));
                resultIntent.putExtra("dalam_rangka", "kosong ae");
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPending = stackBuilder
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(1)// notification icon
                        .setContentTitle(title) // main title of the notification
                        .setContentText(acara)
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
            } while (cursor.moveToNext());
        }
    }

}

