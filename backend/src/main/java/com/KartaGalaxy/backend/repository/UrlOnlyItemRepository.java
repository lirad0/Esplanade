package com.kartagalaxy.backend.repository;

import com.kartagalaxy.backend.model.UrlOnlyItemData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlOnlyItemRepository extends MongoRepository<UrlOnlyItemData, String> {
}
