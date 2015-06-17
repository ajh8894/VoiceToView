package com.swmem.voicetoview.util;

import android.text.format.DateFormat;

import com.swmem.voicetoview.broadcastreceiver.PhoneStateReceiver.LogKind;

public class Log {
    private long id;
    private String number;
    private LogKind kind;
     
    public Log(String number, LogKind kind){
        this.number = number;
        this.kind = kind;
    }
 
    public void setId(long id) {
        this.id = id;
    }
     
    public long getId() {
        return id;
    }
     
    public String getNumber() {
        return number;
    }
     
    public LogKind getKind() {
        return kind;
    }
     
    protected String getTimeString(long time){
        return DateFormat.format("yyyy-mm-dd hh:mm:ss",time).toString();
    }
}