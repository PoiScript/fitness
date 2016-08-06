package com.poipoipo.fitness.httpConnect;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}