package com.bsrvch.arbita.resolver;

import com.bsrvch.arbita.cache.CacheData;
import com.bsrvch.arbita.cache.CacheManager;
import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.dto.InlineButtonDTO;
import com.bsrvch.arbita.encoder.InlineButtonDTOEncoder;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.handler.InteractiveHandler;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class Resolver {
    private final CacheManager cacheManager;
    private final Map<Command, Handler> textCommandHandlers;
    private final Map<String, Handler> inlineButtonsHandlers;



    public Resolver(List<Handler> textCommandHandlers, List<Handler> inlineButtonsHandlers){
        this.textCommandHandlers = textCommandHandlers
                .stream()
                .collect(Collectors.toMap(
                        Handler::getCommandObject, Function.identity()
                ));
        this.inlineButtonsHandlers = inlineButtonsHandlers
                .stream()
                .collect(Collectors.toMap(
                        (handler -> handler.getCommandObject().getCommand()),Function.identity()
                ));
        this.cacheManager = new CacheManager();
    }
    private Handler getHandler(String command){
        for (Command botCommand : textCommandHandlers.keySet()) {
            if (botCommand.getCommand().equals(command) || botCommand.getDescription().equals(command)) {
                return textCommandHandlers.get(botCommand);
            }
        }
        return null;
    }
    public List<PartialBotApiMethod<?>> resolve(Update update, User user){
        List<PartialBotApiMethod<?>> reply = new ArrayList<>();
        if(update.hasMessage() && update.getMessage().hasText()){
            String command = update.getMessage().getText();
            if(command.contains("/start ")){
                command = command.replace("/start ","");
            }
            Handler handler = getHandler(command);
            if(handler == null){
                CacheData commandData = cacheManager.getIfPresent(update.getMessage().getFrom().getId().toString());
                if(commandData != null){
                    reply = commandData.updateData(update, user);
                    cacheManager.triggerTimeBasedEvictionChecker(update.getMessage().getFrom().getId().toString());
                } else {
                    reply = null;
                }
            } else if (handler instanceof InteractiveHandler interactiveHandler) {
                CacheData commandData = new CacheData(interactiveHandler);
                cacheManager.cache(update.getMessage().getFrom().getId().toString(),commandData);
                reply = commandData.handleData(update, user);
                cacheManager.triggerTimeBasedEvictionChecker(update.getMessage().getFrom().getId().toString());
            } else reply = handler.handle(update, user);
        }else if (update.hasCallbackQuery()) {
            CacheData cacheData = cacheManager.getIfPresent(update.getCallbackQuery().getFrom().getId().toString());
            InlineButtonDTO inlineButtonDTO = InlineButtonDTOEncoder.decode(update.getCallbackQuery().getData());
            if (cacheData != null && cacheData.getHandler().getCommandObject().getCommand().equals(inlineButtonDTO.getCommand())) {
                    reply = cacheData.updateData(update, user);
                    cacheManager.triggerTimeBasedEvictionChecker(update.getCallbackQuery().getFrom().getId().toString());
            } else {
                if(cacheData != null && !inlineButtonDTO.getCommand().equals(InlineButtonCommand.NO_ACTION.getCommand())){
                    cacheData.getHandler().removeFromCacheBy(user.getTelegramId().toString());
                    cacheManager.removeInCache(user.getTelegramId().toString());//??????????
                    cacheData.setHasFinished(true);
                    System.out.println("delete");
                }
                InlineButtonDTO buttonData = InlineButtonDTOEncoder.decode(update.getCallbackQuery().getData());
                Handler handler = inlineButtonsHandlers.get(buttonData.getCommand());
                if (handler != null) {
                    if (handler instanceof InteractiveHandler interactiveHandler) {
                        CacheData commandData = new CacheData(interactiveHandler);
                        cacheManager.cache(update.getCallbackQuery().getFrom().getId().toString(), commandData);
                        reply = commandData.handleData(update, user);
                        cacheManager.triggerTimeBasedEvictionChecker(update.getCallbackQuery().getFrom().getId().toString());
                    } else {
                        reply = handler.handle(update, user);
                    }
                }
            }
        }
        return reply;
    }
    public abstract UserRole getResolverUserRole();
}
