package com.eschedule.eclient.jadwal;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.notifikasiService;

public class JadwalDetailActivity extends AppCompatActivity {
    DB dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView kepada = (TextView) findViewById(R.id.kepada);
        TextView tanggal_kegiatan = (TextView) findViewById(R.id.tanggal_kegiatan);
        TextView jam_kegiatan = (TextView) findViewById(R.id.jam_kegiatan);
        TextView acara = (TextView) findViewById(R.id.acara);
        TextView tempat = (TextView) findViewById(R.id.tempat);
        TextView alamat = (TextView) findViewById(R.id.alamat);
        TextView sumber = (TextView) findViewById(R.id.sumber);
        TextView nomor_surat = (TextView) findViewById(R.id.nomor_surat);
        TextView tanggal_surat = (TextView) findViewById(R.id.tanggal_surat);
        TextView keterangan = (TextView) findViewById(R.id.keterangan);
        TextView oleh = (TextView) findViewById(R.id.oleh);

        Intent notificationIntent= getIntent();
        Bundle nI = notificationIntent.getExtras();
        kepada.setText((String) nI.get("kepada"));
        tanggal_kegiatan.setText((String) nI.get("tanggal_kegiatan"));
        jam_kegiatan.setText((String) nI.get("jam_kegiatan"));
        acara.setText((String) nI.get("acara"));
        tempat.setText((String) nI.get("tempat"));
        alamat.setText((String) nI.get("alamat"));
        sumber.setText((String) nI.get("sumber"));
        nomor_surat.setText((String) nI.get("nomor_surat"));
        tanggal_surat.setText((String) nI.get("tanggal_surat"));
        if(sumber.getText().toString().equals("")){
            sumber.setText("-");
        }
        if(nomor_surat.getText().toString().equals("")){
            nomor_surat.setText("-");
        }
        if (tanggal_surat.getText().toString().equals("")) {
            tanggal_surat.setText("-");
        }
        keterangan.setText((String) nI.get("keterangan"));
        oleh.setText((String) nI.get("oleh"));
        String dalam_rangka = (String) nI.get("dalam_rangka");
        String ji = (String) nI.get("id");


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
