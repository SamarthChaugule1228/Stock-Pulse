package com.stockpulse.event;

import com.stockpulse.enums.TriggerReason;

public record InventoryChangedEvent(Long productId, TriggerReason triggerReason) {
}
