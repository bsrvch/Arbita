package com.bsrvch.arbita.util.builder;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public final class ReplyKeyboardBuilder {
    private final List<KeyboardRow> keyboardRows;
    private int buildableRow = -1;
    private ReplyKeyboardBuilder() {
        keyboardRows = new ArrayList<>();
    }

    public static ReplyKeyboardBuilder instance() {
        return new ReplyKeyboardBuilder();
    }
    public ReplyKeyboardBuilder row() {
        keyboardRows.add(new KeyboardRow());
        buildableRow++;
        return this;
    }
    public ReplyKeyboardBuilder button(String text) {
        return button(new KeyboardButton(text));
    }
    public ReplyKeyboardBuilder button(KeyboardButton button) {
        if (keyboardRows.isEmpty()) {
            row();
        }

        keyboardRows.get(buildableRow).add(button);

        return this;
    }

    public ReplyKeyboardMarkup build() {
        return new ReplyKeyboardMarkup(keyboardRows);
    }
}
