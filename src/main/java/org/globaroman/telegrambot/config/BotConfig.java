package org.globaroman.telegrambot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
//@PropertySource(".env")
@Data
public class BotConfig {

    @Value("${TEL_BOT_TOKEN}")
    private String token;

    @Value("${TEL_BOT_USERNAME}")
    private String username;
}
