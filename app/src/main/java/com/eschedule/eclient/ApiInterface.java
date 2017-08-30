package com.eschedule.eclient;

import com.eschedule.eclient.model.Data;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Zund#i on 13/10/2016.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("controller/mobile/auth")
    Call<Data.ResponUser> getUser(@Field("user_id") String userid, @Field("user_password") String password);

    @FormUrlEncoded
    @POST("controller/mobile/getall")
    Call<Data.ResponLoad> getData(@Field("user_id") String userid);

    @FormUrlEncoded
    @POST("controller/mobile/firebaseid")
    Call<Boolean> sendID(@Field("firebase_id") String firebaseID, @Field("user_id") String userID);

    @FormUrlEncoded
    @POST("controller/informasi/save-mobile")
    Call<String> sendInfo(
            @Field("user_name") String username,
            @Field("materi") String materi,
            @Field("waktu_pagi") String waktu_pagi,
            @Field("waktu_siang") String waktu_siang,
            @Field("waktu_sore") String waktu_sore,
            @Field("waktu_malam") String waktu_malam,
            @Field("st_penting_s") String st_penting_s,
            @Field("st_penting") String st_penting,
            @Field("kepada") String kepada,
            @Field("kepada_s") String kepada_s,
            @Field("durasi_pagi") String durasi_pagi,
            @Field("durasi_siang") String durasi_siang,
            @Field("durasi_sore") String durasi_sore,
            @Field("durasi_malam") String durasi_malam,
            @Field("fl_tgl_akhir_s") String fl_tgl_akhir_s,
            @Field("fl_tgl_mulai_s") String fl_tgl_mulai_s,
            @Field("tgl_fusion") String tgl_fusion,
            @Field("dari") String dari,
            @Field("fl_tgl_mulai") String fl_tgl_mulai,
            @Field("fl_tgl_akhir") String fl_tgl_akhir,
            @Field("tampil") String tampil,
            @Field("informasi_parent_id") String informasi_parent_id,
            @Field("st_ralat") String st_ralat);

}
