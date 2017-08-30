package com.eschedule.eclient.informasi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.eschedule.eclient.App;
import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.model.Informasi;
import com.eschedule.eclient.notifikasiService;

import java.util.List;

public class InformasiDetailActivity extends AppCompatActivity {

    private TextView mDetailKepada, mDetailMateri, mDetailRalat, mDetailPenting, mDetailTgl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        List<Informasi> mDataset = App.getInstance().getDB()
                .getInformasi(getIntent().getStringExtra("id"));
        mDetailKepada.setText(mDataset.get(0).kepada);
        mDetailMateri.setText(mDataset.get(0).materi);
        mDetailRalat.setText(mDataset.get(0).stRalat.equals("1") ? "Ralat" : "Tidak" );
        mDetailPenting.setText(mDataset.get(0).stPenting.equals("1") ? "Penting" : "Tidak");
        mDetailTgl.setText(mDataset.get(0).tglFusion);
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

    private void findViews() {
        mDetailKepada = (TextView) findViewById(R.id.info_datail_kepada);
        mDetailMateri = (TextView) findViewById(R.id.info_datail_materi);
        mDetailRalat = (TextView)findViewById(R.id.info_datail_stralat);
        mDetailPenting = (TextView) findViewById(R.id.info_datail_stinfo);
        mDetailTgl = (TextView) findViewById(R.id.info_datail_tgl);
    }

}
