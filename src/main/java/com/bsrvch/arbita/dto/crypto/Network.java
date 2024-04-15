package com.bsrvch.arbita.dto.crypto;

public class Network {
    private String name;
    private float fee;
    private int confirms;

    public Network(String name, float fee, int confirms) {
        this.name = name;
        this.fee = fee;
        this.confirms = confirms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public int getConfirms() {
        return confirms;
    }

    public void setConfirms(int confirms) {
        this.confirms = confirms;
    }
}
