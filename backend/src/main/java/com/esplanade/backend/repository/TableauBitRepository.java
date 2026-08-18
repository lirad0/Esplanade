package com.esplanade.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.esplanade.backend.model.TableauBitData;

public interface TableauBitRepository extends MongoRepository<TableauBitData, String> {
}
