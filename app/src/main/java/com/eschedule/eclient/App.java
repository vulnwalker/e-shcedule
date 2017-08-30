package com.eschedule.eclient;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.eschedule.eclient.auth.LoginActivity;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.database.DBManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zund#i on 13/10/2016.
 */

public class App extends Application {

    // Basic Data
    public static final String PREF_NAME = "eclient";
    public static final String BASE_URL = "http://e-schedule.info/";
    public static final String REG_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String TOPIC = "global";

    public static final int TIMEOUT_DEFAULT = 20;

    private static App mInstance;
    private Retrofit retrofit = null;
    private DB db = null;
    private Preferences pref = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public Retrofit getClient(int timeout) {
        if(retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public Retrofit getClient() {
        return getClient(TIMEOUT_DEFAULT);
    }

    public ApiInterface getApi() {
        return getClient().create(ApiInterface.class);
    }

    public Preferences getPreferences() {
        if (pref == null) {
            pref = new Preferences(getApplicationContext());
        }
        return pref;

    }

    public DB getDB() {
        if(db == null) {
            db = new DB(this);
            DBManager.initializeInstance(db);
        }
        return db;
    }

    public void startSession(Activity activity) {
        if(getStateUser()) {

            Call<Boolean> call = getApi().sendID(
                    getPreferences().getRegId(),
                    getDB().getUser().userId
            );

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {}

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.d("App", "Error meh...");
                }
            });

            Intent in = new Intent(activity, MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
        }
    }

    public void destroySession(Activity activity) {
        File dbFile = getDatabasePath(DB.DB_NAME);
        dbFile.delete();

        Intent in = new Intent(activity, LoginActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }

    private boolean getStateUser() {
        File dbFile = getDatabasePath(DB.DB_NAME);
        return dbFile.exists();
    }

}
