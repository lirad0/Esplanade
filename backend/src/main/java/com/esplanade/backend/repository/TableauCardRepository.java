package com.esplanade.backend.repository;

import com.esplanade.backend.model.TableauCardData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TableauCardRepository extends MongoRepository<TableauCardData, String> {
}
