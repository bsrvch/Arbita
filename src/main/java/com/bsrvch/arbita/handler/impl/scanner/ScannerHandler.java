package com.bsrvch.arbita.handler.impl.scanner;

import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.dto.interactiveHandler.EmailDTO;
import com.bsrvch.arbita.dto.interactiveHandler.ScannerDTO;
import com.bsrvch.arbita.encoder.InlineButtonDTOEncoder;
import com.bsrvch.arbita.exception.IllegalUserInputException;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.handler.InteractiveHandler;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.service.ScannerService;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.state.email.EmailEvent;
import com.bsrvch.arbita.state.email.EmailState;
import com.bsrvch.arbita.state.scanner.ScannerEvent;
import com.bsrvch.arbita.state.scanner.ScannerState;
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

import java.util.ArrayList;
import java.util.List;

import static com.bsrvch.arbita.scanner.Scanner.bundlesList;

@Component
@RequiredArgsConstructor
@InlineButtonType
@AllRoles
@Slf4j
public class ScannerHandler implements InteractiveHandler {

    @Qualifier("scannerSMF")
    private final StateMachineFactory<ScannerState, ScannerEvent> stateMachineFactory;
    private Cache<String, ScannerDTO> scannerCache = Caffeine.newBuilder().build();
    private final UserService userService;
    private final ScannerService scannerService;
    private final MessageMaker messageMaker;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        userService.setMessageId(user,messageId);
        StateMachine<ScannerState, ScannerEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        ScannerDTO dto = ScannerDTO.builder().stateMachine(stateMachine).build();
        dto.setUser(user);
        dto.setShowAll(false);
        dto.setShowListen(false);
        dto.setLastIndex(0);
        dto.setShowVip(user.getUserSettings().isShowVip());
        scannerCache.put(chatId.toString(), dto);
        stateMachine.sendEvent(ScannerEvent.SCANNER_START);
        Scanner.userInWork.put(String.valueOf(user.getTelegramId()),dto);
        return messageMaker.getScanner(dto, chatId, messageId);
    }
    @Override
    public List<PartialBotApiMethod<?>> update(Update update, User user) {
        if(update.hasMessage()){
            Long chatId = update.getMessage().getChatId();
            ScannerDTO dto = scannerCache.getIfPresent(chatId.toString());
            if(dto!=null){
                if(dto.getStateMachine().getState().getId() == ScannerState.SCANNER_START){
                    dto.setSearchText(update.getMessage().getText().toUpperCase());
                    dto.setShowAll(true);
                    dto.setLastIndex(0);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            ScannerDTO dto = scannerCache.getIfPresent(chatId.toString());
            if(dto!=null){

                String data = InlineButtonDTOEncoder.decode(update.getCallbackQuery().getData()).getData();
                if (data.contains("|listen|")){
                    dto.setSearchText("");
                   dto.setShowListen(!dto.isShowListen());
                } else if(data.contains("|addlisten|")){
                    data = data.replace("|addlisten|","");
                    dto.setSearchText("");
                    scannerService.addListen(
                            data,
                            update.getCallbackQuery().getMessage().getChatId()
                    );
                } else if(data.contains("|removelisten|")){
                    data = data.replace("|removelisten|","");
                    dto.setSearchText("");
                    scannerService.removeListen(
                            data,
                            update.getCallbackQuery().getMessage().getChatId()
                    );
                } else if (data.contains("|showvip|")) {
                    dto.setLastIndex(0);
                    dto.setSearchText("");
                    dto.setShowVip(!dto.isShowVip());
                } else if (data.contains("|showall|")){
                    if(dto.getSearchText()=="" || dto.getSearchText()==null) {
                        dto.setLastIndex(0);
                        dto.setShowAll(!dto.isShowAll());
                        dto.setShowListen(false);
                    }
                    dto.setSearchText("");

                } else if (data.contains("|next|")) {
                    dto.setLastIndex(dto.getLastIndex() + 1);
                } else if (data.contains("|prev|")) {
                    dto.setLastIndex(dto.getLastIndex() - 1);
                } else if (data.contains("|back|")) {
                    return handle(update, user);
                }

            }
        }
        return null;
    }
    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.START_SCANNER;
    }



    @Override
    public void removeFromCacheBy(String id) {
        if (scannerCache.getIfPresent(id) != null){
            ScannerDTO dto = scannerCache.getIfPresent(id);
            userService.setVipShow(dto.getUser(),dto.isShowVip());
            Scanner.userInWork.remove(id);
            scannerCache.invalidate(id);
        }
    }

    @Override
    public boolean hasFinished(String id) {
        var dto = scannerCache.getIfPresent(id);

        boolean result = true;

        if (dto != null) {
            result = dto.getStateMachine().isComplete();
        }

        return result;
    }

    @Override
    public int getCurrentStateIndex(String id) {
        var dto = scannerCache.getIfPresent(id);

        int result = -1;

        if (dto != null) {
            result = dto.getStateMachine().getState().getId().getIndex();
        }

        return result;
    }
}