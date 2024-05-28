package com.skillbox.cryptobot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class SubscriberDto {
    private UUID id;
    private Long telegramId;
    private Integer bcPrice;
}
