package com.esplanade.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esplanade.backend.model.TableauBitData;

public interface TableauBitRepository extends JpaRepository<TableauBitData, String> {
}
