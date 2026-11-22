package com.esplanade.backend.repository;

import com.esplanade.backend.model.TableauCardData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableauCardRepository extends JpaRepository<TableauCardData, String> {
}
