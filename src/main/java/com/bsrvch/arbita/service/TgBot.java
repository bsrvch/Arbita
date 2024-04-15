package com.bsrvch.arbita.service;

import com.bsrvch.arbita.config.BotConfig;
import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.repository.UserRepo;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TgBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepo userRepo;


    final BotConfig botConfig;
    private long time;
    public TgBot(BotConfig botConfig){
        this.botConfig = botConfig;
//        List<BotCommand> listOfCommands = new ArrayList<>();
//        listOfCommands.add(new BotCommand("/start","Запустить бота"));
//        listOfCommands.add(new BotCommand("/mydata","Мои данные"));
//        try {
//            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
//        }catch (TelegramApiException e){
//            log.error("Error setting bot command list: " + e.getMessage());
//        }
    }
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }



    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            time = System.currentTimeMillis();
            //System.out.println(update.getMessage().getChat().getFirstName());
            switch (messageText){
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/reg":
                    reg(chatId);
                    break;
                default: sendMessage(chatId,"Ты еблан?");
            }
        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(callData.equals("YES")){
                String text = "gggggg yes";
                EditMessageText msg = new EditMessageText();
                msg.setChatId(chatId);
                msg.setText(text);
                msg.setMessageId(messageId);
                try {
                    execute(msg);
                }catch (TelegramApiException e){
                    log.error("Error occurred: " + e.getMessage());
                }
            } else if (callData.equals("NO")) {
                String text = "gggggg no";
                EditMessageReplyMarkup mrk = new EditMessageReplyMarkup();
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List< InlineKeyboardButton >> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText("no");
                btn.setCallbackData("NO");
                row.add(btn);
                btn = new InlineKeyboardButton();
                btn.setText("yes");
                btn.setCallbackData("YES");
                row.add(btn);

                rowsInline.add(row);
                inlineKeyboardMarkup.setKeyboard(rowsInline);

                mrk.setChatId(chatId);
                mrk.setMessageId(messageId);
                mrk.setReplyMarkup(inlineKeyboardMarkup);
                EditMessageText msg = new EditMessageText();
                msg.setChatId(chatId);
                msg.setText(text);
                msg.setMessageId(messageId);
                try {
                    execute(mrk);
                }catch (TelegramApiException e){
                    log.error("Error occurred: " + e.getMessage());
                }
            }
        }
    }

    private void reg(Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("t ?");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List< InlineKeyboardButton >> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText("yes");
        btn.setCallbackData("YES");
        row.add(btn);
        btn = new InlineKeyboardButton();
        btn.setText("no");
        btn.setCallbackData("NO");
        row.add(btn);

        rowsInline.add(row);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        }catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }


    private void registerUser(Message msg){
//        if(userRepo.findById(msg.getChatId()).isEmpty()){
//            long chatId = msg.getChatId();
//            Chat chat = msg.getChat();
//            User user = new User();
//            user.setChatId(chatId);
//            user.setUserName(chat.getUserName());
//            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
//            userRepo.save(user);
//            log.info("User saved " + user);
//        }
    }
    private void startCommandReceived(long chatId, String name){
        String answer = EmojiParser.parseToUnicode(name + " - писька " + ":blush:");
        log.info("Replied to user " + name);
        sendMessage(chatId, "asdgasdgsd");
    }
    private void sendMessage(long chatId, String textMessage){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textMessage);
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//
//
//
//        KeyboardRow row = new KeyboardRow();
//        row.add(new KeyboardButton("aaa"));
//        keyboardRows.add(row);
//
//
//        KeyboardRow row1 = new KeyboardRow();
//        row1.add(new KeyboardButton("bbb"));
//        keyboardRows.add(row1);
//        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setKeyboard(keyboardRows);
//
//        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
            System.out.println(System.currentTimeMillis()-time);
        }catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }
}
