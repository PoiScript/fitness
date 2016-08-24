package com.poipoipo.fitness.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Random Para Data Generator
 * Use for Testing and Debugging
 */

public class ParaGenerator {
    private static final String TAG = "ParaGenerator";
    private static final int DATA_MIN = 100;
    private static final int DATA_MAX = 300;
    private static final int TIME_MIN = 1471276800; // 8-16-2016
    private static final int TIME_MAX = TIME_MIN + 24 * 60 * 60; // 8-17-2016

    private List<Para> list = new ArrayList<>();
    private Random random = new Random();

    public List<Para> generate(int length){
        list.clear();
        for (int i = 0; i < length; i++){
            Para para = new Para(random.nextInt(3));
            para.setData(random.nextInt(DATA_MAX - DATA_MIN + 1) + DATA_MIN);
            para.setTime(random.nextInt(TIME_MAX - TIME_MIN + 1) + TIME_MIN);
            list.add(para);
        }
        return list;
    }
}
