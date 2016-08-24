package com.poipoipo.fitness.data;

public class Para {
    public static final int TYPE_BPM = 0;
    public static final int TYPE_SPO2 = 1;
    private int time;
    private int data;
    private int type;

    public Para(int type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }
}
