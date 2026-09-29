package com.stockpulse.repository;

import com.stockpulse.entity.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Long> {
}
