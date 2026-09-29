package com.stockpulse.repository;

import com.stockpulse.entity.PricingSuggestion;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.enums.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingSuggestionRepository extends JpaRepository<PricingSuggestion, Long> {
	boolean existsByProductIdAndTriggerReasonAndStatus(Long productId, TriggerReason triggerReason,
														SuggestionStatus status);
	List<PricingSuggestion> findAllByOrderByCreatedAtDesc();
}
