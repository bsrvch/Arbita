package com.bsrvch.arbita.state.email;

import com.bsrvch.arbita.state.payment.PaymentEvent;
import com.bsrvch.arbita.state.payment.PaymentState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name="emailSMF")
public class EmailSMC extends StateMachineConfigurerAdapter<EmailState,EmailEvent> {
    @Override
    public void configure(StateMachineConfigBuilder<EmailState, EmailEvent> config) throws Exception {
        super.configure(config);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EmailState, EmailEvent> states) throws Exception {
        states
                .withStates()
                .initial(EmailState.EMAIL_REQ)
                .end(EmailState.EMAIL_CONFIRM)
                .states(EnumSet.allOf(EmailState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EmailState, EmailEvent> transitions) throws Exception {
        transitions

                .withExternal()
                .source(EmailState.EMAIL_REQ)
                .target(EmailState.EMAIL_ENTER)
                .event(EmailEvent.EMAIL_REQ)
                .and()

                .withExternal()
                .source(EmailState.EMAIL_ENTER)
                .target(EmailState.EMAIL_CONFIRM)
                .event(EmailEvent.EMAIL_ENTER);
    }
}
