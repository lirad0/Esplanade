package com.KartaGalaxy.backend.repository;

import com.KartaGalaxy.backend.model.TableauCardData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TableauCardRepository extends MongoRepository<TableauCardData, String> {
}
