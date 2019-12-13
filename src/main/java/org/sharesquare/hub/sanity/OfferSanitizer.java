package org.sharesquare.hub.sanity;


import org.springframework.stereotype.Component;

@Component
public class OfferSanitizer {
    public boolean sanitizeId(String id) {
        return false;
    }
}
