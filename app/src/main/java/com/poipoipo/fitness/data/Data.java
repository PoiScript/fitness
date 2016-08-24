package com.poipoipo.fitness.data;

import java.util.List;

public class Data {

    private StatusBean status;

    private List<DataBean> data;

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class StatusBean {
        private String status;
        private int cout;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getCout() {
            return cout;
        }

        public void setCout(int cout) {
            this.cout = cout;
        }
    }

    public static class DataBean {
        private String gps;
        private String heartrate;
        private String spo2;
        private String timestamp;

        public String getGps() {
            return gps;
        }

        public void setGps(String gps) {
            this.gps = gps;
        }

        public String getHeartrate() {
            return heartrate;
        }

        public void setHeartrate(String heartrate) {
            this.heartrate = heartrate;
        }

        public String getSpo2() {
            return spo2;
        }

        public void setSpo2(String spo2) {
            this.spo2 = spo2;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
