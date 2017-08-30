package com.eschedule.eclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zund#i on 20/10/2016.
 */

public class InstanceIDService extends FirebaseInstanceIdService {

    public final static String TAG = InstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Ambil Token Lalu simpan
        String newToken = FirebaseInstanceId.getInstance().getToken();

        // Simpan Ke Shared Preferences
        App.getInstance().getPreferences().setRegID(newToken);

        Intent regComplete = new Intent(App.REG_COMPLETE);
        regComplete.putExtra("token", newToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(regComplete);
    }
}
