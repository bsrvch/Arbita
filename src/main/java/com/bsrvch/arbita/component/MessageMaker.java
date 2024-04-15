package com.bsrvch.arbita.component;


import com.bsrvch.arbita.dto.crypto.Bundle;
import com.bsrvch.arbita.dto.interactiveHandler.FilterDTO;
import com.bsrvch.arbita.dto.interactiveHandler.ScannerDTO;
import com.bsrvch.arbita.initializer.BotInit;
import com.bsrvch.arbita.model.SubscriptionInfo;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.scanner.Market;
import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.statics.locale.StaticLocale;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.bsrvch.arbita.scanner.Scanner.bundlesList;
import static com.bsrvch.arbita.statics.locale.StaticLocale.*;


@Component
@RequiredArgsConstructor
public class MessageMaker {

    final InlineKeyboardMaker inlineKeyboardMaker;

    public SendMessage createMessage(String text, Long chatId, ReplyKeyboard replyKeyboard){
        return SendMessage.builder().text(text).chatId(chatId).replyMarkup(replyKeyboard).build();
    }

    private InputMedia createMedia(String name){
        InputMedia inputMedia = new InputMediaPhoto();
        URL path = getClass().getClassLoader().getResource("img/"+name);
        inputMedia.setMedia(new File(path.getPath()),"123");
        return inputMedia;
    }


    public List<PartialBotApiMethod<?>> getSubscriptionMessage(User user, Long chatId, Integer messageId, List<SubscriptionInfo> subscriptionInfos){
        String text = getSubNoText();
        if(user.getSubscription()!=null){
            text = String.format(getSubText(),new SimpleDateFormat("dd.MM.yyy HH:mm").format(user.getSubscription().getSubscriptionOst()));
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getSubscription(subscriptionInfos);
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
//
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(true);
//
//        // new list
//        List<KeyboardRow> keyboard = new ArrayList<>();
//
//        // first keyboard line
//        KeyboardRow keyboardFirstRow = new KeyboardRow();
//        KeyboardButton keyboardButton = new KeyboardButton();
//        keyboardButton.setText("/contact");
//        keyboardButton.setRequestContact(true);
//        keyboardFirstRow.add(keyboardButton);
//
//        keyboard.add(keyboardFirstRow);
//
//        // add list to our keyboard
//        replyKeyboardMarkup.setKeyboard(keyboard);
//        Integer mi = 0;
//        try{
//            mi = BotInit.bot.execute(SendMessage.builder()
//                    .text("123")
//                    .chatId(chatId)
//                    .replyMarkup(replyKeyboardMarkup)
//                    .build()).getMessageId();
//        }catch (Exception e){}


        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("subscription.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(text)
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
//        res.add(DeleteMessage.builder()
//                .chatId(chatId)
//                .messageId(mi).build());
        return res;
    }

    public List<PartialBotApiMethod<?>> getEmailPayMessage(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getEmailPay();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("payment.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getPayEmailText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }

    public List<PartialBotApiMethod<?>> getPaymentMessage(User user, Long chatId, Integer messageId, Integer paymentAmount, String paymentUrl, String currency){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getPayment(paymentAmount,currency, paymentUrl);
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("payment.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getPayText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }
    public List<PartialBotApiMethod<?>> getSettingsMessage(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getSettings();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("settings.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getSettingsText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }
    public List<PartialBotApiMethod<?>> getLocaleMessage(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getLocale();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("settings.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getLocaleText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }


    public List<PartialBotApiMethod<?>> getProfileMessage(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getProfile(user);
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        String caption = String.format(getProfileText(), user.getName(), getRole(user.getRole().getString()), new SimpleDateFormat("dd.MM.yyy").format(user.getCreatedWhen()));
        if(user.getEmail()!=null)
            caption = String.format(getProfileTextWithEmail(), user.getName(), getRole(user.getRole().getString()), user.getEmail(), new SimpleDateFormat("dd.MM.yyy").format(user.getCreatedWhen()));
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("profile.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(caption)
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }
    public List<PartialBotApiMethod<?>> getEmail(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getEmail();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("profile.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getEmailText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }
    public List<PartialBotApiMethod<?>> getBadEmail(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getEmail();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("profile.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getBadEmailText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }

    public List<PartialBotApiMethod<?>> getGreetingMessage(Long chatId, Integer messageId){
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        if(messageId != null){
            res.add(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build()
            );
        }
        URL path = getClass().getClassLoader().getResource("img/menu.jpg");
        res.add(SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(new File(path.getPath())))
                .caption(StaticLocale.getGreeting())
                .replyMarkup(inlineKeyboardMaker.getMenu())
                .build()
        );
        return res;
    }


    public List<PartialBotApiMethod<?>> getMenuMessage(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getMenu();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("menu.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getMenuText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }

    public List<PartialBotApiMethod<?>> getScannerMessage(User user, Long chatId, Integer messageId){
        String markets = "";
        for(Market market : Scanner.markets){
            markets+=market.getName()+"┃";
        }
        if(markets.length()>4){
            markets = markets.substring(0,markets.length()-1);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getMenuScanner();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("scanner.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(String.format(getScannerText(),markets,Scanner.allCoin.size(),Scanner.allPair.size()))
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }
    public List<PartialBotApiMethod<?>> getFilterMessage(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getFilterScanner();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("scanner.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getFilterText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }
    public List<PartialBotApiMethod<?>> getFilterSearch(Long chatId, Integer messageId, FilterDTO dto){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getFilterSearch(dto);
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("scanner.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getFilterSearchText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }

    public List<PartialBotApiMethod<?>> getInfoMessage(User user, Long chatId, Integer messageId){
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getInfoScanner();
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .media(createMedia("scanner.jpg"))
                .build());
        res.add(EditMessageCaption.builder()
                .caption(getScannerInfoText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
        return res;
    }





    public List<PartialBotApiMethod<?>> getScanner(ScannerDTO dto, Long chatId, Integer messageId){
        //InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getScanner(dto);
        List<PartialBotApiMethod<?>> res = new ArrayList<>();
        res.add(EditMessageCaption.builder()
                .caption(getScannerStartText())
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(null)
                .build());
//        res.add(EditMessageReplyMarkup.builder()
//                        .chatId(dto.getUser().getTelegramId())
//                        .messageId(dto.getUser().getBotMessageId())
//                        .replyMarkup(inlineKeyboardMarkup)
//                        .build());
        return res;
    }


}
