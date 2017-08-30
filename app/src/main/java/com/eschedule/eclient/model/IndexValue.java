package com.eschedule.eclient.model;

/**
 * Created by Teguh on 7/31/2015.
 */
public class IndexValue {
    String key;
    String value;

    public IndexValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
