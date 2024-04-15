package com.bsrvch.arbita.handler.impl.scanner;

import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@InlineButtonType
@AllRoles
@Slf4j
public class ScannerInfoHandler implements Handler {
    private final MessageMaker messageMaker;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Scanner.userInWork.remove(String.valueOf(chatId));
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        return messageMaker.getInfoMessage(user,chatId,messageId);
    }

    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.SCANNER_INFO;
    }
}
