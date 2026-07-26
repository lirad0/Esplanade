package com.tvdash.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tvdash.backend.model.UrlOnlyItemData;
import com.tvdash.backend.repository.UrlOnlyItemRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tableau/url-only-items")
@CrossOrigin(origins = "*")
public class UrlOnlyItemController {

    private final UrlOnlyItemRepository repository;

    public UrlOnlyItemController(UrlOnlyItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UrlOnlyItemData> findAll() {
        return repository.findAll();
    }

    @PostMapping
    public UrlOnlyItemData create(@Valid @RequestBody UrlOnlyItemData item) {
        return repository.save(item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UrlOnlyItemData> findById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UrlOnlyItemData> update(@PathVariable String id, @Valid @RequestBody UrlOnlyItemData item) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setUrl(item.getUrl());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
