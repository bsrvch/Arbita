package com.bsrvch.arbita.handler.impl.profile;

import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@InlineButtonType
@RequiredArgsConstructor
@AllRoles
public class SubscriptionMenuHandler implements Handler {
    private final SubscriptionService subscriptionService;
    private final MessageMaker messageMaker;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        return messageMaker.getSubscriptionMessage(user,chatId,messageId,subscriptionService.getAllSub());
    }

    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.MENU_SUBSCRIPTION;
    }
}
