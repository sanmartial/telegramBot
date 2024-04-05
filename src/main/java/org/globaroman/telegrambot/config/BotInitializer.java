package org.globaroman.telegrambot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.globaroman.telegrambot.service.TelegrammBot;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotInitializer {

    private final TelegrammBot telegrammBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegrammBot);

        } catch (TelegramApiException e) {
            log.error("Can not init telegram bot " + telegrammBot.getBotUsername(), e);
            throw new RuntimeException("Can not init telegram bot " + telegrammBot.getBotUsername());
        }
    }
}
