package com.wanchuan.opencvdemo.bean;

public class ActivityName {

    private String name;
    private Class<?> tClass;

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
}
