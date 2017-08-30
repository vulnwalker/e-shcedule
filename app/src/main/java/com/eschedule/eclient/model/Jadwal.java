package com.eschedule.eclient.model;

import com.eschedule.eclient.core.ColumnType;
import com.google.gson.annotations.SerializedName;

import org.chalup.microorm.annotations.Column;


/**
 * Created by Zund#i on 20/10/2016.
 */

public class Jadwal {
    public static String TABLE = "mob_jadwal";

    @Column("jadwal_id")
    @ColumnType(type = "INTEGER", property = "NOT NULL PRIMARY KEY")
    @SerializedName("jadwal_id")
    public String jadwalID;

    @Column("user_id")
    @ColumnType
    @SerializedName("user_id")
    public String userID;

    @Column("user_name")
    @ColumnType()
    @SerializedName("user_name")
    public String userName;

    @Column("user_picture")
    @ColumnType
    @SerializedName("user_picture")
    public String userPicture;

    @Column("untuk_s")
    @ColumnType
    @SerializedName("untuk_s")
    public String untuk;

    @Column("tgl_mulai")
    @ColumnType
    @SerializedName("tgl_mulai")
    public String tglMulai;

    @Column("jam_mulai")
    @ColumnType
    @SerializedName("jam_mulai")
    public String jamMulai;

    @Column("tgl_selesai")
    @ColumnType
    @SerializedName("tgl_selesai")
    public String tglSelesai;

    @Column("jam_selesai")
    @ColumnType
    @SerializedName("jam_selesai")
    public String jamSelesai;

    @Column("acara")
    @ColumnType
    @SerializedName("acara")
    public String acara;

    @Column("tempat")
    @ColumnType
    @SerializedName("tempat")
    public String tempat;

    @Column("alamat")
    @ColumnType
    @SerializedName("alamat")
    public String alamat;

    @Column("keterangan")
    @ColumnType
    @SerializedName("keterangan")
    public String keterangan;

    @Column("sumber")
    @ColumnType
    @SerializedName("sumber")
    public String sumber;

    @Column("sumber_no")
    @ColumnType
    @SerializedName("sumber_no")
    public String sumberNo;

    @Column("sumber_tgl")
    @ColumnType
    @SerializedName("sumber_tgl")
    public String sumberTgl;

    @Column("sumber_terima")
    @ColumnType
    @SerializedName("sumber_terima")
    public String sumberTerima;

    @Column("tgl_entry")
    @ColumnType
    @SerializedName("tgl_entry")
    public String tglEntry;

    @Column("tgl_fusion")
    @ColumnType
    @SerializedName("tgl_fusion")
    public String tglFusion;

    @Column("jam_fusion")
    @ColumnType
    @SerializedName("jam_fusion")
    public String jamFusion;

    @Column("alert1")
    @ColumnType
    @SerializedName("alert1")
    public String alert1;

    @Column("alert2")
    @ColumnType
    @SerializedName("alert2")
    public String alert2;

    @Column("st_alert1")
    @ColumnType
    @SerializedName("st_alert1")
    public String st_alert1;

    @Column("st_alert2")
    @ColumnType
    @SerializedName("st_alert2")
    public String st_alert2;

    public Jadwal(){

    }


}
