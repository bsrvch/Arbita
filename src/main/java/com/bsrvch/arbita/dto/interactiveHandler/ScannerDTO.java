package com.bsrvch.arbita.dto.interactiveHandler;

import com.bsrvch.arbita.dto.crypto.Bundle;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.state.scanner.ScannerEvent;
import com.bsrvch.arbita.state.scanner.ScannerState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.statemachine.StateMachine;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
public class ScannerDTO {
    StateMachine<ScannerState, ScannerEvent> stateMachine;
    User user;
    String searchText;
    boolean showListen;
    boolean showAll;
    boolean showVip;
    int lastIndex;
    InlineKeyboardMarkup prevMark;
    boolean isUpdate;
    HashMap<String, String> lastBundle;
}
