package com.bsrvch.arbita.dto.interactiveHandler;

import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.state.email.EmailEvent;
import com.bsrvch.arbita.state.email.EmailState;
import com.bsrvch.arbita.state.filter.FilterEvent;
import com.bsrvch.arbita.state.filter.FilterState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.statemachine.StateMachine;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
public class FilterDTO {
    private StateMachine<FilterState, FilterEvent> stateMachine;
    private List<String> dataList;
    private boolean all;
    private boolean onlyMode;
    private String type;
    private ArrayList<String> filter;
    private List<String> lastList;
    private int lastIndex;
    private User user;

    public void addFilter(String name){
        filter.add(name);
    }
    public void removeFilter(String name){
        filter.remove(name);
    }
    public void incLastIndex(){
        lastIndex++;
    }
    public void decLastIndex(){
        lastIndex--;
    }
}

