package com.eschedule.eclient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.eschedule.eclient.auth.LoginActivity;
import com.eschedule.eclient.core.Util;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.model.Data;
import com.eschedule.eclient.model.Informasi;
import com.eschedule.eclient.model.Jadwal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RebootAlarmJadwal extends Service {

    DB dbHelper;
    Cursor cursor;
    Context context;
    public RebootAlarmJadwal() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        setPengingatJadwal();

        return START_NOT_STICKY;
    }


    private void setPengingatJadwal(){
        int tanggal_ayeuna = Integer.parseInt(new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-",""));
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);
        String n_j , n_m;
        if (hour < 10) {
            n_j = "0"+ String.valueOf(hour);
        }else {
            n_j = String.valueOf(hour);
        }
        if (minute < 10) {
            n_m = "0" + String.valueOf(minute);
        }else {
            n_m = String.valueOf(minute);
        }
        String cr = n_j + n_m;




        //batas suci
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor = db.rawQuery("select * from mob_alarm where al_tanggal_mulai >= '" + String.valueOf(tanggal_ayeuna) + "' ", null);
        if (cursor.moveToFirst()) {
            do {
                String ____jam, ____menit;
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_jam"))) < 10) {
                    ____jam = "0" + cursor.getString(cursor.getColumnIndex("al_jam"));
                }else{
                    ____jam = cursor.getString(cursor.getColumnIndex("al_jam"));
                }
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_menit"))) < 10) {
                    ____menit = "0" + cursor.getString(cursor.getColumnIndex("al_menit"));
                }else{
                    ____menit = cursor.getString(cursor.getColumnIndex("al_menit"));
                }
                //Long.parseLong(cursor.getString(cursor.getColumnIndex("al_tanggal_mulai")) + cursor.getString(cursor.getColumnIndex("al_jam")) + cursor.getString(cursor.getColumnIndex("al_menit")) )
                long waktunya = Long.parseLong(cursor.getString(cursor.getColumnIndex("al_tanggal_mulai")) +____jam+____menit);

                if (Long.parseLong(String.valueOf(tanggal_ayeuna) + cr) > waktunya ){
                  //  Toast.makeText(this,String.valueOf(Long.parseLong(String.valueOf(tanggal_ayeuna) + cr)) +" banding " + String.valueOf(Long.parseLong(cursor.getString(cursor.getColumnIndex("al_tanggal_mulai")) + cursor.getString(cursor.getColumnIndex("al_jam")) + cursor.getString(cursor.getColumnIndex("al_menit")) )),Toast.LENGTH_LONG).show();
                } else {
                    if (cursor.getString(cursor.getColumnIndex("al_type")).equals("jadwal")) {
                        Alarm_Jadwal(Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_tahun"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_bulan"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_tanggal"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_jam"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_menit"))),cursor.getString(cursor.getColumnIndex("al_title")),cursor.getString(cursor.getColumnIndex("al_acara")), "jadwal",Integer.parseInt(cursor.getString(cursor.getColumnIndex("alarm_id"))) - 1000);
                    }else{
                        Alarm_Jadwal_Menit(Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_tahun"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_bulan"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_tanggal"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_jam"))),Integer.parseInt(cursor.getString(cursor.getColumnIndex("al_menit"))),cursor.getString(cursor.getColumnIndex("al_title")),cursor.getString(cursor.getColumnIndex("al_acara")), "jadwal_menit",Integer.parseInt(cursor.getString(cursor.getColumnIndex("alarm_id"))));
                    }
                }

            } while (cursor.moveToNext());
        }
    }

    private void Alarm_Jadwal(Integer tahun,Integer bulan,Integer tanggal,Integer jam,Integer menit, String title, String acara, String type, Integer id) {
        id = id + 1000;
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.YEAR,tahun);
        skr.set(Calendar.MONTH,bulan - 1);
        skr.set(Calendar.DAY_OF_MONTH,tanggal);
        skr.set(Calendar.HOUR_OF_DAY,jam);
        skr.set(Calendar.MINUTE,menit);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, notifikasiService.class);
        i.putExtra("title",title);
        i.putExtra("acara",acara);
        i.putExtra("type","jadwal");
        i.putExtra("id",String.valueOf(id));
        PendingIntent PI = PendingIntent.getBroadcast(this, id ,i,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
        //Toast.makeText(this,"Tahun = " + String.valueOf(tahun) +  " Bulan = " + String.valueOf(bulan) +  " Tanggal = " + String.valueOf(tanggal) +  " Jam = " + String.valueOf(jam) +  " Menit = " + String.valueOf(menit) +  " ID = " + String.valueOf(id) +  " Title = " + title +  " Acara = " + acara,Toast.LENGTH_LONG).show();
    }

    private void Alarm_Jadwal_Menit(Integer tahun,Integer bulan,Integer tanggal,Integer jam,Integer menit, String title, String acara, String type, Integer id) {
        Calendar skr =  Calendar.getInstance();
        skr.set(Calendar.YEAR,tahun);
        skr.set(Calendar.MONTH,bulan - 1);
        skr.set(Calendar.DAY_OF_MONTH,tanggal);
        skr.set(Calendar.HOUR_OF_DAY,jam);
        skr.set(Calendar.MINUTE,menit);
        skr.set(Calendar.SECOND,00);
        AlarmManager jadwal = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, notifikasiService.class);
        i.putExtra("title",title);
        i.putExtra("acara",acara);
        i.putExtra("type","jadwal_menit");
        i.putExtra("id",String.valueOf(id));
        PendingIntent PI = PendingIntent.getBroadcast(this, id + 5000 ,i,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);
       // Toast.makeText(this,"Alerm Seted 2",Toast.LENGTH_LONG).show();
    }
}
