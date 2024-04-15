package com.bsrvch.arbita.telegram;


import com.bsrvch.arbita.config.BotConfig;
import com.bsrvch.arbita.constant.ButtonCommand;
import com.bsrvch.arbita.dto.InlineButtonDTO;
import com.bsrvch.arbita.encoder.InlineButtonDTOEncoder;
import com.bsrvch.arbita.gateway.Gateway;
import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.service.UserService;
import com.bsrvch.arbita.statics.ramDB.UserRamDB;
import com.bsrvch.arbita.util.builder.InlineKeyboardBuilder;
import com.bsrvch.arbita.util.utilities.TelegramInlineButtonsUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import com.bsrvch.arbita.constant.InlineButtonCommand;
import org.aspectj.weaver.ast.Call;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumb;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    final BotConfig botConfig;
    private final Gateway gateway;
    public final Scanner scanner;
    public Bot(BotConfig botConfig, Gateway gateway,  Scanner scanner) {
        this.botConfig = botConfig;
        this.gateway = gateway;
        this.scanner = scanner.initMarkets().initNetworks();
                    //this.scanner.updateCoins();
        this.scanner.startChecker();
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","Запустить бота"));
        


        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
        }catch (TelegramApiException e){
            log.error("Error setting bot command list: " + e.getMessage());
        }
        this.scanner.startSender();
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
    public void onUpdateReceived(@NotNull Update update) {
// { command:"add_user"
        try {
            if(update.hasMessage()){
                execute(DeleteMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .messageId(update.getMessage().getMessageId())
                    .build());
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        //executeBotApiMethods(gateway.processUpdate(update), update);
        executeAsyncBotMethods(gateway.processUpdate(update), update);
    }
    public void executeBotApiMethods(List<PartialBotApiMethod<?>> methods, Update update) {
        if (methods == null) return;
        String username = "";
        if(update!=null){
            if(update.hasMessage()) {
                username = update.getMessage().getFrom().getFirstName();
            }
            else if(update.hasCallbackQuery()) {
                username = update.getCallbackQuery().getMessage().getChat().getFirstName();
            }
            log.info("Reply sent to " + username);
        }
        for (PartialBotApiMethod<?> method : methods) {
            try {
                execute(method);
            } catch (TelegramApiException e) {
                log.error("123455 "+e.toString());
            }
        }
    }
    public void executeAsyncBotMethods(List<PartialBotApiMethod<?>> methods,Update update) {
        if (methods == null) return;
        String username = "";
        if(update!=null){
            if(update.hasMessage()) {
                username = update.getMessage().getFrom().getFirstName();
            }
            else if(update.hasCallbackQuery()) {
                username = update.getCallbackQuery().getMessage().getChat().getFirstName();
            }
            log.info("Reply sent to " + username);
        }
        for (PartialBotApiMethod<?> method : methods) {
            try {
                executeAsync(method);
            } catch (TelegramApiException e) {
                log.error("123455 "+e.toString());
            }
        }
    }
    public void executeAsync(PartialBotApiMethod<?> method) throws TelegramApiException{
        if (method instanceof SendDocument sendDocument) {
            executeAsync(sendDocument);
        } else if (method instanceof SendPhoto sendPhoto) {
            executeAsync(sendPhoto);
        } else if (method instanceof SendVideo sendVideo) {
            executeAsync(sendVideo);
        } else if (method instanceof SendVideoNote sendVideoNote) {
            executeAsync(sendVideoNote);
        } else if (method instanceof SendSticker sendSticker) {
            executeAsync(sendSticker);
        } else if (method instanceof SendAudio sendAudio) {
            executeAsync(sendAudio);
        } else if (method instanceof SendVoice sendVoice) {
            executeAsync(sendVoice);
        } else if (method instanceof SendMediaGroup sendMediaGroup) {
            executeAsync(sendMediaGroup);
        } else if (method instanceof SetChatPhoto setChatPhoto) {
            executeAsync(setChatPhoto);
        } else if (method instanceof AddStickerToSet addStickerToSet) {
            executeAsync(addStickerToSet);
        } else if (method instanceof SetStickerSetThumb setStickerSetThumb) {
            executeAsync(setStickerSetThumb);
        } else if (method instanceof CreateNewStickerSet createNewStickerSet) {
            executeAsync(createNewStickerSet);
        } else if (method instanceof UploadStickerFile uploadStickerFile) {
            executeAsync(uploadStickerFile);
        } else if (method instanceof EditMessageMedia editMessageMedia) {
            executeAsync(editMessageMedia);
        } else if (method instanceof SendAnimation sendAnimation) {
            executeAsync(sendAnimation);
        } else if (method instanceof BotApiMethod<?> botApiMethod) {
            executeAsync(botApiMethod);
        } else {
            throw new TelegramApiException("Unexpected PartialBotApiMethod tried to execute. Method: " + method.getClass().getName());
        }
    }
    public void execute(PartialBotApiMethod<?> method) throws TelegramApiException {
        if (method instanceof SendDocument sendDocument) {
            execute(sendDocument);
        } else if (method instanceof SendPhoto sendPhoto) {
            execute(sendPhoto);
        } else if (method instanceof SendVideo sendVideo) {
            execute(sendVideo);
        } else if (method instanceof SendVideoNote sendVideoNote) {
            execute(sendVideoNote);
        } else if (method instanceof SendSticker sendSticker) {
            execute(sendSticker);
        } else if (method instanceof SendAudio sendAudio) {
            execute(sendAudio);
        } else if (method instanceof SendVoice sendVoice) {
            execute(sendVoice);
        } else if (method instanceof SendMediaGroup sendMediaGroup) {
            execute(sendMediaGroup);
        } else if (method instanceof SetChatPhoto setChatPhoto) {
            execute(setChatPhoto);
        } else if (method instanceof AddStickerToSet addStickerToSet) {
            execute(addStickerToSet);
        } else if (method instanceof SetStickerSetThumb setStickerSetThumb) {
            execute(setStickerSetThumb);
        } else if (method instanceof CreateNewStickerSet createNewStickerSet) {
            execute(createNewStickerSet);
        } else if (method instanceof UploadStickerFile uploadStickerFile) {
            execute(uploadStickerFile);
        } else if (method instanceof EditMessageMedia editMessageMedia) {
            execute(editMessageMedia);
        } else if (method instanceof SendAnimation sendAnimation) {
            execute(sendAnimation);
        } else if (method instanceof BotApiMethod<?> botApiMethod) {
            execute(botApiMethod);
        } else {
            throw new TelegramApiException("Unexpected PartialBotApiMethod tried to execute. Method: " + method.getClass().getName());
        }

    }
}
