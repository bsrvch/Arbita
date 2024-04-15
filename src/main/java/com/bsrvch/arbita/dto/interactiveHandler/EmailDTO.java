package com.bsrvch.arbita.dto.interactiveHandler;

import com.bsrvch.arbita.state.email.EmailEvent;
import com.bsrvch.arbita.state.email.EmailState;
import com.bsrvch.arbita.state.payment.PaymentEvent;
import com.bsrvch.arbita.state.payment.PaymentState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.statemachine.StateMachine;

@ToString
@Getter
@Setter
@Builder
public class EmailDTO {
    private StateMachine<EmailState, EmailEvent> stateMachine;
}
