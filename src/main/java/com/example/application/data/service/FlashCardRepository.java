package com.example.application.data.service;

import com.example.application.data.entity.FlashCard;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashCardRepository extends JpaRepository<FlashCard, UUID> {

}