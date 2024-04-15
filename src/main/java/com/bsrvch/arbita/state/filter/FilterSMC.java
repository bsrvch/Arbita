package com.bsrvch.arbita.state.filter;


import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name="filterSMF")
public class FilterSMC extends StateMachineConfigurerAdapter<FilterState, FilterEvent> {
    @Override
    public void configure(StateMachineConfigBuilder<FilterState, FilterEvent> config) throws Exception {
        super.configure(config);
    }

    @Override
    public void configure(StateMachineStateConfigurer<FilterState, FilterEvent> states) throws Exception {
        states
                .withStates()
                .initial(FilterState.FILTER_START)
                .end(FilterState.FILTER_ADD)
                .states(EnumSet.allOf(FilterState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<FilterState, FilterEvent> transitions) throws Exception {
        transitions

                .withExternal()
                .source(FilterState.FILTER_START)
                .target(FilterState.FILTER_CHOOSE)
                .event(FilterEvent.FILTER_START)
                .and()

                .withExternal()
                .source(FilterState.FILTER_CHOOSE)
                .target(FilterState.FILTER_SEARCH)
                .event(FilterEvent.FILTER_SEARCH)
                .and()

                .withExternal()
                .source(FilterState.FILTER_SEARCH)
                .target(FilterState.FILTER_ADD)
                .event(FilterEvent.FILTER_ADD);
    }
}
