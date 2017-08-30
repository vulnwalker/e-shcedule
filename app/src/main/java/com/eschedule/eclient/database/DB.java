package com.eschedule.eclient.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eschedule.eclient.core.Util;
import com.eschedule.eclient.model.Alarm;
import com.eschedule.eclient.model.Informasi;
import com.eschedule.eclient.model.InnerInfo;
import com.eschedule.eclient.model.Jadwal;
import com.eschedule.eclient.model.User;

import org.chalup.microorm.MicroOrm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zund#i on 20/10/2016.
 */

public class DB extends SQLiteOpenHelper {

    public static final String DB_NAME = "eclient";
    public static final int DB_VERSION = 1;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Util.getSQL(User.TABLE, User.class));
        db.execSQL(Util.getSQL(Jadwal.TABLE, Jadwal.class));
        db.execSQL(Util.getSQL(Informasi.TABLE, Informasi.class));
        db.execSQL(Util.getSQL(Alarm.TABLE, Alarm.class));
        db.execSQL(Util.getSQL(InnerInfo.TABLE, InnerInfo.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    // Users
    public void manageUser(User item, byte[] userPicture, boolean edit) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.beginTransaction();

        try {
            ContentValues val = new MicroOrm().toContentValues(item);
            val.put("user_picture", userPicture);

            if(!edit) {
                db.insert(User.TABLE, null, val);
            } else {
                db.update(User.TABLE, val, "user_id = ?", new String[] {item.userId});
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DBManager.getInstance().closeDatabase();
        }
    }

    public void manageUser(User item, byte[] userPicture) {
        manageUser(item, userPicture, false);
    }

    public User getUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+User.TABLE+" limit 1 ", null);
        List<User> item =  new MicroOrm().listFromCursor(c, User.class);

        db.close();
        c.close();
        return item.get(0);
    }

    // Informasi
    public void manageInformasi(Informasi item, boolean edit) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.beginTransaction();

        try {
            ContentValues val = new MicroOrm().toContentValues(item);
            if(!edit) {
                db.replace(Informasi.TABLE, null, val);
            } else {
                db.update(Informasi.TABLE, val, "informasi_id = ?", new String[] {item.informasiID});
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DBManager.getInstance().closeDatabase();
        }
    }

    public void manageInformasi(Informasi item) {
        manageInformasi(item, false);
    }

    public List<Informasi> getInformasi(String id) {
        String wh = id.isEmpty() ? " tampilkan = 1 " : "informasi_id = '"+id+"'";
        wh = " where "+wh;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+Informasi.TABLE+" "+wh+" order by informasi_id desc, date(fl_tgl_mulai) desc", null);
        List<Informasi> item =  new MicroOrm().listFromCursor(c, Informasi.class);

        db.close();
        c.close();
        return item;
    }

    public List<Informasi> getInformasi() {
        return getInformasi("");
    }

    public List<Informasi> getRalat(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+Informasi.TABLE+" where (informasi_id='"+id+"' " +
                "or informasi_parent_id= '"+id+"') and informasi_id <> '"+id+"' ", null);
        List<Informasi> item =  new MicroOrm().listFromCursor(c, Informasi.class);

        db.close();
        c.close();
        return item;
    }

    public void deleteInformasi(Informasi item) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete(Informasi.TABLE, "informasi_id = ?", new String[] {item.informasiID});
        DBManager.getInstance().closeDatabase();
    }

    // Jadwal
    public void manageJadwal(Jadwal item, boolean edit) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.beginTransaction();

        try {
            ContentValues val = new MicroOrm().toContentValues(item);
            if(!edit) {
                db.insert(Jadwal.TABLE, null, val);
            } else {
                db.update(Jadwal.TABLE, val, "jadwal_id = ?", new String[] {item.jadwalID});
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DBManager.getInstance().closeDatabase();
        }
    }

    public void manageJadwal(Jadwal item) {
        manageJadwal(item, false);
    }

    public List<Jadwal> getJadwal() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+Jadwal.TABLE+" order by  jadwal_id desc, date(tgl_mulai) desc", null);
        List<Jadwal> item =  new MicroOrm().listFromCursor(c, Jadwal.class);

        db.close();
        c.close();
        return item;
    }

    public void deleteJadwal(Jadwal item) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete(Jadwal.TABLE, "jadwal_id = ?", new String[] {item.jadwalID});
        DBManager.getInstance().closeDatabase();
    }

    // Delete ALL
    public void deleteAll() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete(Jadwal.TABLE, null, null);
        db.delete(Informasi.TABLE, null, null);
        DBManager.getInstance().closeDatabase();
    }
}
