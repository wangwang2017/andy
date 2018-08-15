package com.yuyuehao.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Wang
 * on 2017-09-20
 */

public class LogData {
    private long timestamp;
    private String record;
    private String send_reg_time;
    private String log_type;
    private String log_send_type;
    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private String data7;
    private UUID log_uid;



    public LogData(String log_type, String log_send_type , String data3, String data4) {
        this.timestamp = System.currentTimeMillis();
        this.record = "thirteen.log_cir_2";
        this.send_reg_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.log_type = log_type;
        this.log_send_type = log_send_type;
        this.data1 = "undefined";
        this.data2 = "undefined";
        this.data3 = data3;
        this.data4 = data4;
        this.data5 = "undefined";
        this.data6 = "undefined";
        this.data7 = "undefined";
        this.log_uid = UUID.randomUUID();

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public void setSend_reg_time(String send_reg_time) {
        this.send_reg_time = send_reg_time;
    }


    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    public void setLog_send_type(String log_send_type) {
        this.log_send_type = log_send_type;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

    public void setData6(String data6) {
        this.data6 = data6;
    }

    public void setData7(String data7) {
        this.data7 = data7;
    }

    public String getRecord() {
        return record;
    }

    public String getSend_reg_time() {
        return send_reg_time;
    }

    public String getLog_type() {
        return log_type;
    }

    public String getLog_send_type() {
        return log_send_type;
    }

    public String getData1() {
        return data1;
    }

    public String getData2() {
        return data2;
    }

    public String getData3() {
        return data3;
    }

    public String getData4() {
        return data4;
    }

    public String getData5() {
        return data5;
    }

    public String getData6() {
        return data6;
    }

    public String getData7() {
        return data7;
    }

    public UUID getLog_uid() {
        return log_uid;
    }

    public void setLog_uid(UUID log_uid) {
        this.log_uid = log_uid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"record\":\"")
                .append(record).append('\"');
        sb.append(",\"send_reg_time\":\"")
                .append(send_reg_time).append('\"');
        sb.append(",\"log_type\":\"")
                .append(log_type).append('\"');
        sb.append(",\"log_send_type\":\"")
                .append(log_send_type).append('\"');
        sb.append(",\"data1\":\"")
                .append(data1).append('\"');
        sb.append(",\"data2\":\"")
                .append(data2).append('\"');
        sb.append(",\"data3\":\"")
                .append(data3).append('\"');
        sb.append(",\"data4\":\"")
                .append(data4).append('\"');
        sb.append(",\"data5\":\"")
                .append(data5).append('\"');
        sb.append(",\"data6\":\"")
                .append(data6).append('\"');
        sb.append(",\"data7\":\"")
                .append(data7).append('\"');
        sb.append(",\"log_uid\":")
                .append(log_uid);
        sb.append('}');
        return sb.toString();
    }



}
