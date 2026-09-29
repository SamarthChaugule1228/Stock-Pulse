package com.stockpulse.controller;

import com.stockpulse.commerce.CommerceAdvisor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commerce-strategy")
public class CommerceStrategyController {

    private final CommerceAdvisor commerceAdvisor;

    public CommerceStrategyController(CommerceAdvisor commerceAdvisor) {
        this.commerceAdvisor = commerceAdvisor;
    }

    @PatchMapping
    public StrategyResponse change(@Valid @RequestBody StrategyRequest request) {
        commerceAdvisor.setActiveStrategy(request.strategy());
        return new StrategyResponse(commerceAdvisor.getActiveStrategy());
    }

    public record StrategyRequest(@NotBlank String strategy) {
    }

    public record StrategyResponse(String strategy) {
    }
}