package com.stockpulse.event;

import com.stockpulse.service.SuggestionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InventoryEventListener {

	private final SuggestionService suggestionService;

	public InventoryEventListener(SuggestionService suggestionService) {
		this.suggestionService = suggestionService;
	}

	@Async("commerceTaskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onInventoryChanged(InventoryChangedEvent event) {
		suggestionService.process(event.productId(), event.triggerReason());
	}

	@Async("commerceTaskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onDemandSpike(DemandSpikeEvent event) {
		suggestionService.process(event.productId(), com.stockpulse.enums.TriggerReason.DEMAND_SPIKE);
	}
}
