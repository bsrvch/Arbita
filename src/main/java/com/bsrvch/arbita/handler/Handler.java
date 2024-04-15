package com.bsrvch.arbita.handler;

import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface Handler {

    List<PartialBotApiMethod<?>> handle(Update update, User user);


    Command getCommandObject();
}