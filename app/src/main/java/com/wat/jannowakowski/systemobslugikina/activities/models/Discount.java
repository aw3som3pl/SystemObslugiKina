package com.wat.jannowakowski.systemobslugikina.activities.models;

/**
 * Created by Jan Nowakowski on 27.01.2019.
 */

public class Discount {

    private double discountModifier;
    private String discountName;

    public Discount(double discountModifier, String discountName) {
        this.discountModifier = discountModifier;
        this.discountName = discountName;
    }

    public double getDiscountModifier() {
        return discountModifier;
    }

    public void setDiscountModifier(double discountModifier) {
        this.discountModifier = discountModifier;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }




}
