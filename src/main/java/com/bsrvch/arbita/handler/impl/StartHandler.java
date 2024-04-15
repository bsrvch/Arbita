package com.bsrvch.arbita.handler.impl;


import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.TextCommandType;
import com.bsrvch.arbita.component.InlineKeyboardMaker;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.initializer.BotInit;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.statics.locale.StaticLocale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@TextCommandType
@RequiredArgsConstructor
@AllRoles
public class StartHandler implements Handler {

    private final MessageMaker messageMaker;
    private final InlineKeyboardMaker inlineKeyboardMaker;
    private final UserService userService;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
//        if (user == null) {
//            //
//
//
//            String userFirstName = "";
//            String userLastName = "";
//            String userName = "";
//            userFirstName = update.getMessage().getFrom().getFirstName();
//            if(update.getMessage().getFrom().getLastName() != null){
//                userLastName = update.getMessage().getFrom().getLastName();
//            }
//            if(update.getMessage().getFrom().getUserName() != null){
//                userName = update.getMessage().getFrom().getUserName();
//            }else{
//                userName = userFirstName;
//            }
//            user = userService.createUser(
//                    update.getMessage().getFrom().getId(),
//                    userFirstName,
//                    userLastName,
//                    userName
//            );
//
//
//            Long chatId = update.getMessage().getChatId();
//            URL path = getClass().getClassLoader().getResource("img/menu.jpg");
//            res.add(SendPhoto.builder()
//                    .chatId(chatId)
//                    .photo(new InputFile(new File(path.getPath())))
//                    .caption(StaticLocale.getGreeting())
//                    .replyMarkup(inlineKeyboardMaker.getMenu())
//                    .build());
//            return res;
//        }

//        else if(user.getBotMessageId()!=null){
//            return messageMaker.getMenuMessage(null, update.getMessage().getFrom().getId(), user.getBotMessageId());
//        }
        ///???

        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId();
        URL path = getClass().getClassLoader().getResource("img/menu.jpg");
        try{
            BotInit.bot.execute(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId-1)
                    .build()
            );
        }catch (Exception ignored) {}
        try {
            messageId = BotInit.bot.execute(SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(new File(path.getPath())))
                    .caption(StaticLocale.getGreeting())
                    .replyMarkup(inlineKeyboardMaker.getMenu())
                    .build()
            ).getMessageId();
            userService.setMessageId(user, messageId);
        }catch (Exception ignored){}
        return null;
    }

    @Override
    public Command getCommandObject() {
        return ButtonCommand.START;
    }
}
