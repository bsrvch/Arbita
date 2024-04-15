package com.bsrvch.arbita.state.payment;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "paymentSMF")
public class PaymentSMC extends StateMachineConfigurerAdapter<PaymentState,PaymentEvent> {
    @Override
    public void configure(StateMachineConfigBuilder<PaymentState, PaymentEvent> config) throws Exception {
        super.configure(config);
    }

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states
                .withStates()
                .initial(PaymentState.PAY_CREATE)
                .end(PaymentState.PAY_CONFIRM)
                .states(EnumSet.allOf(PaymentState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PaymentState.PAY_CREATE)
                .target(PaymentState.PAY_SHOW)
                .event(PaymentEvent.PAY_SHOW)
                .and()

                .withExternal()
                .source(PaymentState.PAY_SHOW)
                .target(PaymentState.PAY_CONFIRM)
                .event(PaymentEvent.PAY_CONFIRM);
    }
}
