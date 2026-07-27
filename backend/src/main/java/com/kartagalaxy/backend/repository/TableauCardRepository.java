package com.kartagalaxy.backend.repository;

import com.kartagalaxy.backend.model.TableauCardData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TableauCardRepository extends MongoRepository<TableauCardData, String> {
}
