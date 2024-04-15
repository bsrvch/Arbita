package com.bsrvch.arbita.handler.impl.scanner;

import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.annotation.TextCommandType;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.dto.interactiveHandler.EmailDTO;
import com.bsrvch.arbita.dto.interactiveHandler.FilterDTO;
import com.bsrvch.arbita.encoder.InlineButtonDTOEncoder;
import com.bsrvch.arbita.exception.IllegalUserInputException;
import com.bsrvch.arbita.handler.InteractiveHandler;
import com.bsrvch.arbita.model.Filter;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.scanner.Market;
import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.service.ScannerService;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.state.email.EmailEvent;
import com.bsrvch.arbita.state.email.EmailState;
import com.bsrvch.arbita.state.filter.FilterEvent;
import com.bsrvch.arbita.state.filter.FilterState;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@InlineButtonType
@AllRoles
@Slf4j
public class ScannerFilterHandler implements InteractiveHandler {
    @Qualifier("filterSMF")
    private final StateMachineFactory<FilterState, FilterEvent> stateMachineFactory;
    private Cache<String, FilterDTO> filterCache = Caffeine.newBuilder().build();

    private final MessageMaker messageMaker;
    private final ScannerService scannerService;
    private final UserService userService;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        StateMachine<FilterState, FilterEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        FilterDTO dto = FilterDTO.builder().stateMachine(stateMachine).build();
        dto.setUser(user);
        filterCache.put(chatId.toString(), dto);
        stateMachine.sendEvent(FilterEvent.FILTER_START);
        return messageMaker.getFilterMessage(user, chatId, messageId);
    }

    @Override
    public List<PartialBotApiMethod<?>> update(Update update, User user) {
        if(update.hasCallbackQuery()){
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            FilterDTO dto = filterCache.getIfPresent(chatId.toString());
            if(dto != null){
                user = dto.getUser();
                StateMachine<FilterState, FilterEvent> stateMachine = dto.getStateMachine();
                String data = InlineButtonDTOEncoder.decode(update.getCallbackQuery().getData()).getData();
                if(stateMachine.getState().getId() == FilterState.FILTER_CHOOSE){
                    stateMachine.sendEvent(FilterEvent.FILTER_SEARCH);
                    scannerService.filterStart(user, data, dto);
                }else if (stateMachine.getState().getId() == FilterState.FILTER_SEARCH){
                     if(data.equals("|back|")) {
                        return handle(update, user);
                     }else {
                         scannerService.filter(user, data, dto);
                     }
                }
                return messageMaker.getFilterSearch(chatId, messageId, dto);
            }
        }else if(update.hasMessage()){
            Long chatId = update.getMessage().getChatId();
            Integer messageId = user.getBotMessageId();
            FilterDTO dto = filterCache.getIfPresent(chatId.toString());
            if(dto!=null && update.getMessage().hasText()){
                user = dto.getUser();
                if(dto.getStateMachine().getState().getId() == FilterState.FILTER_SEARCH){
                    scannerService.filterSearch(dto, update.getMessage().getText());
                    return messageMaker.getFilterSearch(chatId, messageId, dto);
                }
            }
        }
        return null;
    }

    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.SCANNER_FILTER;
    }

    @Override
    public void removeFromCacheBy(String id) {
        if (filterCache.getIfPresent(id) != null){
            userService.save(filterCache.getIfPresent(id).getUser());
            filterCache.invalidate(id);
        }
    }

    @Override
    public boolean hasFinished(String id) {
        var dto = filterCache.getIfPresent(id);

        boolean result = true;

        if (dto != null) {
            result = dto.getStateMachine().isComplete();
        }

        return result;
    }

    @Override
    public int getCurrentStateIndex(String id) {
        var dto = filterCache.getIfPresent(id);

        int result = -1;

        if (dto != null) {
            result = dto.getStateMachine().getState().getId().getIndex();
        }

        return result;
    }
}
