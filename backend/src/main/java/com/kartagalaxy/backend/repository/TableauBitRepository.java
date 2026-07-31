package com.kartagalaxy.backend.repository;

import com.kartagalaxy.backend.model.TableauBitData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TableauBitRepository extends MongoRepository<TableauBitData, String> {
}
