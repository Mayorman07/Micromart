package com.micromart.products.utils;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
@Component
public class SkuCodeGenerator {

    /**
     * Generates a human-readable SKU code.
     * Example: "Apple", "iPhone 15", "Black" -> "APP-IPH-BLA"
     */
    public String generateSku(String brand, String model, String variant) {
        return (shorten(brand) + "-" + shorten(model) + "-" + shorten(variant))
                .toUpperCase(Locale.ROOT);
    }

    private static String shorten(String input) {
        if (input == null || input.isBlank()) {
            return "XXX";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String clean = normalized.replaceAll("[^a-zA-Z0-9]", "");
        return clean.length() > 3 ? clean.substring(0, 3) : clean;
    }
}
