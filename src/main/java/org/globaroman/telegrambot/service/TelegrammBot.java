package org.globaroman.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.globaroman.telegrambot.config.BotConfig;
import org.globaroman.telegrambot.model.User;
import org.globaroman.telegrambot.reposiitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegrammBot extends TelegramLongPollingBot {

    private static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities.\n\n"
            + "You can execute commands from the main menu on the left or by typing a command:\n\n"
            + "Type /start to see a welcome message\n\n"
            + "Type /mydata to see data stored about yourself\n\n"
            + "Type /help to see this message again";
    private final BotConfig botConfig;

    @Autowired
    private final UserRepository userRepository;

    public TelegrammBot(BotConfig botConfig, UserRepository userRepository) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/mydata", "get your data stored"));
        listofCommands.add(new BotCommand("/deletedata", "delete my data"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/settings", "set your preferences"));

        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: ", e);
        }
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            Long chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start" -> {
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                }
                case "/help" -> {
                    sendMessage(chatId, HELP_TEXT);
                }
                default -> {
                    sendMessage(chatId, "Sorry, this command is not yet supported.");
                }
            }
        }
    }

    private void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            Long chatId = message.getChatId();
            Chat chat = message.getChat();

            User user = new User();
            user.setId(chatId);
            user.setUserName(chat.getUserName());
            user.setLastName(chat.getLastName());
            user.setFirstName(chat.getFirstName());
            user.setRegisteredAt(Timestamp.valueOf(LocalDateTime.now()));

            userRepository.save(user);
            log.info("User saved: " + user);

        }
    }

    private void startCommandReceived(Long chatId, String firstName) {

        String answer = "Hi, " + firstName + ", nice to meet!";
        log.info("Replied to user " + firstName + " from " + chatId);
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText(textToSend);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Can not send message to " + chatId, e);
            throw new RuntimeException("Can not send message to " + chatId, e);
        }
    }

}
