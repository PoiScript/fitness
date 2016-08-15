package com.poipoipo.fitness.data;

public class Para {
    private int time;
    private int data;
    private int type;

    public static final int TYPE_BPM = 0;
    public static final int TYPE_TEMP = 1;
    public static final int TYPE_SPO2 = 2;

    public int getTime() {
        return time;
    }

    public int getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setData(int data) {
        this.data = data;
    }
}
