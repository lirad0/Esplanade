package com.KartaGalaxy.backend.repository;

import com.KartaGalaxy.backend.model.UrlOnlyItemData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlOnlyItemRepository extends MongoRepository<UrlOnlyItemData, String> {
}
