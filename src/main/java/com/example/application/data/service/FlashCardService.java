package com.example.application.data.service;

import com.example.application.data.entity.FlashCard;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FlashCardService {

    private final FlashCardRepository repository;

    @Autowired
    public FlashCardService(FlashCardRepository repository) {
        this.repository = repository;
    }

    public Optional<FlashCard> get(UUID id) {
        return repository.findById(id);
    }

    public FlashCard update(FlashCard entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<FlashCard> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
