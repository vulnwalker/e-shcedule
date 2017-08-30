package com.eschedule.eclient.model;

import com.eschedule.eclient.core.ColumnType;
import com.google.gson.annotations.SerializedName;

import org.chalup.microorm.annotations.Column;



public class InnerInfo {
    public static String TABLE = "inner_info";

    @Column("informasi_id")
    @ColumnType(property = "NOT NULL PRIMARY KEY")
    @SerializedName("informasi_id")
    public String informasi_id;

    @Column("tanggal_mulai")
    @ColumnType
    @SerializedName("tanggal_mulai")
    public String tanggal_mulai;

    @Column("tanggal_akhir")
    @ColumnType()
    @SerializedName("tanggal_akhir")
    public String tanggal_akhir;

    public InnerInfo(){

    }


}
