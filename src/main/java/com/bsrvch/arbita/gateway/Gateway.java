package com.bsrvch.arbita.gateway;


import com.bsrvch.arbita.exception.NoSuchUserException;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import com.bsrvch.arbita.resolver.Resolver;
import com.bsrvch.arbita.service.SubscriptionService;
import com.bsrvch.arbita.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Gateway {

    private final UserService userService;

    private final Map<UserRole, Resolver> resolvers;


    public Gateway(UserService userService, List<Resolver> resolvers) {
        this.resolvers = resolvers.stream().collect(Collectors.toMap(
                Resolver::getResolverUserRole, Function.identity()
        ));
        this.userService = userService;
    }
    public List<PartialBotApiMethod<?>> processUpdate(Update update) {
        Message message = null;
        if(update.hasMessage()){
            message = update.getMessage();
        } else if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        } else return null;
        User user = userService.getUserByChatId(message.getChatId());
        if(user==null){
            user = userService.createUser(
                    message.getFrom().getId(),
                    message.getFrom().getFirstName(),
                    message.getFrom().getLastName(),
                    message.getFrom().getUserName()
            );
        }
        user = userService.checkSubscription(user);
        return resolvers.get(user.getRole()).resolve(update, user);

//
//
//
//
//        if (update.hasMessage()) {
//            try {
//                User user = userService.getUserByChatId(update.getMessage().getChatId());
//                user = userService.checkSubscription(user);
//                UserRole userRole = user.getRole();
//                reply = resolvers.get(userRole).resolve(update, user);
//            } catch (Exception exception) {
//                reply = resolvers.get(UserRole.LITE).resolve(update, null);
//            }
//
//        } else if(update.hasCallbackQuery()) {
//            User user = userService.getUserByTelegramId(update.getCallbackQuery().getMessage().getChatId());
//            if(user==null) userService.createUser(
//                    update.getCallbackQuery().getMessage().getFrom().getId(),
//                    update.getCallbackQuery().getMessage().getFrom().getFirstName(),
//                    update.getCallbackQuery().getMessage().getFrom().getLastName(),
//                    update.getCallbackQuery().getMessage().getFrom().getUserName()
//            );
//            user = userService.checkSubscription(user);
//            UserRole userRole = user.getRole();
//            reply = resolvers.get(userRole).resolve(update, user);
//        }
//
//        return reply;
    }


}
