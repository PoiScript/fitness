package com.poipoipo.fitness.HttpConnect;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}