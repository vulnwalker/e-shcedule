package com.eschedule.eclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.eschedule.eclient.auth.LoginActivity;
import com.eschedule.eclient.core.Util;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.model.Data;
import com.eschedule.eclient.model.Informasi;
import com.eschedule.eclient.model.Jadwal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartService extends Service {

    private App app = App.getInstance();
    public StartService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Call<Data.ResponLoad> call = app.getApi().getData(app.getDB().getUser().userId);
        call.enqueue(new Callback<Data.ResponLoad>() {
            @Override
            public void onResponse(Call<Data.ResponLoad> call, Response<Data.ResponLoad> response) {
                DB db = app.getDB();
                db.deleteAll();

                for(Jadwal item : response.body().jadwal) {
                    db.manageJadwal(item);
                }

                for(Informasi item : response.body().informasi) {
                    db.manageInformasi(item);
                }
            }

            @Override
            public void onFailure(Call<Data.ResponLoad> call, Throwable t) {
                Util.toast(getApplicationContext(), "Koneksi Bermasalah, Gunakan Sync Manual");
            }
        });

        return START_NOT_STICKY;
    }
}
