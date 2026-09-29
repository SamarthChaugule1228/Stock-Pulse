package com.stockpulse.repository;

import com.stockpulse.entity.ReorderSuggestion;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.enums.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, Long> {
	boolean existsByProductIdAndTriggerReasonAndStatus(Long productId, TriggerReason triggerReason,
													   SuggestionStatus status);
	List<ReorderSuggestion> findAllByOrderByCreatedAtDesc();
}
