package com.eschedule.eclient;

import android.app.AlarmManager;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.informasi.InformasiDetailActivity;
import com.eschedule.eclient.jadwal.JadwalDetailActivity;
import com.eschedule.eclient.model.Informasi;
import com.eschedule.eclient.model.Jadwal;
import com.eschedule.eclient.model.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.provider.Settings.System.DATE_FORMAT;


public class PushService extends FirebaseMessagingService {
    Integer batas = 5;
    public final static String TAG = PushService.class.getSimpleName();
    Uri qqq;
    App app2;
    private JSONArray pushData;
    private String pushType;
    String iniJSON = "";
    DB dbHelper;
    Cursor cursor;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        batas = 5;
        Log.v("wakwaw", remoteMessage.getData().toString());
        iniJSON = remoteMessage.getData().toString();
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleMessage(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(JSONObject json) throws JSONException {
        DB db = App.getInstance().getDB();
        Intent push = new Intent(App.PUSH_NOTIFICATION);
        Log.v("data get", "get in");
        pushType = json.getString("type");

        if(!pushType.equals(Common.TYPE_DEL_INFO)) {
            pushData = json.getJSONArray("data");
        }

        // test
        if (pushType.equals(Common.TYPE_UPDATE_ACCOUNT)) {
            User item = new GsonBuilder().create()
                    .fromJson(pushData.getString(0), User.class);
            db.manageUser(item, null, true);
            push.putExtra("extra", item.userName);
        }

        if (pushType.equals(Common.TYPE_ADD_INFO) || pushType.equals(Common.TYPE_EDT_INFO)) {
            Informasi item = new GsonBuilder().create()
                    .fromJson(pushData.getString(0), Informasi.class);
            boolean editMode = pushType.equals(Common.TYPE_EDT_INFO);
            db.manageInformasi(item, editMode);
            if(pushType.equals(Common.TYPE_ADD_INFO)) {
                try {
                    JSONObject jsonRootObject = new JSONObject(iniJSON);
                    JSONArray jsonArray = jsonRootObject.optJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String aku = "";
                        String who = jsonObject.getString("user_id").toString();
                        String infID = jsonObject.getString("informasi_id").toString();
                        String mti = jsonObject.getString("materi").toString();
                        dbHelper = new DB(getApplicationContext());
                        SQLiteDatabase db45 = dbHelper.getWritableDatabase();
                        cursor = db45.rawQuery("SELECT * FROM mob_user", null);

                        if (cursor.moveToFirst()) {
                            do {
                                aku  = cursor.getString(cursor.getColumnIndex("user_id"));
                            } while (cursor.moveToNext());
                        }
                        dbHelper.close();
                        if (who.equals(aku)){}
                        else {
                            Notify_Informasi("Informasi baru diterima ", mti, infID);
                        }
                        insert_InnerInfo(infID,jsonObject.getString("fl_tgl_mulai").toString(),jsonObject.getString("fl_tgl_akhir").toString() );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(iniJSON);
                    JSONArray jsonArray = jsonRootObject.optJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String aku = "";
                        String who = jsonObject.getString("user_id").toString();
                        String infID = jsonObject.getString("informasi_id").toString();
                        String mti = jsonObject.getString("materi").toString();
                        dbHelper = new DB(getApplicationContext());
                        SQLiteDatabase db45 = dbHelper.getWritableDatabase();
                        cursor = db45.rawQuery("SELECT * FROM mob_user", null);

                        if (cursor.moveToFirst()) {
                            do {
                                aku  = cursor.getString(cursor.getColumnIndex("user_id"));
                            } while (cursor.moveToNext());
                        }
                        dbHelper.close();
                        insert_InnerInfo(infID,jsonObject.getString("fl_tgl_mulai").toString(),jsonObject.getString("fl_tgl_akhir").toString() );
                        if (who.equals(aku)){}
                        else {
                            Notify_Informasi("Perubahan informasi diterima ", mti, infID);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (pushType.equals(Common.TYPE_DEL_INFO)) {
            JSONArray upd = json.getJSONObject("data").getJSONArray("update");
            JSONArray dlt = json.getJSONObject("data").getJSONArray("delete");

            for(int i=0; i<dlt.length(); i++) {
                Informasi item = new GsonBuilder().create()
                        .fromJson(String.valueOf(dlt.getJSONObject(i)), Informasi.class);
                db.deleteInformasi(item);
            }

            for(int i=0; i<upd.length(); i++) {
                Informasi item = new GsonBuilder().create()
                        .fromJson(String.valueOf(upd.getJSONObject(i)), Informasi.class);
                db.manageInformasi(item, true);
            }
        }
        if(pushType.equals(Common.TYPE_REV_INFO)) {
            for(int i=0; i<pushData.length(); i++) {
                Informasi item = new GsonBuilder().create()
                        .fromJson(pushData.getString(i), Informasi.class);
                db.deleteInformasi(item);
                db.manageInformasi(item);
            }
            try {
                JSONObject jsonRootObject = new JSONObject(iniJSON);
                JSONArray jsonArray = jsonRootObject.optJSONArray("data");
                    JSONObject js2 = jsonArray.getJSONObject(1);
                    String infID = js2.getString("informasi_parent_id").toString();
                    delelte_InnerJoin(infID);
                    insert_InnerInfo(js2.getString("informasi_id").toString(),js2.getString("fl_tgl_mulai").toString(),js2.getString("fl_tgl_akhir").toString() );

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if(pushType.equals(Common.TYPE_ADD_JADWAL) || pushType.equals(Common.TYPE_EDT_JADWAL)) {
            Jadwal item = new GsonBuilder().create()
                    .fromJson(pushData.getString(0), Jadwal.class);
            boolean editMode = pushType.equals(Common.TYPE_EDT_JADWAL);
            db.manageJadwal(item, editMode);
            if(pushType.equals(Common.TYPE_ADD_JADWAL)) {
                try {
                    JSONObject jsonRootObject = new JSONObject(iniJSON);
                    JSONArray jsonArray = jsonRootObject.optJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String aku = "";
                        String jiiii = "";
                        String ju = "";
                        String ac = "";
                        String who = jsonObject.getString("user_id").toString();
                        jiiii = jsonObject.getString("jadwal_id").toString();
                        ju = jsonObject.getString("jam_fusion").toString();
                        ac = jsonObject.getString("acara").toString();
                        dbHelper = new DB(getApplicationContext());
                        SQLiteDatabase db44 = dbHelper.getWritableDatabase();
                        cursor = db44.rawQuery("SELECT * FROM mob_user", null);

                        if (cursor.moveToFirst()) {
                            do {
                                aku  = cursor.getString(cursor.getColumnIndex("user_id"));
                            } while (cursor.moveToNext());
                        }
                        dbHelper.close();
                        if (who.equals(aku)){}
                        else {
                            Notify_Jadwal("Jadwal baru diterima ", ju, ac, jiiii, "kosong ae" );
                            String[] pecah_tanggal_mulai = jsonObject.getString("tgl_mulai").toString().split("-");
                            String[] waktu = jsonObject.getString("jam_mulai").toString().split(":");
                            if (jsonObject.getString("st_alert1").toString().equals("1")) {
                                if (jsonObject.getString("alert1").toString().equals("1")){
                                    if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                        Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    } else {
                                        if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 >0) {
                                            Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        } else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                Integer tanggal__ = 32;
                                                for (int ST = 1; ST < batas; ST++) {
                                                    tanggal__ = tanggal__ - 1;
                                                    dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                }
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            } else {
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }
                                        }
                                    }
                                } else if(jsonObject.getString("alert1").toString().equals("2")){
                                    if (Integer.parseInt(waktu[0]) - 2 >= 0){
                                        Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 2,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    } else {
                                        if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 >0) {
                                            if (Integer.parseInt(waktu[0]) == 00 || Integer.parseInt(waktu[0]) == 0){
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,22,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            } else if (Integer.parseInt(waktu[0]) == 01 || Integer.parseInt(waktu[0]) == 1){
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }
                                        } else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                Integer tanggal__ = 32;
                                                for (int ST = 1; ST < batas; ST++) {
                                                    tanggal__ = tanggal__ - 1;
                                                    dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                }
                                                if (Integer.parseInt(waktu[0]) == 00 || Integer.parseInt(waktu[0]) == 0){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,22,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else if (Integer.parseInt(waktu[0]) == 01 || Integer.parseInt(waktu[0]) == 1){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            } else {
                                                if (Integer.parseInt(waktu[0]) == 00 || Integer.parseInt(waktu[0]) == 0){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,22,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else if (Integer.parseInt(waktu[0]) == 01 || Integer.parseInt(waktu[0]) == 1){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                            if (jsonObject.getString("st_alert2").toString().equals("1")) {
                                if (jsonObject.getString("alert2").toString().equals("1")){
                                    if (Integer.parseInt(waktu[1]) - 10 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 10,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("2")){
                                    if (Integer.parseInt(waktu[1]) - 20 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 20,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("3")){
                                    if (Integer.parseInt(waktu[1]) - 30 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("4")){
                                    if (Integer.parseInt(waktu[1]) - 40 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 40,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("5")){
                                    if (Integer.parseInt(waktu[1]) - 50 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 50,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]),"Judul",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(iniJSON);
                    JSONArray jsonArray = jsonRootObject.optJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String aku = "";
                        String jiiii = "";
                        String ju = "";
                        String ac = "";
                        String who = jsonObject.getString("user_id").toString();
                        jiiii = jsonObject.getString("jadwal_id").toString();
                        ju = jsonObject.getString("jam_fusion").toString();
                        ac = jsonObject.getString("acara").toString();
                        dbHelper = new DB(getApplicationContext());
                        SQLiteDatabase db44 = dbHelper.getWritableDatabase();
                        cursor = db44.rawQuery("SELECT * FROM mob_user", null);
                        if (cursor.moveToFirst()) {
                            do {
                                aku  = cursor.getString(cursor.getColumnIndex("user_id"));
                            } while (cursor.moveToNext());
                        }
                        dbHelper.close();
                        if (who.equals(aku)){}
                        else {
                            Notify_Jadwal("Perubahan jadwal diterima ", ju, ac, jiiii, "kosong ae" );
                            String[] pecah_tanggal_mulai = jsonObject.getString("tgl_mulai").toString().split("-");
                            String[] waktu = jsonObject.getString("jam_mulai").toString().split(":");
                            if (jsonObject.getString("st_alert1").toString().equals("1")) {
                                if (jsonObject.getString("alert1").toString().equals("1")){
                                    if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                        Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    } else {
                                        if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 >0) {
                                            Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        } else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                Integer tanggal__ = 32;
                                                for (int ST = 1; ST < batas; ST++) {
                                                    tanggal__ = tanggal__ - 1;
                                                    dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                }
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            } else {
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 1 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }
                                        }
                                    }
                                } else if(jsonObject.getString("alert1").toString().equals("2")){
                                    if (Integer.parseInt(waktu[0]) - 2 >= 0){
                                        Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 2,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    } else {
                                        if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 >0) {
                                            if (Integer.parseInt(waktu[0]) == 00 || Integer.parseInt(waktu[0]) == 0){
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,22,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            } else if (Integer.parseInt(waktu[0]) == 01 || Integer.parseInt(waktu[0]) == 1){
                                                Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }
                                        } else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                Integer tanggal__ = 32;
                                                for (int ST = 1; ST < batas; ST++) {
                                                    tanggal__ = tanggal__ - 1;
                                                    dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                }
                                                if (Integer.parseInt(waktu[0]) == 00 || Integer.parseInt(waktu[0]) == 0){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,22,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else if (Integer.parseInt(waktu[0]) == 01 || Integer.parseInt(waktu[0]) == 1){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            } else {
                                                if (Integer.parseInt(waktu[0]) == 00 || Integer.parseInt(waktu[0]) == 0){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,22,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else if (Integer.parseInt(waktu[0]) == 01 || Integer.parseInt(waktu[0]) == 1){
                                                    Alarm_Jadwal(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]),"Acara akan dimulai dalam 2 jam",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                            if (jsonObject.getString("st_alert2").toString().equals("1")) {
                                if (jsonObject.getString("alert2").toString().equals("1")){
                                    if (Integer.parseInt(waktu[1]) - 10 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 10,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 50,"Acara akan dimulai dalam 10 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("2")){
                                    if (Integer.parseInt(waktu[1]) - 20 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 20,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 40,"Acara akan dimulai dalam 20 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("3")){
                                    if (Integer.parseInt(waktu[1]) - 30 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 30,"Acara akan dimulai dalam 30 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("4")){
                                    if (Integer.parseInt(waktu[1]) - 40 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 40,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 20,"Acara akan dimulai dalam 40 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }else if (jsonObject.getString("alert2").toString().equals("5")){
                                    if (Integer.parseInt(waktu[1]) - 50 >= 0){
                                        Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]),Integer.parseInt(waktu[1]) - 50,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                    }else {
                                        if (Integer.parseInt(waktu[0]) - 1 >= 0){
                                            Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]),Integer.parseInt(waktu[0]) - 1,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                        }else {
                                            if (Integer.parseInt(pecah_tanggal_mulai[0]) - 1 > 0) {
                                                Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]),Integer.parseInt(pecah_tanggal_mulai[0]) - 1,23,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                            }else {
                                                if (Integer.parseInt(pecah_tanggal_mulai[1]) - 1 > 0 ) {
                                                    Integer tahun__  = Integer.parseInt(pecah_tanggal_mulai[2]);
                                                    Integer bulan__  = Integer.parseInt(pecah_tanggal_mulai[1]);
                                                    Integer tanggal__ = 32;
                                                    for (int ST = 1; ST < batas; ST++) {
                                                        tanggal__ = tanggal__ - 1;
                                                        dateChecker(String.valueOf(String.valueOf(tanggal__) +"-"+ String.valueOf(bulan__ - 1)+"-"+ String.valueOf(tahun__)));
                                                    }
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]),Integer.parseInt(pecah_tanggal_mulai[1]) - 1,tanggal__,23,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                } else {
                                                    Alarm_Jadwal_Menit(Integer.parseInt(pecah_tanggal_mulai[2]) - 1,12,31,23,Integer.parseInt(waktu[1]) + 10,"Acara akan dimulai dalam 50 menit",ac,"type",Integer.parseInt(jsonObject.getString("jadwal_id").toString()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(pushType.equals(Common.TYPE_DEL_JADWAL)) {
            Jadwal item  = new GsonBuilder().create()
                    .fromJson(pushData.getString(0), Jadwal.class);
            db.deleteJadwal(item);
        }
        push.putExtra("type", pushType);
        LocalBroadcastManager.getInstance(this).sendBroadcast(push);
    }
    private void Notify_Jadwal(String notificationTitle, String jam, String acara, String iniJD, String dalam_rangka) {
        app2 = App.getInstance();
        dbHelper = new DB(getApplicationContext());
        Intent resultIntent = new Intent(this, JadwalDetailActivity.class);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM mob_jadwal where jadwal_id = '" + iniJD + "'", null);
        if (cursor.moveToFirst()) {
            do {
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
                resultIntent.putExtra("id", iniJD);
                resultIntent.putExtra("dalam_rangka", dalam_rangka);
            } while (cursor.moveToNext());
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(1)// notification icon
                .setContentTitle(notificationTitle) // main title of the notification
                .setContentText(acara)
                .setAutoCancel(true); // notification text
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mBuilder.setColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
            mBuilder.setSmallIcon( R.mipmap.ic_launcher);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher));
        }
        if (app2.getDB().getUser().suara == null || app2.getDB().getUser().suara.equals("default")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound);
        } else if (app2.getDB().getUser().suara.equals("sound1")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound1);
        } else if (app2.getDB().getUser().suara.equals("sound2")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound2);
        } else if (app2.getDB().getUser().suara.equals("sound3")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound3);
        } else if (app2.getDB().getUser().suara.equals("sound4")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound4);
        } else if (app2.getDB().getUser().suara.equals("sound5")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound5);
        }
        mBuilder.setSound(qqq);
        mBuilder.setContentIntent(resultPending);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(Integer.parseInt(iniJD), mBuilder.build());
    }

    private void Notify_Informasi(String notificationTitle,  String materi, String iniInfID) {
        app2 = App.getInstance();
        Intent resultIntent = new Intent(this, InformasiDetailActivity.class);
        resultIntent.putExtra("id",iniInfID);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(1)// notification icon
                .setContentTitle(notificationTitle) // main title of the notification
                .setContentText(materi)
                .setAutoCancel(true); // notification text
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mBuilder.setColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
            mBuilder.setSmallIcon( R.mipmap.ic_launcher);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher));
        }
        if (app2.getDB().getUser().suara == null || app2.getDB().getUser().suara.equals("default")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound);
        } else if (app2.getDB().getUser().suara.equals("sound1")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound1);
        } else if (app2.getDB().getUser().suara.equals("sound2")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound2);
        } else if (app2.getDB().getUser().suara.equals("sound3")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound3);
        } else if (app2.getDB().getUser().suara.equals("sound4")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound4);
        } else if (app2.getDB().getUser().suara.equals("sound5")){
            qqq = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.sound5);
        }
        mBuilder.setSound(qqq);
        mBuilder.setContentIntent(resultPending); // notification intent
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(Integer.parseInt(iniInfID), mBuilder.build());
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
        //batas suci
        String x = String.valueOf(tahun);
        String y = "";
        String z = "";
        if (bulan < 10){
            y = "0"+String.valueOf(bulan);
        }else {
            y = String.valueOf(bulan);
        }
        if (tanggal < 10) {
            z = "0" + String.valueOf(tanggal);
        }else{
            z = String.valueOf(tanggal);
        }
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into mob_alarm (alarm_id,al_tanggal_mulai,al_tahun,al_bulan,al_tanggal,al_jam,al_menit,al_title,al_acara,al_type) values('"+ String.valueOf(id) + "','"+ x + y + z + "','"+ x + "','"+ y + "','"+ z + "','"+ String.valueOf(jam) + "','"+ String.valueOf(menit) + "','"+ title + "','"+ acara + "','jadwal')");
        dbHelper.close();
    }

    private void Alarm_Jadwal_Menit(Integer tahun,Integer bulan,Integer tanggal,Integer jam,Integer menit, String title, String acara, String type, Integer id) {
        id = id + 5000;
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
        PendingIntent PI = PendingIntent.getBroadcast(this, id ,i,PendingIntent.FLAG_CANCEL_CURRENT);
        jadwal.set(AlarmManager.RTC, skr.getTimeInMillis(), PI);

        //batas suci
        String x = String.valueOf(tahun);
        String y = "";
        String z = "";
        if (bulan < 10){
            y = "0"+String.valueOf(bulan);
        }else {
            y = String.valueOf(bulan);
        }
        if (tanggal < 10) {
            z = "0" + String.valueOf(tanggal);
        }else{
            z = String.valueOf(tanggal);
        }
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into mob_alarm (alarm_id,al_tanggal_mulai,al_tahun,al_bulan,al_tanggal,al_jam,al_menit,al_title,al_acara,al_type) values('"+ String.valueOf(id) + "','"+ x + y + z + "','"+ x + "','"+ y + "','"+ z + "','"+ String.valueOf(jam) + "','"+ String.valueOf(menit) + "','"+ title + "','"+ acara + "','jadwal_menit')");
        dbHelper.close();
    }
    public boolean dateChecker(String date) {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            batas = 0;
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void insert_InnerInfo(String info_id, String tanggal_mulai, String tanggal_akhir){
        String[] pecah_tanggal_mulai = tanggal_mulai.split("-");
        String[] pecah_tanggal_akhir = tanggal_akhir.split("-");

        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into inner_info (informasi_id, tanggal_mulai, tanggal_akhir) values('" + info_id+ "','" + pecah_tanggal_mulai[2] + pecah_tanggal_mulai[1] + pecah_tanggal_mulai[0]  + "','" + pecah_tanggal_akhir[2] + pecah_tanggal_akhir[1] + pecah_tanggal_akhir[0] + "')");
        dbHelper.close();
    }

    private void delelte_InnerJoin(String parentID) {
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from inner_info where informasi_id = '" + parentID + "'");
        dbHelper.close();
    }

}
