package com.eschedule.eclient.lu;


public class selected {
    public String getNama() {
        return nama;
    }

    public String getUid() {
        return uid;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String nama, uid;

    public selected(String nama, String uid) {
        this.nama  = nama;
        this.uid = uid;
    }
}
