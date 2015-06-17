package com.swmem.voicetoview.util;

import com.swmem.voicetoview.broadcastreceiver.PhoneStateReceiver.LogKind;

public class CallLog extends Log {
    private String ringingDate;
    private String startDate;
    private String endDate;
     
    public CallLog(String number, LogKind kind){
        super(number, kind);
    }
     
    public void setRingingDate(long time) {
        this.ringingDate = getTimeString(time);
    }
 
    public void setStartDate(long time) {
        this.startDate = getTimeString(time);
    }
 
    public void setEndDate(long time) {
        this.endDate = getTimeString(time);
    }
 
    public String getRingingDate() {
        return ringingDate;
    }
     
    public String getStartDate() {
        return startDate;
    }
     
    public String getEndDate() {
        return endDate;
    }
     
    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(getKind()).append(" / ")
            .append(getNumber()).append(" / ")
            .append(getId()).append(" / ")
            .append(getRingingDate()).append(" / ")
            .append(getStartDate()).append(" / ")
            .append(getEndDate());
         
        return buffer.toString();
    }
}