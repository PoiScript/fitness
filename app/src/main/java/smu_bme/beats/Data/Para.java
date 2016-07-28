package smu_bme.beats.Data;

public class Para {
    private int date;
    private int data;
    private int type;

    public static final int TYPE_BPM = 1;
    public static final int TYPE_PACE = 2;
    public static final int TYPE_SPO2 = 3;

    public int getDate() {
        return date;
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

    public void setDate(int date) {
        this.date = date;
    }

    public void setData(int data) {
        this.data = data;
    }
}
