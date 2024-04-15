package com.bsrvch.arbita.handler;

import com.bsrvch.arbita.exception.IllegalUserInputException;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface InteractiveHandler extends Handler {

    List<PartialBotApiMethod<?>> update(Update update, User user) throws IllegalUserInputException, IllegalStateException;


    void removeFromCacheBy(String id);

    boolean hasFinished(String id);

    int getCurrentStateIndex(String id);
}
