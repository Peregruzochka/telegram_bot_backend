package ru.peregruzochka.telegram_bot_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotBackendApplication.class, args);
	}

}
