package com.bsrvch.arbita.component;


import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.util.builder.InlineKeyboardBuilder;
import com.bsrvch.arbita.util.builder.ReplyKeyboardBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ReplyKeyboardMaker {



    private ReplyKeyboardMarkup getReplyKeyboardMarkup(List<KeyboardRow> keyboardRows) {
        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboardRows)
                .selective(false)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }


}
