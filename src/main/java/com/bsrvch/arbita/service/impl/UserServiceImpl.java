package com.bsrvch.arbita.service.impl;

import com.bsrvch.arbita.exception.NoSuchUserException;
import com.bsrvch.arbita.exception.UserAlreadyExistsException;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.UserSettings;
import com.bsrvch.arbita.model.dictionary.UserRole;
import com.bsrvch.arbita.repository.UserRepo;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.state.email.EmailEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.List;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepo userRepo;


    @Override
    public boolean setEmail(User user, String email){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        if(m.matches()){
            user.setEmail(email);
            user = userRepo.save(user);
            return true;
        }else{
            return false;
        }
    }








    @Override
    @Transactional
    public User getUserByChatId(Long chatId) {
        return userRepo.findByTelegramId(chatId);
    }



    public User checkSubscription(User user){
        if(user.getSubscription()!=null){
            if(System.currentTimeMillis() < user.getSubscription().getSubscriptionOst().getTime() && user.getRole().equals(UserRole.LITE)){
                user.setRole(UserRole.VIP);
                userRepo.save(user);
            } else if (System.currentTimeMillis() > user.getSubscription().getSubscriptionOst().getTime() && user.getRole().equals(UserRole.VIP)) {
                user.setRole(UserRole.LITE);
                userRepo.save(user);
            }
        }else if(!user.getRole().equals(UserRole.ADMIN)){
            user.setRole(UserRole.LITE);
            userRepo.save(user);
        }
        return user;
    }


    @Override
    @Transactional
    public User createUser(Long chatId, String firstName, String lastName, String username) {
        User user = getUserByChatId(chatId);

        if (user != null)
            return user;//throw new UserAlreadyExistsException(chatId.toString());
        UserSettings userSettings = new UserSettings();
        user = new User();
        user.setTelegramId(chatId);
        user.setName(firstName);
        if(lastName != null && !lastName.equals(""))
            user.setLastName(lastName);
        if(username != null && !username.equals(""))
            user.setUsername(username);
        else user.setUsername(firstName);
        user.setRole(UserRole.LITE);
//        try{
//            user.setTelegramId(chatId);
//        }catch (Exception e){}
//        try{
//            user.setName(firstName);
//        }catch (Exception e){}
//        try{
//            user.setLastName(lastName);
//        }catch (Exception e){}
//        try{
//            user.setUsername(username);
//        }catch (Exception e){}
//        try{
//            user.setRole(UserRole.LITE);
//        }catch (Exception e){}
        user = userRepo.save(user);
        userSettings.setUser(user);
        userSettings.setFilterOnlyMode("");
        user.setUserSettings(userSettings);
        user = userRepo.save(user);
        return user;
    }

    @Override
    public void save(User user){
        userRepo.save(user);
    }

    @Override
    @Transactional
    public UserRole getUserRole(Long chatId) {
        User user = getUserByChatId(chatId);

        if (user == null)
            throw new NoSuchUserException(String.format("User with telegram id %s hasn't been found", chatId));

        return user.getRole();
    }

    @Override
    @Transactional
    public void changeUserRole(Long chatId, UserRole role) {
        User user = getUserByChatId(chatId);

        if (user == null)
            throw new NoSuchUserException(String.format("User with telegram id %s hasn't been found", chatId));

        user.setRole(role);
    }

    @Override
    public void setMessageId(User user, Integer messageId) {
        user.setBotMessageId(messageId);
        userRepo.save(user);
    }

    @Override
    public void setVipShow(User user, boolean show) {
        user.getUserSettings().setShowVip(show);
        user = userRepo.save(user);
    }

//    @Override
//    public void setEmail(User user, String email){
//        user.setEmail(email);
//        user = userRepo.save(user);
//    }
}
