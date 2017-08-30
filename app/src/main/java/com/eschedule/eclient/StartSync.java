package com.eschedule.eclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.eschedule.eclient.auth.LoginActivity;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.jadwal.JadwalDetailActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class StartSync extends BroadcastReceiver {
    public StartSync() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        File dbExist = context.getDatabasePath(DB.DB_NAME);

        if(dbExist.exists()) {


            context.startService(new Intent(context, reloadData.class));
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
            Intent start = new Intent(context, StartService.class);
            Intent reboot = new Intent(context, RebootAlarmJadwal.class);
            context.startService(start);
            context.startService(reboot);

        }
    }



}
