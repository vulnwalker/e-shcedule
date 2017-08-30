package com.eschedule.eclient.model;

import java.util.ArrayList;

/**
 * Created by Zund#i on 21/10/2016.
 */

public class Data {

    public static class ResponUser {
        public boolean success;
        public String msg;
        public ArrayList<User> rows;
    }

    public static class ResponLoad {
        public boolean success;
        public ArrayList<Jadwal> jadwal;
        public ArrayList<Informasi> informasi;
    }
}
