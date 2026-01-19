package com.micromart.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String addressId;
    private String country;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String type;       // e.g., "SHIPPING", "BILLING"
}
