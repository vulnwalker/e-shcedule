package com.eschedule.eclient.jadwal;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.RecyclerClickListener;
import com.eschedule.eclient.adapter.InfoRecAdapter;
import com.eschedule.eclient.adapter.JadwalRecAdapter;
import com.eschedule.eclient.database.DB;
import com.eschedule.eclient.model.Jadwal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class JadwalFragment extends Fragment {
    DB dbHelper;
    Cursor cursor;
    private List<Jadwal> mDataset;
    private RecyclerView mJadwalRecycle;
    private RelativeLayout mJadwalNoData;
    private JadwalRecAdapter mDataAdapter;
    private MainActivity mParent;

    public JadwalFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mParent = (MainActivity) getActivity();

        mDataset = mParent.myApp.getDB().getJadwal();
        mDataAdapter = new JadwalRecAdapter(mParent, mDataset);
        mJadwalNoData.setVisibility( mDataset.size() > 0 ? View.GONE : View.VISIBLE);

        mJadwalRecycle.setLayoutManager(new LinearLayoutManager(mParent));
        mJadwalRecycle.setHasFixedSize(true);
        mJadwalRecycle.setAdapter(mDataAdapter);
        mJadwalRecycle.addOnItemTouchListener(
                new RecyclerClickListener(getActivity(), new RecyclerClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d("click item", String.valueOf(position));
                        TextView jadwal_id = (TextView) view.findViewById(R.id.item_jadwal_idjd);
                        String iniJadwalID = jadwal_id.getText().toString();
                        Log.d("jadwal ID : ", iniJadwalID);

                        Intent notificationIntent = new Intent(getActivity(), JadwalDetailActivity.class);

                        dbHelper = new DB(getActivity().getApplicationContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        cursor = db.rawQuery("SELECT * FROM mob_jadwal where jadwal_id = '" + iniJadwalID + "'", null);
                        if (cursor.moveToFirst()) {
                            do {
                                notificationIntent.putExtra("kepada",cursor.getString(cursor.getColumnIndex("untuk_s")));
                                notificationIntent.putExtra("tanggal_kegiatan", cursor.getString(cursor.getColumnIndex("tgl_fusion")));
                                notificationIntent.putExtra("jam_kegiatan", cursor.getString(cursor.getColumnIndex("jam_fusion")));
                                notificationIntent.putExtra("acara", cursor.getString(cursor.getColumnIndex("acara")));
                                notificationIntent.putExtra("tempat", cursor.getString(cursor.getColumnIndex("tempat")));
                                notificationIntent.putExtra("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                                notificationIntent.putExtra("sumber", cursor.getString(cursor.getColumnIndex("sumber")));
                                notificationIntent.putExtra("sumber_no", cursor.getString(cursor.getColumnIndex("sumber_no")));
                                notificationIntent.putExtra("sumber_tgl", cursor.getString(cursor.getColumnIndex("sumber_tgl")));
                                notificationIntent.putExtra("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                                notificationIntent.putExtra("oleh", cursor.getString(cursor.getColumnIndex("user_name")));
                                notificationIntent.putExtra("id", iniJadwalID);
                                notificationIntent.putExtra("dalam_rangka", "kosongkan sajalah");
                            } while (cursor.moveToNext());
                        }
                        startActivity(notificationIntent);
                    }
                }
                ));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_jadwal, container, false);
        mJadwalRecycle = (RecyclerView) v.findViewById(R.id.jadwal_recycle);
        mJadwalNoData = (RelativeLayout) v.findViewById(R.id.jadwal_nodata);
        getActivity().findViewById(R.id.add_info).setVisibility(View.INVISIBLE);
//        dbHelper = new DB(getActivity().getApplicationContext());
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.execSQL("delete from mob_jadwal where REPLACE(tgl_selesai, '-', '') < '"  +  new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-","") + "'");
        return v;
    }

    public void swap() {
        mDataset = mParent.myApp.getDB().getJadwal();
        mJadwalRecycle.setAdapter(new JadwalRecAdapter(mParent, mDataset));
        mJadwalNoData.setVisibility( mDataset.size() > 0 ? View.GONE : View.VISIBLE);
    }
}
