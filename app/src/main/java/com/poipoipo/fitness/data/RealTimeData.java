package com.poipoipo.fitness.data;

import java.util.List;

public class RealTimeData {
    private List<GpsBean> gps;
    private List<Spo2Bean> spo2;
    private List<HeartrateBean> heartrate;

    public List<GpsBean> getGps() {
        return gps;
    }

    public List<Spo2Bean> getSpo2() {
        return spo2;
    }

    public List<HeartrateBean> getHeartrate() {
        return heartrate;
    }

    public static class GpsBean {
        private String gps;
        private String timestamp;

        public String getGps() {
            return gps;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public static class Spo2Bean {
        private String spo2;
        private String timestamp;

        public String getSpo2() {
            return spo2;
        }

        public void setSpo2(String spo2) {
            this.spo2 = spo2;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public static class HeartrateBean {
        private String heartrate;
        private String timestamp;

        public String getHeartrate() {
            return heartrate;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
