package com.kartagalaxy.backend.controller;

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

import com.kartagalaxy.backend.model.TableauBitData;
import com.kartagalaxy.backend.repository.TableauBitRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tableau/bits")
@CrossOrigin(origins = "*")
public class TableauBitController {

    private final TableauBitRepository repository;

    public TableauBitController(TableauBitRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TableauBitData> findAll() {
        return repository.findAll();
    }

    @PostMapping
    public TableauBitData create(@Valid @RequestBody TableauBitData item) {
        return repository.save(item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableauBitData> findById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableauBitData> update(@PathVariable String id, @Valid @RequestBody TableauBitData item) {
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
