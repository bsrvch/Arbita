package com.bsrvch.arbita.constant;

public enum ButtonCommand implements Command{
    START("/start", "Start"),
    MENU_PAYMENT("/menu_payment","Payment"),
    START_CHECK("/startch", "Checker"),
    TEST("/test","TEST"),
    PAY("/pay","Pay");
    private final String command;
    private final String description;
    ButtonCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
