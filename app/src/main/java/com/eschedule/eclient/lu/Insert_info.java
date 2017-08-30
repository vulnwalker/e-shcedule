package com.eschedule.eclient.lu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.database.DB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Insert_info extends AppCompatActivity implements View.OnClickListener {
    String json_data, uidnya;
    DatePicker fl_tgl_mulai, fl_tgl_akhir;
    String me;
    RadioButton pagi1,pagi2 , siang1,siang2, sore1,sore2, malam1,malam2;
    RadioGroup rg_pagi, rg_siang, rg_malam, rg_sore;
    CheckBox penting, waktu_pagi, waktu_siang , waktu_sore, waktu_malam;
    EditText materi;
    TextView kepada;
    String durasi_pagi = "0";
    String durasi_siang = "0";
    String durasi_sore = "0";
    String durasi_malam = "0";
    String mulai , akhir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent next= getIntent();
        Bundle nI = next.getExtras();
        json_data = (String) nI.get("json_list");
        uidnya = (String) nI.get("idnya");
        kepada = (TextView) findViewById(R.id.jang);
        fl_tgl_mulai = (DatePicker) findViewById(R.id.fl_tgl_mulai);
        fl_tgl_akhir = (DatePicker) findViewById(R.id.fl_tgl_akhir);
        penting = (CheckBox) findViewById(R.id.penting);
        pagi1 = (RadioButton) findViewById(R.id.pagi1);
        pagi2 = (RadioButton) findViewById(R.id.pagi2);
        siang1 = (RadioButton) findViewById(R.id.siang1);
        siang2 = (RadioButton) findViewById(R.id.siang2);
        sore1 = (RadioButton) findViewById(R.id.sore1);
        sore2 = (RadioButton) findViewById(R.id.sore2);
        malam1 = (RadioButton) findViewById(R.id.malam1);
        malam2 = (RadioButton) findViewById(R.id.malam2);
        waktu_pagi = (CheckBox) findViewById(R.id.waktu_pagi);
        waktu_siang = (CheckBox) findViewById(R.id.waktu_siang);
        waktu_sore = (CheckBox) findViewById(R.id.waktu_sore);
        waktu_malam = (CheckBox) findViewById(R.id.waktu_malam);
        materi = (EditText) findViewById(R.id.materi);
        kepada.setText(json_data);
        waktu_pagi.setChecked(true);
        waktu_siang.setChecked(true);
        waktu_sore.setChecked(true);
        waktu_malam.setChecked(true);
        pagi1.setChecked(true);
        siang1.setChecked(true);
        sore1.setChecked(true);
        malam1.setChecked(true);
        ImageButton send_button = (ImageButton) findViewById(R.id.next);
        send_button.setVisibility(View.VISIBLE);
        send_button.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        String s_materi = materi.getText().toString();
        String s_kepada = kepada.getText().toString();
        String s_penting = "0";
        String s_penting_s = "Tidak";
        String s_waktu_pagi = "0";
        String s_waktu_siang = "0";
        String s_waktu_sore = "0";
        String s_waktu_malam = "0";

        String s_fl_tgl_mulai = "";
        String s_fl_tgl_ahir = "";

        //radio button

        if (pagi1.isChecked()){
            durasi_pagi = "1";
        } else if (pagi2.isChecked()){
            durasi_pagi = "2";
        }
        if (siang1.isChecked()){
            durasi_siang = "1";
        } else if (siang2.isChecked()){
            durasi_siang = "2";
        }
        if (sore1.isChecked()){
            durasi_sore = "1";
        } else if (sore2.isChecked()){
            durasi_sore = "2";
        }
        if (malam1.isChecked()){
            durasi_malam = "1";
        } else if (sore2.isChecked()){
            durasi_malam = "2";
        }
        //

        //Check BOXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        if (penting.isChecked()){
            s_penting = "1";
            s_penting_s = "Ya";
        }
        if (waktu_pagi.isChecked()){
            s_waktu_pagi = "1";
        }
        if (waktu_siang.isChecked()){
            s_waktu_siang = "1";
        }
        if (waktu_sore.isChecked()){
            s_waktu_sore = "1";
        }
        if (waktu_malam.isChecked()){
            s_waktu_malam = "1";
        }

        //Check BOXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

        //

        if (s_materi.equals("")) {
            Toast.makeText(getApplicationContext(),"Mohon isi materi informasi", Toast.LENGTH_SHORT).show();
        } else {
            int day1 = fl_tgl_mulai.getDayOfMonth();
            int month1 = fl_tgl_mulai.getMonth() + 1;
            int year1 = fl_tgl_mulai.getYear();
            int day2 = fl_tgl_mulai.getDayOfMonth();
            int month2 = fl_tgl_mulai.getMonth() + 1;
            int year2 = fl_tgl_mulai.getYear();
            mulai = String.valueOf(year1) + "-" + String.valueOf(month1) + "-" + String.valueOf(day1);
            akhir = String.valueOf(year2) + "-" + String.valueOf(month2) + "-" + String.valueOf(day2);
//            Toast.makeText(getApplicationContext(),"Mulai :" + mulai + "   Selesai" + akhir + " Penting = " + s_penting , Toast.LENGTH_LONG).show();
//          //Get Username
            DB dbHelper;
            Cursor cursor;
            dbHelper = new DB(getApplicationContext());
            SQLiteDatabase db44 = dbHelper.getWritableDatabase();
            cursor = db44.rawQuery("SELECT * FROM mob_user", null);
            String qscb = "";
            if (cursor.moveToFirst()) {
                do {
                    qscb  = cursor.getString(cursor.getColumnIndex("user_id"));
                    me = cursor.getString(cursor.getColumnIndex("user_name"));
                } while (cursor.moveToNext());
            }
            dbHelper.close();


            // pengoprasian tanggal
            String s_tgl_awal, s_tgl_akhir;
            String[] pecah_tanggal_mulai = mulai.split("-");
            String bulan_mulai = "";
            if (pecah_tanggal_mulai[1].equals("1")) {
                bulan_mulai = "Januari";
            } else if (pecah_tanggal_mulai[1].equals("2")) {
                bulan_mulai = "Februari";
            } else if (pecah_tanggal_mulai[1].equals("3")) {
                bulan_mulai = "Maret";
            } else if (pecah_tanggal_mulai[1].equals("4")) {
                bulan_mulai = "April";
            } else if (pecah_tanggal_mulai[1].equals("5")) {
                bulan_mulai = "Mei";
            } else if (pecah_tanggal_mulai[1].equals("6")) {
                bulan_mulai = "Juni";
            } else if (pecah_tanggal_mulai[1].equals("7")) {
                bulan_mulai = "Juli";
            } else if (pecah_tanggal_mulai[1].equals("8")) {
                bulan_mulai = "Agustus";
            } else if (pecah_tanggal_mulai[1].equals("9")) {
                bulan_mulai = "September";
            } else if (pecah_tanggal_mulai[1].equals("10")) {
                bulan_mulai = "Oktober";
            } else if (pecah_tanggal_mulai[1].equals("11")) {
                bulan_mulai = "November";
            } else if (pecah_tanggal_mulai[1].equals("12")) {
                bulan_mulai = "Desember";
            }
            s_tgl_awal = pecah_tanggal_mulai[2] + " " + bulan_mulai + " " + pecah_tanggal_mulai[0];

            String[] pecah_tanggal_akhir = akhir.split("-");
            String bulan_akhir = "";
            if (pecah_tanggal_akhir[1].equals("1")) {
                bulan_akhir = "Januari";
            } else if (pecah_tanggal_akhir[1].equals("2")) {
                bulan_akhir = "Februari";
            } else if (pecah_tanggal_akhir[1].equals("3")) {
                bulan_akhir = "Maret";
            } else if (pecah_tanggal_akhir[1].equals("4")) {
                bulan_akhir = "April";
            } else if (pecah_tanggal_akhir[1].equals("5")) {
                bulan_akhir = "Mei";
            } else if (pecah_tanggal_akhir[1].equals("6")) {
                bulan_akhir = "Juni";
            } else if (pecah_tanggal_akhir[1].equals("7")) {
                bulan_akhir = "Juli";
            } else if (pecah_tanggal_akhir[1].equals("8")) {
                bulan_akhir = "Agustus";
            } else if (pecah_tanggal_akhir[1].equals("9")) {
                bulan_akhir = "September";
            } else if (pecah_tanggal_akhir[1].equals("10")) {
                bulan_akhir = "Oktober";
            } else if (pecah_tanggal_akhir[1].equals("11")) {
                bulan_akhir = "November";
            } else if (pecah_tanggal_akhir[1].equals("12")) {
                bulan_akhir = "Desember";
            }
            s_tgl_akhir = pecah_tanggal_akhir[2] + " " + bulan_akhir + " " + pecah_tanggal_akhir[0];
            post(me,s_waktu_pagi,s_waktu_siang,s_waktu_sore,s_waktu_malam,s_penting,s_penting_s,qscb,durasi_pagi,durasi_siang,durasi_sore,durasi_malam,s_tgl_awal,s_tgl_akhir,s_materi);
        }
    }
    private void post(final String usernamnya, final String s_waktu_pagi, final String s_waktu_siang, final String s_waktu_sore, final String s_waktu_malam, final String s_penting, final String s_penting_s, final String whoami, final String durasi_pagi, final String durasi_siang, final String durasi_sore, final String durasi_malam,  final String fl_tgl_mulai_s,  final String fl_tgl_akhir_s, final String s_materi) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://e-schedule.info/controller/c_informasi/curl_url.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String pregmatch) {
                        Toast.makeText(getApplicationContext(),"INFORMASI TERKIRIM",Toast.LENGTH_LONG).show();
                        Intent beres = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(beres);
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
                String jang = "";
                String untuk_sopo = uidnya.replace(" ","");
                String kapan_mulai ="";
                String kapan_akhir = "";
                String mgd = "";
                String mgm = "";
                String mgy = "";
                String agd = "";
                String agm = "";
                String agy = "";

                if (fl_tgl_mulai.getDayOfMonth() < 10){
                    mgd = "0"+ String.valueOf(fl_tgl_mulai.getDayOfMonth());
                }else{
                    mgd = String.valueOf(fl_tgl_mulai.getDayOfMonth());
                }
                if (fl_tgl_mulai.getMonth() + 1 < 10){
                    mgm = "0"+ String.valueOf(fl_tgl_mulai.getMonth() + 1);
                } else {
                    String.valueOf(fl_tgl_mulai.getMonth() + 1);
                }
                mgy = String.valueOf(fl_tgl_mulai.getYear());

                if (fl_tgl_akhir.getDayOfMonth() < 10){
                    agd = "0"+ String.valueOf(fl_tgl_akhir.getDayOfMonth());
                }else{
                    agd = String.valueOf(fl_tgl_akhir.getDayOfMonth());
                }
                if (fl_tgl_akhir.getMonth() + 1 < 10){
                    agm = "0"+ String.valueOf(fl_tgl_akhir.getMonth() + 1);
                } else {
                    String.valueOf(fl_tgl_akhir.getMonth() + 1);
                }
                agy = String.valueOf(fl_tgl_akhir.getYear());


                kapan_mulai = mgd + "-" + mgm + "-" + mgy;
                kapan_akhir = agd + "-" + agm + "-" + agy;


