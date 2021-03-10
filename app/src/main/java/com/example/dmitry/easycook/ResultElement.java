package com.example.dmitry.easycook;

public class ResultElement {
    public String mName = "";
    public long mCalories = 0;
    public String mLink = "";

    ResultElement(String name, long calories, String link) {
        this.mName = name;
        this.mCalories = calories;
        this.mLink = link;
    }
}
