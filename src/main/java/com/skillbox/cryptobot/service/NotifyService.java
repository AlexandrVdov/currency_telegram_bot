package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.dto.SubscriberDto;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Оповещение понижения стоимости валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotifyService {
    @Value("${telegram.bot.notify.delay.value}")
    private long delayValue;
    @Value("${telegram.bot.notify.delay.unit}")
    private ChronoUnit delayUnit;

    private final SubscriberCRUDService subscriberCRUDService;
    private final CryptoCurrencyService cryptoCurrencyService;
    private final CryptoBot cryptoBot;

    private final HashMap<Long, LocalDateTime> lastMessages = new HashMap<>();
    private Double actualPrice;

    @Async
    @Scheduled(fixedRateString = "${telegram.bot.notify.repeat.value}", timeUnit = TimeUnit.MINUTES)
    public void sendNotify() {
        try {
            actualPrice = cryptoCurrencyService.getBitcoinPrice();
        } catch (Exception e) {
            log.error("Ошибка получения цены", e);
        }
        sendMessage(findSubscribersWithMorePrice());
    }

    private List<SubscriberDto> findSubscribersWithMorePrice() {
        return subscriberCRUDService.getAll().stream()
                .filter(subscriberDto ->
                        subscriberDto.getBcPrice() != null &&
                        subscriberDto.getBcPrice() > actualPrice)
                .toList();
    }

    private void sendMessage(List<SubscriberDto> subscribers) {
        cleanLastMessages();
        subscribers.forEach(subscriberDto -> {
            if(lastMessages.containsKey(subscriberDto.getTelegramId())) {
                return;
            }
            lastMessages.put(subscriberDto.getTelegramId(), LocalDateTime.now());
            cryptoBot.sendMessage(subscriberDto.getTelegramId(),
                    "Пора покупать, стоимость биткоина "
                            + TextUtil.toString(actualPrice) + " USD");
        });
    }

    private void cleanLastMessages() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeAgo = now.minus(delayValue, delayUnit);

        Map<Long, LocalDateTime> filteredLastMessages =  lastMessages.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isBefore(timeAgo))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        filteredLastMessages.keySet().forEach(lastMessages::remove);
    }
}
