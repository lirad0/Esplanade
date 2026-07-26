package com.tvdash.backend.repository;

import com.tvdash.backend.model.UrlOnlyItemData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlOnlyItemRepository extends MongoRepository<UrlOnlyItemData, String> {
}
