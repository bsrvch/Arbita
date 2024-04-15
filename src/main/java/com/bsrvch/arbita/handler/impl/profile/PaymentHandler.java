package com.bsrvch.arbita.handler.impl.profile;


import com.bsrvch.arbita.annotation.AllRoles;
import com.bsrvch.arbita.annotation.InlineButtonType;
import com.bsrvch.arbita.annotation.TextCommandType;
import com.bsrvch.arbita.component.MessageMaker;
import com.bsrvch.arbita.constant.Command;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import com.bsrvch.arbita.dto.interactiveHandler.PaymentDTO;
import com.bsrvch.arbita.encoder.InlineButtonDTOEncoder;
import com.bsrvch.arbita.exception.IllegalUserInputException;
import com.bsrvch.arbita.handler.InteractiveHandler;
import com.bsrvch.arbita.model.Payment;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.service.PaymentService;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.state.payment.PaymentEvent;
import com.bsrvch.arbita.state.payment.PaymentState;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@AllRoles
@RequiredArgsConstructor
@InlineButtonType
@TextCommandType
@Slf4j
public class PaymentHandler implements InteractiveHandler {
    @Value("${bot.yookassa.shopId}")
    private String shopId;
    @Value("${bot.yookassa.secretApi}")
    private String secretApi;
    @Qualifier("paymentSMF")
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private Cache<String, PaymentDTO> paymentCache = Caffeine.newBuilder().build();

    private final MessageMaker messageMaker;
    private final PaymentService paymentService;
    private final UserService userService;
    @Override
    public List<PartialBotApiMethod<?>> handle(Update update, User user) {
        if(update.hasMessage()){
            return null;
        }
        System.out.println("7");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        if(user.getEmail()==null){
            return messageMaker.getEmailPayMessage(user,chatId,messageId);
        }
        StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        PaymentDTO dto = PaymentDTO.builder().stateMachine(stateMachine).build();
        JSONObject jo = paymentService.createUrl(
                user.getEmail(),
                dto,
                InlineButtonDTOEncoder.decode(update.getCallbackQuery().getData()).getData(),
                shopId,
                secretApi
        );
        paymentCache.put(chatId.toString(), dto);
        stateMachine.sendEvent(PaymentEvent.PAY_SHOW);
        return messageMaker.getPaymentMessage(
                user,
                chatId,
                messageId,
                (int)Float.parseFloat(jo.getJSONObject("amount").getString("value")),
                jo.getJSONObject("confirmation").getString("confirmation_url"),
                jo.getJSONObject("amount").getString("currency")
        );
    }

    @Override
    public List<PartialBotApiMethod<?>> update(Update update, User user) throws IllegalUserInputException, IllegalStateException {
        Long chatId;
        System.out.println("8");
        if(update.hasMessage()){
            chatId = update.getMessage().getChatId();
        }else chatId = update.getCallbackQuery().getMessage().getChatId();
        PaymentDTO dto = paymentCache.getIfPresent(chatId.toString());
        if(dto != null){
            StateMachine<PaymentState,PaymentEvent> stateMachine = dto.getStateMachine();
            if(stateMachine.getState().getId() == PaymentState.PAY_SHOW){
                Payment payment = paymentService.createPayment(user, dto, shopId, secretApi);
                if(payment != null){
                    stateMachine.sendEvent(PaymentEvent.PAY_CONFIRM);
                    //System.out.println("conf");
                }
//                else {
//                    System.out.println("not conf");
//                }
                return null;
            }
        }
        return handle(update, user);
    }
    @Override
    public Command getCommandObject() {
        return InlineButtonCommand.MENU_PAYMENT;
    }
    @Override
    public void removeFromCacheBy(String id) {
        if (paymentCache.getIfPresent(id) != null){
            User user = userService.getUserByChatId(Long.parseLong(id));
            PaymentDTO dto = paymentCache.getIfPresent(id);
            paymentService.createPayment(user, dto, shopId, secretApi);
            paymentCache.invalidate(id);
        }

    }

    @Override
    public boolean hasFinished(String id) {
        var dto = paymentCache.getIfPresent(id);
        boolean result = true;
        if (dto != null) {
            result = dto.getStateMachine().isComplete();
        }
        return result;
    }

    @Override
    public int getCurrentStateIndex(String id) {
        var dto = paymentCache.getIfPresent(id);

        int result = -1;

        if (dto != null) {
            result = dto.getStateMachine().getState().getId().getIndex();
        }

        return result;
    }
}
