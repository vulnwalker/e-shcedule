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
import com.eschedule.eclient.auth.LoginActivity;
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

public class reloadData extends Service {
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
        timer.schedule(new DelayedTask(), 300, 6000);
    }

    private class DelayedTask extends TimerTask {

        @Override
        public void run() {
            Log.v(TAG,"restore data running");

            File dbExist = getApplicationContext().getDatabasePath(DB.DB_NAME);
            if(dbExist.exists()) {
                enter();
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
        Log.v(TAG, "STOPED");
    }
    private void enter(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://e-schedule.info/android/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("failed")){
                            if (connection_status == 0){
                                Log.i(TAG,"data keambil");

                                deleteInformasi();
                                deleteJadwal();
                                reloadData();

                                dbHelper.close();
                            }
                            connection_status = 1;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG,"gak ada koneksi");
                        connection_status = 0;
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
        app2 = App.getInstance();
        Call<Data.ResponLoad> call = app2.getApi().getData(app2.getDB().getUser().userId);
        call.enqueue(new Callback<Data.ResponLoad>() {
            @Override
            public void onResponse(Call<Data.ResponLoad> call, retrofit2.Response<Data.ResponLoad> response) {
                DB db = app2.getDB();

                for(Jadwal item : response.body().jadwal) {
                    db.manageJadwal(item);
                }

                for(Informasi item : response.body().informasi) {
                    db.manageInformasi(item);
                }
            }
            @Override
            public void onFailure(Call<Data.ResponLoad> call, Throwable t) {
            }
        });
    }



}
