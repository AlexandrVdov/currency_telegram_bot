package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.dto.SubscriberDto;
import com.skillbox.cryptobot.service.SubscriberCRUDService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final SubscriberCRUDService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        try {
            SubscriberDto subscriberDto = new SubscriberDto();
            subscriberDto.setTelegramId(message.getChatId());
            subscriberDto = subscriberService.getByItem(subscriberDto);

            if(subscriberDto.getBcPrice() != null) {
                answer.setText("Вы подписаны на стоимость биткоина "
                        + subscriberDto.getBcPrice() + " USD");
            } else {
                answer.setText("Активные подписки отсутствуют");
            }
            absSender.execute(answer);

        } catch (Exception e) {
            log.error("Error occurred in /get_subscription command", e);
        }
    }
}