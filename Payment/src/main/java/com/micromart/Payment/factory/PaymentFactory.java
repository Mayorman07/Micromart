package com.micromart.Payment.factory;

import com.micromart.Payment.enums.PaymentMethod;
import com.micromart.Payment.strategies.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentFactory {
    private final Map<PaymentMethod, PaymentStrategy> strategies;

    public PaymentFactory(List<PaymentStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getType, s -> s));
    }

    public PaymentStrategy get(PaymentMethod method) {
        return strategies.get(method);
    }
}
