package smu_bme.beats;

public class Para {
    private String date;
    private String time;
    private int BPM;
    private int pace;
    private int avgBPM;
    /** For visualization and input*/
    public Para(String date, String time, int BPM){
        this.date = date;
        this.time=time;
        this.BPM = BPM;
    }
    /** For output on the very date*/
    public Para(String date, int avgBPM, int pace){
        this.date=date;
        this.avgBPM=avgBPM;
        this.pace=pace;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBPM(int BPM) {

        this.BPM = BPM;
    }

    public int getPace() {
        return pace;
    }

    public String getDate() {
        return date;
    }

    public int getBPM() {
        return BPM;
    }

    public String getTime() {
        return time;
    }
        public int getAvgBPM() {
        return avgBPM;
    }
    //    public String getDate() {
//        return date;
//    }
//
//    public String getTime() {
//        return time;
//    }
}
