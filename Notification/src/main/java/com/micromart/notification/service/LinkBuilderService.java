package com.micromart.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LinkBuilderService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.tracking-path}")
    private String trackingPath;

    /**
     * Constructs the tracking URL
     * Result: http://localhost:3000/orders/track/ORD-12345
     */
    public String buildTrackingUrl(String orderId) {
        return frontendUrl + trackingPath + "/" + orderId;
    }

    public String buildSupportUrl() {
        return frontendUrl + "/support";
    }
}