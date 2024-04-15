package com.bsrvch.arbita.constant;

import com.bsrvch.arbita.statics.locale.StaticLocale;

public enum InlineButtonCommand implements Command{

    MAIN_MENU("/menu","Menu"),
    MENU_PROFILE("/menu_profile","Profile"),
    MENU_SUBSCRIPTION("/menu_subscription","Subscription"),
    MENU_PAYMENT("/menu_payment","Payment"),
    GET_EMAIL("get_email","Get email"),
    MENU_SCANNER("/menu_scanner","Scanner"),
    START_SCANNER("/start_scanner","Start scanner"),
    SCANNER_INFO("/scanner_info","Scanner info"),
    SCANNER_FILTER("/scanner_filter","Scanner filter"),
    ADD_LISTEN("/add_listen","Add listen"),
    REMOVE_LISTEN("/remove_listen","Remove listen"),
    SHOW_VIP("/show_vip", "Show vip"),
    MENU_TRACKING("/menu_tracking","Tracking"),
    MENU_SETTINGS("/menu_settings","Settings"),
    MENU_LOCALE("/menu_locale", "Locale"),

    NO_ACTION("/no_action", "No action"),







    SELECT_LOCALE("/selectLocale", "Выбрать"),
    YES("/yes", "Подробнее"),
    GO("","...");

    private final String command;
    private final String description;

    InlineButtonCommand(String command, String description) {
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
