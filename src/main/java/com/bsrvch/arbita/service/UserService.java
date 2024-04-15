package com.bsrvch.arbita.service;

import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;

import java.util.List;

public interface UserService {


    User getUserByChatId(Long chatId);


    User createUser(Long telegramId, String firstName, String lastName, String username);
    void save(User user);

    UserRole getUserRole(Long telegramId);

    User checkSubscription(User user);


    void changeUserRole(Long telegramId, UserRole role);

    void setMessageId(User user, Integer messageId);

    void setVipShow(User user, boolean show);
    boolean setEmail(User user, String email);

}
