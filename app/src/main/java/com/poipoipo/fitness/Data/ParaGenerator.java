package com.poipoipo.fitness.Data;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Random Para Data Generator
 * Use for Testing and Debugging
 */

public class ParaGenerator {

    private static final int DATA_MIN = 100;
    private static final int DATA_MAX = 300;
    private static final int TIME_MIN = 1467331200; // 7-1-2016
    private static final int TIME_MAX = 1475193600; // 9-30-2016

    private int length;
    private List<Para> list = new ArrayList<>();
    private Random random = new Random();

    public ParaGenerator (int length) {
        this.length = length;
    }

    public List<Para> create(){
        for (int i = 0; i < length; i++){
            Para para = new Para();
            para.setData(random.nextInt(DATA_MAX - DATA_MIN + 1) + DATA_MIN);
            para.setTime(random.nextInt(TIME_MAX - TIME_MIN + 1) + TIME_MIN);
            para.setType(random.nextInt(2) + 1);
            list.add(para);
        }
        return list;
    }
}
