package com.eschedule.eclient.lu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eschedule.eclient.App;
import com.eschedule.eclient.database.DB;
import com.google.common.base.Joiner;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.RecyclerClickListener;
import com.eschedule.eclient.informasi.InformasiDetailActivity;
import com.eschedule.eclient.jadwal.JadwalDetailActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;




public class ListUser extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView list_item;
    private List<itemObject> itemObjects;
    private List<selected> selecteds;
    private android.support.v7.widget.LinearLayoutManager LinearLayoutManager;
    private AdapterItem2 adapter;
    DB dbHelper;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list_item = (RecyclerView)findViewById(R.id.rcc);
        LinearLayoutManager = new LinearLayoutManager(this);
        list_item.setLayoutManager(LinearLayoutManager);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(this);
        selecteds = new ArrayList<>();
        list_item.addOnItemTouchListener(
                new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TextView uid = (TextView) view.findViewById(R.id.uid);
                        TextView nama = (TextView) view.findViewById(R.id.nama);
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_list_item);
                        if (checkBox.isChecked()){
                            checkBox.setChecked(false);
                            for (int i =0;i<selecteds.size();i++) {
                                selected data = selecteds.get(i);
                                if (data.getNama().equals(nama.getText().toString()) && data.getUid().equals(uid.getText().toString())){
                                    selecteds.remove(i);
                                }
                            }
                       } else {
                            checkBox.setChecked(true);
                            selecteds.add(new selected(nama.getText().toString(),uid.getText().toString()));
                        }

                    }
                }
                ));

        show();

    }
    private void show() {
        dbHelper = new DB(getApplicationContext());
        SQLiteDatabase db44 = dbHelper.getWritableDatabase();
        cursor = db44.rawQuery("SELECT * FROM mob_user", null);
        String qscb = "";
        if (cursor.moveToFirst()) {
            do {
                qscb  = cursor.getString(cursor.getColumnIndex("user_id"));
            } while (cursor.moveToNext());
        }
        dbHelper.close();

        String[] group = qscb.split("-");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://e-schedule.info/android/nama.php?grup=" + group[0],
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String pregmatch) {
                        try {
                            JSONObject jsonRootObject = new JSONObject(pregmatch);
                            JSONArray jsonArray = jsonRootObject.optJSONArray("result");
                            itemObjects = new ArrayList<>();
                            for (int i = 0 ; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                itemObjects.add(new itemObject(jsonObject.getString("nama").toString(), jsonObject.getString("user_id").toString(), jsonObject.getString("gambar").toString()));
                            }
                            adapter = new AdapterItem2 (getApplicationContext(),itemObjects);
                            list_item.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"KONEKSI BERMASALAH",Toast.LENGTH_LONG).show();
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
    @Override
    public void onClick(View view) {
    if (selecteds.size() == 0) {
        Toast.makeText(this,"Pilih tujuan informasi",Toast.LENGTH_SHORT).show();
    } else {
      Intent next = new Intent(this, Insert_info.class);


        String json = new Gson().toJson(selecteds);
        String isinya = "" ;
        String idnya = "";
        try{
            JSONArray jsond = new JSONArray(json);
            for(int i=0;i<jsond.length();i++){
                JSONObject a = jsond.getJSONObject(i);
                if (isinya.equals("")) {
                    isinya = a.getString("nama");
                } else {
                    isinya =  isinya + ", " + a.getString("nama");
                }
                if (idnya.equals("")) {
                    idnya= a.getString("uid");
                } else {
                    idnya =  idnya + ", " + a.getString("uid");
                }

            }
            next.putExtra("json_list", isinya);
            next.putExtra("idnya", idnya);
            startActivity(next);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.toString() ,Toast.LENGTH_LONG).show();
        }



    }
    }

}
