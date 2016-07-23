package smu_bme.beats.HttpConnect;

/**
 * Created by alex on 7/23/2016.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
