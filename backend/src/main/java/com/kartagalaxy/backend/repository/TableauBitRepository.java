package com.kartagalaxy.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kartagalaxy.backend.model.TableauBitData;

public interface TableauBitRepository extends MongoRepository<TableauBitData, String> {
}
