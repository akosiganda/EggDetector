package com.example.eggdetector.Helper;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Transaction extends RealmObject {

    private String type;
    private Date date;
    private  int count;
    @PrimaryKey
    private long id;

    public Transaction(){

    }

    public Transaction(String type, Date date, int count, long id) {
        this.type = type;
        this.date = date;
        this.count = count;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
