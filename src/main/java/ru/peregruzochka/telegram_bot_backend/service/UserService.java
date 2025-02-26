package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.User;
import ru.peregruzochka.telegram_bot_backend.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserByTelegramId(Long telegramId) {
        User user = userRepository.findByTelegramId(telegramId)
                .orElse(null);
        log.info("Get user by tgId {}: {}", telegramId, user);
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElse(null);
        log.info("Get user by phone {}: {}", phone, user);
        return user;
    }
}
