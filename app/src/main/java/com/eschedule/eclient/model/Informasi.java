package com.eschedule.eclient.model;

import com.eschedule.eclient.core.ColumnType;
import com.google.gson.annotations.SerializedName;

import org.chalup.microorm.annotations.Column;

/**
 * Created by Zund#i on 21/10/2016.
 */

public class Informasi {

    public static String TABLE = "mob_informasi";

    @Column("informasi_id")
    @ColumnType(type = "INTEGER", property = "NOT NULL PRIMARY KEY")
    @SerializedName("informasi_id")
    public String informasiID;

    @Column("informasi_parent_id")
    @ColumnType(type = "INTEGER")
    @SerializedName("informasi_parent_id")
    public String informasiParentID;

    @Column("kepada_s")
    @ColumnType
    @SerializedName("kepada_s")
    public String kepada;

    @Column("user_id")
    @ColumnType
    @SerializedName("user_id")
    public String userID;

    @Column("user_name")
    @ColumnType
    @SerializedName("user_name")
    public String userName;

    @Column("user_picture")
    @ColumnType
    @SerializedName("user_picture")
    public String userPicture;

    @Column("st_ralat")
    @ColumnType
    @SerializedName("st_ralat")
    public String stRalat;

    @Column("st_penting")
    @ColumnType
    @SerializedName("st_penting")
    public String stPenting;

    @Column("materi")
    @ColumnType
    @SerializedName("materi")
    public String materi;

    @Column("fl_tgl_mulai")
    @ColumnType
    @SerializedName("fl_tgl_mulai")
    public String flTglMulai;

    @Column("fl_tgl_akhir")
    @ColumnType
    @SerializedName("fl_tgl_akhir")
    public String flTglAkhir;

    @Column("waktu_pagi")
    @ColumnType(type = "INTEGER")
    @SerializedName("waktu_pagi")
    public String waktuPagi;

    @Column("waktu_siang")
    @ColumnType(type = "INTEGER")
    @SerializedName("waktu_siang")
    public String waktuSiang;

    @Column("waktu_sore")
    @ColumnType(type = "INTEGER")
    @SerializedName("waktu_sore")
    public String waktuSore;

    @Column("waktu_malam")
    @ColumnType(type = "INTEGER")
    @SerializedName("waktu_malam")
    public String waktuMalam;

    @Column("durasi_pagi")
    @ColumnType(type = "INTEGER")
    @SerializedName("durasi_pagi")
    public String durasiPagi;

    @Column("durasi_siang")
    @ColumnType(type = "INTEGER")
    @SerializedName("durasi_siang")
    public String durasiSiang;

    @Column("durasi_sore")
    @ColumnType(type = "INTEGER")
    @SerializedName("durasi_sore")
    public String durasiSore;

    @Column("durasi_malam")
    @ColumnType(type = "INTEGER")
    @SerializedName("durasi_malam")
    public String durasiMalam;

    @Column("tgl_fusion")
    @ColumnType
    @SerializedName("tgl_fusion")
    public String tglFusion;

    @Column("tampilkan")
    @ColumnType
    @SerializedName("tampilkan")
    public String tampilkan;

    public Informasi() {

    }

}
