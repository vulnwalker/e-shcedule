package com.eschedule.eclient.auth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.eschedule.eclient.App;
import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.SinkronManual;
import com.eschedule.eclient.StartService;
import com.eschedule.eclient.core.Util;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.model.Data;
import com.eschedule.eclient.model.Informasi;
import com.eschedule.eclient.model.Jadwal;
import com.eschedule.eclient.model.User;
import com.eschedule.eclient.notifikasiService;
import com.eschedule.eclient.reloadData;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public final static String TAG = LoginActivity.class.getSimpleName();

    private EditText mLoginUser, mLoginPass;
    private ImageButton mLoginSubmit;
    private ProgressDialog mProgressDialog;
    private App app;
    private byte[] userPicByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        mLoginPass.setTransformationMethod(new PasswordTransformationMethod());
        mLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        File dbExist = getApplicationContext().getDatabasePath(DB.DB_NAME);
        if(dbExist.exists()) {
            app.startSession(this);
            stopService(new Intent(LoginActivity.this, reloadData.class));
            startService(new Intent(LoginActivity.this, reloadData.class));
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    private void findViews() {
        app = App.getInstance();

        mLoginUser = (EditText) findViewById(R.id.login_user_id);
        mLoginPass = (EditText) findViewById(R.id.login_user_password);
        mLoginSubmit = (ImageButton) findViewById(R.id.login_submit);
    }

    private boolean onValidate() {
        boolean ok = true;
        if(!Util.hasEmpty(mLoginUser)) ok = false;
        if(!Util.hasEmpty(mLoginPass)) ok = false;
        return ok;
    }

    private void onSubmit() {
        if(onValidate()) {
            onPostData();
        }
    }

    private void onPostData() {
        mProgressDialog = Util.progressDialog(LoginActivity.this, "Autentifikasi");
        mProgressDialog.show();

        Call<Data.ResponUser> call = app.getApi().getUser(Util.val(mLoginUser), Util.val(mLoginPass));
        call.enqueue(new Callback<Data.ResponUser>() {
            @Override
            public void onResponse(Call<Data.ResponUser> call, Response<Data.ResponUser> response) {
                if(response.body().success) {
                    User user = response.body().rows.get(0);

                    // Ambil Gambar Untuk Mendownload
                    Glide.with(getApplicationContext())
                            .load(App.BASE_URL+"images/"+user.userPicture)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    userPicByte = Util.createByteFromBitmap(resource);
                                }
                            });

                    // Simpan Ke SQL
                    app.getDB().manageUser(user, userPicByte);

                    // Load Data Pertama Kali
                    onLoadFirst();

                } else {
                    Util.toast(getApplicationContext(), response.body().msg);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Data.ResponUser> call, Throwable t) {
                Log.e(TAG, t.toString());
                mProgressDialog.dismiss();
            }
        });
    }

    public void onLoadFirst() {
        mProgressDialog = Util.progressDialog(LoginActivity.this, "Sinkronasi Data");
        mProgressDialog.show();

        Call<Data.ResponLoad> call = app.getApi().getData(app.getDB().getUser().userId);
        call.enqueue(new Callback<Data.ResponLoad>() {
            @Override
            public void onResponse(Call<Data.ResponLoad> call, Response<Data.ResponLoad> response) {
                DB db = app.getDB();

                for(Jadwal item : response.body().jadwal) {
                    db.manageJadwal(item);
                }

                for(Informasi item : response.body().informasi) {
                    db.manageInformasi(item);
                }
                startService(new Intent(LoginActivity.this, SinkronManual.class));
                app.startSession(LoginActivity.this);
            }

            @Override
            public void onFailure(Call<Data.ResponLoad> call, Throwable t) {

            }
        });
    }

    }
