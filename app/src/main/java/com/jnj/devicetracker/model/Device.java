package com.jnj.devicetracker.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Device extends RealmObject {


    @PrimaryKey
    private long uid;


    private int id;
    private String device;
    private String os;
    private String manufacturer;
    private Date lastCheckedOutDate;
    private String lastCheckedOutBy;
    private boolean isCheckedOut;
    private boolean toBeUpdated;

    final static public String UID ="uid";
    final static public String DEVICE_ID ="id";
    final static public String DEVICE ="device";
    final static public String OS="os";
    final static public String MANUFACTURER="manufacturer";
    final static public String LASTCHECKEDOUTDATE="lastCheckedOutDate";
    final static public String LASTCHECKEDOUTBY="lastCheckedOutBy";
    final static public String ISCHECKEDOUT="isCheckedOut";
    final static public String TOBEUPDATED="toBeUpdated";


    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }



    public boolean isToBeUpdated() {
        return toBeUpdated;
    }

    public void setToBeUpdated(boolean toBeUpdated) {
        this.toBeUpdated = toBeUpdated;
    }


    public String getOS() {
        return os;
    }

    public void setOS(String os) {
        this.os = os;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }


    public Date getLastCheckedOutDate() {
        return lastCheckedOutDate;
    }

    public void setLastCheckedOutDate(Date lastCheckedOutDate) {
        this.lastCheckedOutDate = lastCheckedOutDate;
    }


    public String getLastCheckedOutBy() {
        return lastCheckedOutBy;
    }

    public void setLastCheckedOutBy(String lastCheckedOutBy) {
        this.lastCheckedOutBy = lastCheckedOutBy;
    }


    public boolean isCheckedOut() {
        return isCheckedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        isCheckedOut = checkedOut;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
