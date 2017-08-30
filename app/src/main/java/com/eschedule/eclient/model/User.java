package com.eschedule.eclient.model;

import com.eschedule.eclient.core.ColumnType;
import com.google.gson.annotations.SerializedName;

import org.chalup.microorm.annotations.Column;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Zund#i on 13/10/2016.
 */

public class User {

    public static String TABLE = "mob_user";

    public User() {

    }

    @Column("user_id")
    @ColumnType(property = "NOT NULL PRIMARY KEY")
    @SerializedName("user_id")
    public String userId;

    @Column("user_name")
    @ColumnType
    @SerializedName("user_name")
    public String userName;

    @Column("user_tmp_lhr")
    @ColumnType
    @SerializedName("user_tmp_lhr")
    public String userTmpLhr;

    @Column("user_tgl_lhr")
    @ColumnType
    @SerializedName("user_tgl_lhr")
    public String userTglLhr;

    @Column("user_jabatan")
    @ColumnType
    @SerializedName("user_jabatan")
    public String userJabatan;

    @Column("user_level")
    @ColumnType
    @SerializedName("user_level")
    public String userLevel;

    @Column("user_picture")
    @ColumnType(type = "BLOB")
    @SerializedName("user_picture")
    public String userPicture;

    @Column("last_edit")
    @ColumnType(type = "INTEGER")
    @SerializedName("last_edit")
    public int lastEdit;

    @Column("suara")
    @ColumnType
    @SerializedName("suara")
    public String suara;
}
