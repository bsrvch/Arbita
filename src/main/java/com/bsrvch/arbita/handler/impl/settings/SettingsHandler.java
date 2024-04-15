package com.bsrvch.arbita.handler.impl.settings;

import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.annotation.LiteRole;
import com.bsrvch.arbita.annotation.TextCommandType;
import com.bsrvch.arbita.component.InlineKeyboardMaker;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.component.ReplyKeyboardMaker;
import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.handler.InteractiveHandler;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import com.bsrvch.arbita.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
@Component
@InlineButtonType
@RequiredArgsConstructor
@AllRoles
public class SettingsHandler implements Handler {
    private final MessageMaker messageMaker;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        return messageMaker.getSettingsMessage(user,chatId,messageId);
    }

    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.MENU_SETTINGS;
    }
}
