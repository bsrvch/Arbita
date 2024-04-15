package com.bsrvch.arbita.initializer;

import com.bsrvch.arbita.scanner.Scanner;
import com.bsrvch.arbita.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class BotInit {
    public static Bot bot;
    public BotInit(Bot bot){
        this.bot = bot;
    }
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        }catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
