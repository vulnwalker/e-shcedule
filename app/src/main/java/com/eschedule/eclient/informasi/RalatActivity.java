package com.eschedule.eclient.informasi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.eschedule.eclient.App;
import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.adapter.InfoRecAdapter;
import com.eschedule.eclient.model.Informasi;

import java.util.List;

public class RalatActivity extends AppCompatActivity {

    private List<Informasi> mDataset;
    private RecyclerView mInfoRecycle;
    private InfoRecAdapter mDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ralat);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDataset = App.getInstance().getDB().getRalat(getIntent().getStringExtra("id"));
        mDataAdapter = new InfoRecAdapter(this, mDataset, false);

        mInfoRecycle = (RecyclerView) findViewById(R.id.ralat_recycle);
        mInfoRecycle.setLayoutManager(new LinearLayoutManager(this));
        mInfoRecycle.setHasFixedSize(true);
        mInfoRecycle.setAdapter(mDataAdapter);
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
