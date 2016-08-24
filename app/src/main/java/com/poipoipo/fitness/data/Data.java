package com.poipoipo.fitness.data;

import java.util.List;

public class Data {
    private List<DataBean> data;
    private StatusBean status;


    public List<DataBean> getData() {
        return data;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public static class DataBean {
        private String gps;
        private String heartrate;
        private String spo2;
        private String timestamp;

        public String getGps() {
            return gps;
        }

        public String getHeartrate() {
            return heartrate;
        }

        public String getSpo2() {
            return spo2;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public static class StatusBean {
        private int cout;

        public int getCout() {
            return cout;
        }

        public void setCout(int cout) {
            this.cout = cout;
        }
    }
}
