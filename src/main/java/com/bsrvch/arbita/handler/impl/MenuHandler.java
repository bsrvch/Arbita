package com.bsrvch.arbita.handler.impl;

import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.annotation.LiteRole;
import com.bsrvch.arbita.component.InlineKeyboardMaker;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;


import java.io.File;
import java.net.URL;
import java.util.List;
@Component
@InlineButtonType
@RequiredArgsConstructor
@AllRoles
public class MenuHandler implements Handler {
    private final MessageMaker messageMaker;

    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        return messageMaker.getMenuMessage(null, chatId, messageId);
    }

    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.MAIN_MENU;
    }
}
