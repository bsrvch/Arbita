package com.bsrvch.arbita.state.scanner;

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
@EnableStateMachineFactory(name = "scannerSMF")
public class ScannerSMC extends StateMachineConfigurerAdapter<ScannerState, ScannerEvent> {
    @Override
    public void configure(StateMachineConfigBuilder<ScannerState, ScannerEvent> config) throws Exception {
        super.configure(config);
    }

    @Override
    public void configure(StateMachineStateConfigurer<ScannerState, ScannerEvent> states) throws Exception {
        states
                .withStates()
                .initial(ScannerState.SCANNER_OPEN)
                .end(ScannerState.SCANNER_STOP)
                .states(EnumSet.allOf(ScannerState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ScannerState, ScannerEvent> transitions) throws Exception {
        transitions

                .withExternal()
                .source(ScannerState.SCANNER_OPEN)
                .target(ScannerState.SCANNER_START)
                .event(ScannerEvent.SCANNER_START)
                .and()

                .withExternal()
                .source(ScannerState.SCANNER_START)
                .target(ScannerState.SCANNER_STOP)
                .event(ScannerEvent.SCANNER_STOP);
    }
}