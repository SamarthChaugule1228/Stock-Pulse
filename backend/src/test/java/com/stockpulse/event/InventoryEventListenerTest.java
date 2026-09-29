package com.stockpulse.event;

import com.stockpulse.enums.TriggerReason;
import com.stockpulse.service.SuggestionService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InventoryEventListenerTest {

    @Test
    void mapsInventoryEventToSuggestionProcessing() {
        SuggestionService service = mock(SuggestionService.class);

        new InventoryEventListener(service).onInventoryChanged(new InventoryChangedEvent(3L, TriggerReason.INVENTORY_LOW));

        verify(service).process(3L, TriggerReason.INVENTORY_LOW);
    }
}