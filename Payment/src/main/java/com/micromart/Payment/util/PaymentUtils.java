package com.micromart.Payment.util;

import java.util.UUID;

public class PaymentUtils {

    /**
     * Generates a unique reference for manual bank transfers.
     */
    public static String generateBankTransferReference() {
        String shortUuid = UUID.randomUUID().toString().substring(0, 9).toUpperCase();
        return "BT-MICRO-" + shortUuid;
    }

    /**
     * Validates if a reference looks like a mock bank transfer ID.
     * Useful for admin endpoints to filter mock vs real payments.
     */
    public static boolean isMockBankTransferReference(String reference) {
        return reference != null && reference.startsWith("BT-MICRO-");
    }
}
