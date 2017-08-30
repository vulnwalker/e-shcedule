package com.eschedule.eclient.model;

import com.eschedule.eclient.core.ColumnType;
import com.google.gson.annotations.SerializedName;

import org.chalup.microorm.annotations.Column;



public class Alarm {
    public static String TABLE = "mob_alarm";

    @Column("alarm_id")
    @ColumnType(property = "NOT NULL PRIMARY KEY")
    @SerializedName("alarm_id")
    public String alarm_id;

    @Column("al_tanggal_mulai")
    @ColumnType
    @SerializedName("al_tanggal_mulai")
    public String al_tanggal_mulai;

    @Column("al_tahun")
    @ColumnType()
    @SerializedName("al_tahun")
    public String al_tahun;

    @Column("al_bulan")
    @ColumnType
    @SerializedName("al_bulan")
    public String al_bulan;

    @Column("al_tanggal")
    @ColumnType
    @SerializedName("al_tanggal")
    public String al_tanggal;

    @Column("al_jam")
    @ColumnType
    @SerializedName("al_jam")
    public String al_jam;

    @Column("al_menit")
    @ColumnType
    @SerializedName("al_menit")
    public String al_menit;

    @Column("al_title")
    @ColumnType
    @SerializedName("al_title")
    public String al_title;

    @Column("al_acara")
    @ColumnType
    @SerializedName("al_acara")
    public String al_acara;

    @Column("al_type")
    @ColumnType
    @SerializedName("al_type")
    public String al_type;

    public Alarm(){

    }


}
