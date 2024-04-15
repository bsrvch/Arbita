package com.bsrvch.arbita.handler.impl.profile;


import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.annotation.LiteRole;
import com.bsrvch.arbita.annotation.TextCommandType;
import com.bsrvch.arbita.component.InlineKeyboardMaker;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.component.ReplyKeyboardMaker;
import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.initializer.BotInit;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.statics.ramDB.UserRamDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Component
@InlineButtonType
@RequiredArgsConstructor
@AllRoles
public class ProfileHandler implements Handler {
    private final UserService userService;
    private final MessageMaker messageMaker;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        return messageMaker.getProfileMessage(user,chatId,messageId);
    }

    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.MENU_PROFILE;
    }
}
