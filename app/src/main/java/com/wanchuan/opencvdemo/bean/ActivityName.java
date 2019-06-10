package com.wanchuan.opencvdemo.bean;

public class ActivityName {

    private String name;
    private Class<?> tClass;
    private int type;

    public ActivityName(String name, Class<?> tClass, int type) {
        this.name = name;
        this.tClass = tClass;
        this.type = type;
    }

    public ActivityName(String name, Class<?> tClass) {
        this.name = name;
        this.tClass = tClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> gettClass() {
        return tClass;
    }

    public void settClass(Class<?> tClass) {
        this.tClass = tClass;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