//                kapan_mulai = String.valueOf(fl_tgl_mulai.getDayOfMonth()) + "-" + String.valueOf(fl_tgl_mulai.getMonth() + 1)+ "-" + String.valueOf(fl_tgl_mulai.getYear());
//                kapan_akhir = String.valueOf(fl_tgl_akhir.getDayOfMonth()) + "-" + String.valueOf(fl_tgl_akhir.getMonth() + 1)+ "-" + String.valueOf(fl_tgl_akhir.getYear());
//
                String[] ini_array_kepada = untuk_sopo.split(",");
                    for (int a = 0; a < ini_array_kepada.length; a++) {
                        if (a == 0){
                            jang = "data[untuks][0][user_id]="+ini_array_kepada[0];
                        }else {
                            jang = jang +"&data[untuks][" + String.valueOf(a) + "][user_id]="+ini_array_kepada[a];
                        }
                    }
                String data_untuk_di_post = "";
                if (s_penting.equals("1")){
                     data_untuk_di_post = jang + "&data[user_id]="+ whoami +"&data[dari]="+ whoami +"&data[st_ralat]=0&data[st_penting]=1&data[materi]="+ s_materi + "&data[fl_tgl_mulai]=" + kapan_mulai + "&data[fl_tgl_akhir]=" + kapan_akhir + "";
                }else if (s_penting.equals("0")){
                     data_untuk_di_post = jang + "&data[user_id]="+ whoami +"&data[dari]="+ whoami +"&data[st_ralat]=0&data[materi]="+ s_materi + "&data[fl_tgl_mulai]=" + kapan_mulai + "&data[fl_tgl_akhir]=" + kapan_akhir + "";
                }
                if (s_waktu_pagi.equals("1")){
                    data_untuk_di_post = data_untuk_di_post + "&data[waktu_pagi]=1";
                    if (durasi_pagi.equals("1")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_pagi]=1";
                    } else if (durasi_pagi.equals("2")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_pagi]=2";
                    }
                }
                if (s_waktu_siang.equals("1")){
                    data_untuk_di_post = data_untuk_di_post + "&data[waktu_siang]=1";
                    if (durasi_siang.equals("1")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_siang]=1";
                    } else if (durasi_pagi.equals("2")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_siang]=2";
                    }
                }
                if (s_waktu_sore.equals("1")){
                    data_untuk_di_post = data_untuk_di_post + "&data[waktu_sore]=1";
                    if (durasi_sore.equals("1")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_sore]=1";
                    } else if (durasi_sore.equals("2")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_sore]=2";
                    }
                }
                if (s_waktu_malam.equals("1")){
                    data_untuk_di_post = data_untuk_di_post + "&data[waktu_malam]=1";
                    if (durasi_malam.equals("1")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_malam]=1";
                    } else if (durasi_malam.equals("2")){
                        data_untuk_di_post = data_untuk_di_post + "&data[durasi_malam]=2";
                    }
                }



                Log.v("ini datanya buat ngirim : ", data_untuk_di_post);
                Map<String,String> params = new HashMap<>();
                params.put("hasil_post",data_untuk_di_post);
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
}
