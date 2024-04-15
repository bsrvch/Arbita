package com.bsrvch.arbita.model.dictionary;

public enum UserRole {
    LITE("lite"),
    VIP("vip"),
    ADMIN("admin");
    private final String string;

    UserRole(String string){
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
