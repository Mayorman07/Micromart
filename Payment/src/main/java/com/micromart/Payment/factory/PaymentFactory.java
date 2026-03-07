package com.micromart.Payment.factory;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.strategies.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentFactory {
    private final Map<PaymentMethod, PaymentStrategy> strategies;

    public PaymentFactory(List<PaymentStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getType, s -> s));
    }

    public PaymentStrategy getStrategy(PaymentMethod method) {
        return Optional.ofNullable(strategies.get(method))
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for: " + method));
    }
}
