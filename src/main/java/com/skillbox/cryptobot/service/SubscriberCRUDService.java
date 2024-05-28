package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.dto.SubscriberDto;
import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.repositories.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubscriberCRUDService implements CRUDService<SubscriberDto>{

    private final SubscriberRepository subscriberRepository;

    @Override
    public SubscriberDto getByItem(SubscriberDto subscriberDto) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(subscriberDto.getTelegramId()).orElseThrow();
        return mapToDto(subscriber);
    }

    @Override
    public Collection<SubscriberDto> getAll() {
        return subscriberRepository.findAll()
                .stream()
                .map(SubscriberCRUDService::mapToDto)
                .toList();
    }

    @Override
    public void create(SubscriberDto item) {
        subscriberRepository.save(mapToEntity(item));
    }

    @Override
    public void update(SubscriberDto item) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(item.getTelegramId()).orElseThrow();
        subscriber.setBcPrice(item.getBcPrice());
        subscriberRepository.save(subscriber);
    }

    @Override
    public void delete(SubscriberDto item) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(item.getTelegramId()).orElseThrow();
        subscriber.setBcPrice(null);
        subscriberRepository.save(subscriber);
    }

    public static SubscriberDto mapToDto(Subscriber subscriber) {
        SubscriberDto subscriberDto = new SubscriberDto();
        subscriberDto.setId(subscriber.getId());
        subscriberDto.setTelegramId(subscriber.getTelegramId());
        subscriberDto.setBcPrice(subscriber.getBcPrice());
        return subscriberDto;
    }

    public static Subscriber mapToEntity(SubscriberDto subscriberDto) {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(subscriberDto.getId());
        subscriber.setTelegramId(subscriberDto.getTelegramId());
        subscriber.setBcPrice(subscriberDto.getBcPrice());
        return subscriber;
    }
}
