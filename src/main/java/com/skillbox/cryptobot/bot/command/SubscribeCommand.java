package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.dto.SubscriberDto;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.service.SubscriberCRUDService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final SubscriberCRUDService subscriberService;
    private final CryptoCurrencyService cryptoCurrencyService;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        try {
            if (arguments.length != 1 || !arguments[0].matches("[0-9]+")) {
                answer.setText("""
                          Ошибка при указании цены, введите:
                           /subscribe [число] - подписаться на стоимость биткоина в USD
                          """);
                absSender.execute(answer);
                return;
            }
            answer.setText("Текущая цена биткоина "
                    + TextUtil.toString(cryptoCurrencyService.getBitcoinPrice()) + " USD");
            absSender.execute(answer);

            answer.setText("Новая подписка создана на стоимость "
                    + arguments[0] + " USD");
            absSender.execute(answer);

            SubscriberDto subscriberDto = new SubscriberDto();
            subscriberDto.setTelegramId(message.getChatId());
            subscriberDto.setBcPrice(Integer.valueOf(arguments[0]));
            subscriberService.update(subscriberDto);

        } catch (Exception e) {
            log.error("Error occurred in /subscribe command", e);
        }
    }
}