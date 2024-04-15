package com.bsrvch.arbita.handler.impl.profile;
import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.annotation.TextCommandType;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.dto.InlineButtonDTO;
import com.bsrvch.arbita.dto.interactiveHandler.EmailDTO;
import com.bsrvch.arbita.dto.interactiveHandler.PaymentDTO;
import com.bsrvch.arbita.encoder.InlineButtonDTOEncoder;
import com.bsrvch.arbita.exception.IllegalUserInputException;
import com.bsrvch.arbita.handler.Handler;
import com.bsrvch.arbita.handler.InteractiveHandler;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.state.email.EmailEvent;
import com.bsrvch.arbita.state.email.EmailState;
import com.bsrvch.arbita.state.payment.PaymentEvent;
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
import com.bsrvch.arbita.constant.InlineButtonCommand;
import java.util.List;
@Component
@AllRoles
@RequiredArgsConstructor
@InlineButtonType
@Slf4j
public class EmailHandler implements InteractiveHandler{
    private final UserService userService;
    private final MessageMaker messageMaker;
    @Qualifier("emailSMF")
    private final StateMachineFactory<EmailState, EmailEvent> stateMachineFactory;
    private Cache<String, EmailDTO> emailCache = Caffeine.newBuilder().build();
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
//        if(update.hasMessage()){
//            if(!update.getMessage().hasText()) {
//                return null;
//            }
//        }
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        StateMachine<EmailState, EmailEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        EmailDTO dto = EmailDTO.builder().stateMachine(stateMachine).build();
        emailCache.put(chatId.toString(), dto);
        userService.setMessageId(user,messageId);
        stateMachine.sendEvent(EmailEvent.EMAIL_REQ);
        return messageMaker.getEmail(user,chatId,messageId);
    }
    @Override
    public List<PartialBotApiMethod<?>> update(Update update, User user) throws IllegalUserInputException, IllegalStateException {
        if(update.hasMessage()){
            Long chatId = update.getMessage().getChatId();
            EmailDTO dto = emailCache.getIfPresent(chatId.toString());
            if(dto != null){
                StateMachine<EmailState, EmailEvent> stateMachine = dto.getStateMachine();
                Integer messageId = user.getBotMessageId();
                String email = update.getMessage().getText();
                if(userService.setEmail(user, email)){
                    stateMachine.sendEvent(EmailEvent.EMAIL_ENTER);
                    return messageMaker.getProfileMessage(user,chatId,messageId);
                }else {
                    return messageMaker.getBadEmail(user,chatId,messageId);
                }
            }
        }else if(update.hasCallbackQuery()){
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            EmailDTO dto = emailCache.getIfPresent(chatId.toString());
            var stateMachine = dto.getStateMachine();
            stateMachine.sendEvent(EmailEvent.EMAIL_ENTER);
            return handle(update,user);
        }
        return null;
    }
    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.GET_EMAIL;
    }



    @Override
    public void removeFromCacheBy(String id) {
        if (emailCache.getIfPresent(id) != null)
            emailCache.invalidate(id);
    }

    @Override
    public boolean hasFinished(String id) {
        var dto = emailCache.getIfPresent(id);

        boolean result = true;

        if (dto != null) {
            result = dto.getStateMachine().isComplete();
        }

        return result;
    }

    @Override
    public int getCurrentStateIndex(String id) {
        var dto = emailCache.getIfPresent(id);

        int result = -1;

        if (dto != null) {
            result = dto.getStateMachine().getState().getId().getIndex();
        }

        return result;
    }
}
