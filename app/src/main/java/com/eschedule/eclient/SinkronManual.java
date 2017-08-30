package com.eschedule.eclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;
import static android.provider.Settings.System.DATE_FORMAT;


public class SinkronManual extends Service {
    Integer batas = 5;
    DB dbHelper;
    private App app2;
    Context context;
    Cursor cursor;
    public SinkronManual() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        enter();

        return START_NOT_STICKY;
    }


    private void enter(){
        aku();
        deleteInformasi();
        deleteJadwal();
        clear_alarm_info();
        clear_alarm_jadwal();
        reloadData();
        Intent reboot = new Intent(context, RebootAlarmJadwal.class);
        context.startService(reboot);

    }
    private void deleteJadwal(){
        dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from mob_jadwal");
    }
    private void deleteInformasi() {
        dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from mob_informasi");
    }
    public void reloadData() {
        getInformasi();
        getJadwal();
    }

    private void getInformasi(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://e-schedule.info/android/informasi.php?user_id=" + aku,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            JSONArray jsonArray = jsonRootObject.optJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                insertInfo(jsonObject.getString("informasi_id").toString(),jsonObject.getString("informasi_parent_id").toString(),jsonObject.getString("kepada_s").toString(),jsonObject.getString("user_id").toString(),jsonObject.getString("user_name").toString(),jsonObject.getString("user_picture").toString(),jsonObject.getString("st_ralat").toString(),jsonObject.getString("st_penting").toString(),jsonObject.getString("materi").toString(),jsonObject.getString("fl_tgl_mulai").toString(),jsonObject.getString("fl_tgl_akhir").toString(),jsonObject.getString("waktu_pagi").toString(),jsonObject.getString("waktu_siang").toString(),jsonObject.getString("waktu_sore").toString(),jsonObject.getString("waktu_malam").toString(),jsonObject.getString("durasi_pagi").toString(),jsonObject.getString("durasi_siang").toString(),jsonObject.getString("durasi_sore").toString(),jsonObject.getString("durasi_malam").toString(),jsonObject.getString("tgl_fusion").toString(),jsonObject.getString("tampilkan").toString());
                                insert_InnerInfo(jsonObject.getString("informasi_id").toString(),jsonObject.getString("fl_tgl_mulai").toString(),jsonObject.getString("fl_tgl_akhir").toString());
                                delelte_InnerJoin(jsonObject.getString("informasi_parent_id").toString());
                                updateInfo(jsonObject.getString("informasi_parent_id").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void insertInfo(String informasi_id, String informasi_parent_id, String kepada_s, String user_id, String user_name, String user_picture, String st_ralat, String st_penting, String materi, String fl_tgl_mulai, String fl_tgl_akhir, String waktu_pagi, String waktu_siang, String waktu_sore, String waktu_malam, String durasi_pagi, String durasi_siang, String durasi_sore, String durasi_malam, String tgl_fusion, String tampilkan){
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into mob_informasi (informasi_id,informasi_parent_id,kepada_s,user_id,user_name,user_picture,st_ralat,st_penting,materi,fl_tgl_mulai,fl_tgl_akhir,waktu_pagi,waktu_siang,waktu_sore,waktu_malam,durasi_pagi,durasi_siang,durasi_sore,durasi_malam,tgl_fusion,tampilkan) values ('" + Integer.parseInt(informasi_id) + "','" + Integer.parseInt(informasi_parent_id) + "','" + kepada_s + "','" + user_id + "','" + user_name + "','" + user_picture + "','" + st_ralat + "','" + st_penting + "','" + materi + "','" + fl_tgl_mulai + "','" + fl_tgl_akhir + "','" + Integer.parseInt(waktu_pagi) + "','" + Integer.parseInt(waktu_siang) + "','" + Integer.parseInt(waktu_sore) + "','" + Integer.parseInt(waktu_malam) + "','" + Integer.parseInt(durasi_pagi) + "','" + Integer.parseInt(durasi_siang) + "','" + Integer.parseInt(durasi_sore) + "','" + Integer.parseInt(durasi_malam) +"','" + tgl_fusion + "','" + tampilkan + "')");
        dbHelper.close();
    }
    private void insert_InnerInfo(String info_id, String tanggal_mulai, String tanggal_akhir){
        String[] pecah_tanggal_mulai = tanggal_mulai.split("-");
        String[] pecah_tanggal_akhir = tanggal_akhir.split("-");
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into inner_info (informasi_id, tanggal_mulai, tanggal_akhir) values('" + info_id+ "','" + pecah_tanggal_mulai[0] + pecah_tanggal_mulai[1] + pecah_tanggal_mulai[2]  + "','" + pecah_tanggal_akhir[0] + pecah_tanggal_akhir[1] + pecah_tanggal_akhir[2] + "')");
        dbHelper.close();
    }

    private void delelte_InnerJoin(String parentID) {
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from inner_info where informasi_id = '" + parentID + "'");
        dbHelper.close();
    }
    private void updateInfo(String parentID) {
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update mob_informasi set tampilkan = '0' where informasi_id = '" + parentID + "'");
        dbHelper.close();
    }

    private void  clear_alarm_info(){
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from inner_info");
        dbHelper.close();
    }
    private void  clear_alarm_jadwal(){
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from mob_alarm");
        dbHelper.close();
    }

    private void getJadwal(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://e-schedule.info/android/jadwal.php?user_id=" + aku ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Kena : ", response.toString());
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            JSONArray jsonArray = jsonRootObject.optJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                insertJadwal(jsonObject.getString("jadwal_id").toString(),jsonObject.getString("user_id").toString(),jsonObject.getString("user_name").toString(),jsonObject.getString("user_picture").toString(),jsonObject.getString("untuk_s").toString(),jsonObject.getString("tgl_mulai").toString(),jsonObject.getString("jam_mulai").toString(),jsonObject.getString("tgl_selesai").toString(),jsonObject.getString("jam_selesai").toString(),jsonObject.getString("acara").toString(),jsonObject.getString("tempat").toString(),jsonObject.getString("alamat").toString(),jsonObject.getString("keterangan").toString(),jsonObject.getString("sumber").toString(),jsonObject.getString("sumber_no").toString(),jsonObject.getString("sumber_tgl").toString(),jsonObject.getString("sumber_terima").toString(),jsonObject.getString("tgl_entry").toString(),jsonObject.getString("tgl_fusion").toString(),jsonObject.getString("jam_fusion").toString(),jsonObject.getString("alert1").toString(),jsonObject.getString("alert2").toString(),jsonObject.getString("st_alert1").toString(),jsonObject.getString("st_alert2").toString());
                                String ac = jsonObject.getString("acara").toString();
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

                                //jsonObject.getString("informasi_id").toString()
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v("Error json",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("volley","sss");
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void insertJadwal(String jadwal_id, String user_id, String user_name, String user_picture, String untuk_s, String tgl_mulai, String jam_mulai, String tgl_selesai, String jam_selesai, String acara, String tempat, String alamat, String keterangan, String sumber, String sumber_no, String sumber_tgl, String sumber_terima, String tgl_entry, String tgl_fusion, String jam_fusion, String alert1, String alert2, String st_alert1, String st_alert2){
        batas = 5;
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into mob_jadwal (jadwal_id,user_id,user_name,user_picture,untuk_s,tgl_mulai,jam_mulai,tgl_selesai,jam_selesai,acara,tempat,alamat,keterangan,sumber,sumber_no,sumber_tgl,sumber_terima,tgl_entry,tgl_fusion,jam_fusion,alert1,alert2,st_alert1,st_alert2) values ('" + Integer.parseInt(jadwal_id) + "','" + user_id + "','" + user_name + "','" + user_picture + "','" + untuk_s + "','" + tgl_mulai + "','" + jam_mulai + "','" + tgl_selesai + "','" + jam_selesai+ "','" + acara + "','" + tempat + "','" + alamat + "','" + keterangan + "','" + sumber + "','" + sumber_no + "','" + sumber_tgl + "','" + sumber_terima + "','" + tgl_entry + "','" + tgl_fusion + "','" + jam_fusion + "','" + alert1 + "','" + alert2 + "','" + st_alert1 + "','" + st_alert2 + "')");
        dbHelper.close();
    }

    private void Alarm_Jadwal(Integer tahun,Integer bulan,Integer tanggal,Integer jam,Integer menit, String title, String acara, String type, Integer id) {
        String x = String.valueOf(tahun);
        String y = "";
        String z = "";
        if (bulan < 10){
            y = "0"+String.valueOf(bulan);
        }else {
            y = String.valueOf(bulan);
        }
        z = String.valueOf(tanggal);

        if (tanggal < 10) {
            x = String.valueOf(tahun);
        }else{
            x = String.valueOf(tahun);
        }
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into mob_alarm (alarm_id,al_tanggal_mulai,al_tanggal,al_bulan,al_tahun,al_jam,al_menit,al_title,al_acara,al_type) values('"+ String.valueOf(id + 1000) + "','"+  z+x+y + "','"+ x + "','"+ y + "','"+ z + "','"+ String.valueOf(jam) + "','"+ String.valueOf(menit) + "','"+ title + "','"+ acara + "','jadwal')");
        dbHelper.close();
    }

    private void Alarm_Jadwal_Menit(Integer tahun,Integer bulan,Integer tanggal,Integer jam,Integer menit, String title, String acara, String type, Integer id) {
        String x = String.valueOf(tahun);
        String y = "";
        String z = "";
        if (bulan < 10){
            y = "0"+String.valueOf(bulan);
        }else {
            y = String.valueOf(bulan);
        }
            z = String.valueOf(tanggal);

        if (tanggal < 10) {
            x = String.valueOf(tahun);
        }else{
            x = String.valueOf(tahun);
        }
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into mob_alarm (alarm_id,al_tanggal_mulai,al_tanggal,al_bulan,al_tahun,al_jam,al_menit,al_title,al_acara,al_type) values('"+ String.valueOf(id + 5000) + "','"+  z+x+y + "','"+ x + "','"+ y + "','"+ z + "','"+ String.valueOf(jam) + "','"+ String.valueOf(menit) + "','"+ title + "','"+ acara + "','jadwal_menit')");
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
    String aku = "";
    private void aku(){
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db45 = dbHelper.getWritableDatabase();
        cursor = db45.rawQuery("SELECT * FROM mob_user", null);

        if (cursor.moveToFirst()) {
            do {
                aku  = cursor.getString(cursor.getColumnIndex("user_id"));
            } while (cursor.moveToNext());
        }
        dbHelper.close();
    }




}
