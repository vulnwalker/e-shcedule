package com.eschedule.eclient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eschedule.eclient.auth.LoginActivity;
import com.eschedule.eclient.core.Util;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.informasi.InformasiFragment;
import com.eschedule.eclient.lu.ListUser;
import com.eschedule.eclient.jadwal.JadwalDetailActivity;
import com.eschedule.eclient.jadwal.JadwalFragment;
import com.eschedule.eclient.settings.PengaturanFragment;
import com.eschedule.eclient.settings.TentangFragment;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public App myApp;
    public PendingIntent pendingIntent;
    public AlarmManager manager;
    public static final String TAG = MainActivity.class.getSimpleName();
    public String TAG_INFORMASI;
    private BroadcastReceiver mRegBroadcastReceiver;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView mDrawerNav;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mainVersion, mainUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp = App.getInstance();
        stopService(new Intent(MainActivity.this, reloadData.class));
        startService(new Intent(MainActivity.this, reloadData.class));
        stopService(new Intent(MainActivity.this,setInformasiAlarm.class));
        startService(new Intent(MainActivity.this, setInformasiAlarm.class));
        findViews();
        mRegBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(App.REG_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(App.TOPIC);
                }

                if (intent.getAction().equals(App.PUSH_NOTIFICATION)) {
                    String type = intent.getStringExtra("type");
                    onHandle(intent, type);
                }
            }
        };

        mainUser.setText("Hi, " + myApp.getDB().getUser().userName);
        mainVersion.setText("Versi " + Util.getVersion(this));

        if (savedInstanceState == null) {
            mDrawerNav.getMenu().performIdentifierAction(R.id.nav_info, 0);
        }
        ImageButton add_info = (ImageButton) findViewById(R.id.add_info);
        add_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,  ListUser.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mRegBroadcastReceiver, new IntentFilter(App.REG_COMPLETE));

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mRegBroadcastReceiver, new IntentFilter(App.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
            mDrawer.closeDrawer(Gravity.LEFT);
        } else {
            moveTaskToBack(true);
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void page(MenuItem menuItem) {
        String abcdefgh = "biasa";
        if (menuItem.getItemId() == R.id.nav_keluar) {
            onLogout();
        } else {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.nav_info:
                    fragment = new InformasiFragment();
                    abcdefgh = "biasa";
                    break;
                case R.id.nav_jadwal:
                    fragment = new JadwalFragment();
                    abcdefgh = "biasa";
                    break;
                case R.id.nav_pengaturan:
                    fragment = new PengaturanFragment();
                    abcdefgh = "biasa";
                    break;
                case R.id.nav_reload:
                    fragment = new InformasiFragment();
                    abcdefgh = "reload";
                    break;
                case R.id.nav_tentang:
                    fragment = new TentangFragment();
                    abcdefgh = "biasa";
                    break;
                default:
                    fragment = new InformasiFragment();
                    abcdefgh = "biasa";
            }
            if (abcdefgh.equals("biasa")){
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
            }

        }
        // Hilangkan Ceklis Untuk Pertama
        if (abcdefgh.equals("biasa")){
            mDrawerNav.getMenu().findItem(R.id.nav_info).setChecked(false);
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
        }else {
            reload();
        }

        mDrawer.closeDrawers();

    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerNav = (NavigationView) findViewById(R.id.main_nav);
        setupDrawerContent(mDrawerNav);

        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);

        mainUser = (TextView) mDrawerNav.getHeaderView(0).findViewById(R.id.main_user);
        mainVersion = (TextView) mDrawerNav.getHeaderView(0).findViewById(R.id.main_version);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                onHideKeyboard();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                onHideKeyboard();
            }
        };

        return actionBarDrawerToggle;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        page(menuItem);
                        return true;
                    }
                }
        );
    }

    private void onHideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void onLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Keluar Dari e-Schedule Mobile ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startService(new Intent(MainActivity.this, reloadData.class));
                        myApp.destroySession(MainActivity.this);
                        dialog.dismiss();
                        cancelAlarm();
                        LocalBroadcastManager.getInstance(MainActivity.this)
                                .unregisterReceiver(mRegBroadcastReceiver);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void onHandle(Intent intent, String type) {
        if (type.equals(Common.TYPE_UPDATE_ACCOUNT)) {
            mainUser.setText(intent.getStringExtra("extra"));
        }

        if (page() instanceof InformasiFragment) {
            if (type.equals(Common.TYPE_ADD_INFO) ||
                    type.equals(Common.TYPE_DEL_INFO) ||
                    type.equals(Common.TYPE_EDT_INFO) ||
                    type.equals(Common.TYPE_REV_INFO)) {
                ((InformasiFragment) page()).swap();
            }
        }

        if (page() instanceof JadwalFragment) {
            if (type.equals(Common.TYPE_ADD_JADWAL) ||
                    type.equals(Common.TYPE_DEL_JADWAL) || type.equals(Common.TYPE_EDT_JADWAL)) {
                ((JadwalFragment) page()).swap();
            }
        }
    }

    private Fragment page() {
        return getSupportFragmentManager().findFragmentById(R.id.main_content);
    }


    public void cancelAlarm() {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
    }





    private void reload(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://e-schedule.info/android/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("failed")){
                            startService(new Intent(MainActivity.this, SinkronManual.class));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Koneksi Bermasalah",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}