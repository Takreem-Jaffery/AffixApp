package com.example.affixapp.Weka;

import weka.core.Instance;

public class TestUser {
    private int id;
    private double age;
    private String gender;
    private double income;
    private String interests;

    public TestUser(double age, String gender, double income, String interests) {
        this.age = age;
        this.gender = gender;
        this.income = income;
        this.interests = interests;
    }

    public TestUser(Instance instance) {
        // Populate user attributes from the Weka Instance
        this.age = instance.value(0); // Assuming age is the first attribute
        this.gender = instance.stringValue(1); // Assuming gender is the second attribute
        this.income = instance.value(2); // Assuming income is the third attribute
        this.interests = instance.stringValue(3); // Assuming interests is the fourth attribute
    }

    public int getId() {
        return id;
    }
}
