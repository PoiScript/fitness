package smu_bme.beats.HttpConnect;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}