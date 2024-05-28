package com.skillbox.cryptobot.repositories;

import com.skillbox.cryptobot.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Optional<Subscriber> findByTelegramId(Long telegram_id);
}
