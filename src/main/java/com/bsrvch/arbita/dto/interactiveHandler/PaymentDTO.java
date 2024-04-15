package com.bsrvch.arbita.dto.interactiveHandler;

import com.bsrvch.arbita.state.payment.PaymentEvent;
import com.bsrvch.arbita.state.payment.PaymentState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.statemachine.StateMachine;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;

@ToString
@Getter
@Setter
@Builder
public class PaymentDTO {

    private Long userId;

    private String payId;

    private Long durationUnix;

    private StateMachine<PaymentState, PaymentEvent> stateMachine;
}
