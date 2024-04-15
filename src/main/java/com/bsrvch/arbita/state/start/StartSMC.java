package com.bsrvch.arbita.state.start;


import com.bsrvch.arbita.state.payment.PaymentEvent;
import com.bsrvch.arbita.state.payment.PaymentState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfig;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.common.annotation.AnnotationBuilder;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "startSMF")
public class StartSMC extends StateMachineConfigurerAdapter<StartState,StartEvent> {
    @Override
    public void configure(StateMachineConfigBuilder<StartState, StartEvent> config) throws Exception {
        super.configure(config);
    }
    @Override
    public void configure(StateMachineStateConfigurer<StartState, StartEvent> states) throws Exception {
        states
                .withStates()
                .initial(StartState.START)
                .end(StartState.FINISH)
                .states(EnumSet.allOf(StartState.class));
    }
    @Override
    public void configure(StateMachineTransitionConfigurer<StartState, StartEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(StartState.START)
                .target(StartState.CHOICE_LOCALE)
                .event(StartEvent.CHOOSE_LOCALE)
                .and()

                .withExternal()
                .source(StartState.CHOICE_LOCALE)
                .target(StartState.REGISTER)
                .event(StartEvent.CREATE_USER)
                .and()

                .withExternal()
                .source(StartState.REGISTER)
                .target(StartState.FINISH)
                .event(StartEvent.REGISTER);
    }


}
