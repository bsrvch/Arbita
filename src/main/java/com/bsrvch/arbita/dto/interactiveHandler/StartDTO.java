package com.bsrvch.arbita.dto.interactiveHandler;

import com.bsrvch.arbita.state.payment.PaymentEvent;
import com.bsrvch.arbita.state.payment.PaymentState;
import com.bsrvch.arbita.state.start.StartEvent;
import com.bsrvch.arbita.state.start.StartState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.statemachine.StateMachine;

@ToString
@Getter
@Setter
@Builder
public class StartDTO {
    private Long userId;
    private StateMachine<StartState, StartEvent> stateMachine;
}
