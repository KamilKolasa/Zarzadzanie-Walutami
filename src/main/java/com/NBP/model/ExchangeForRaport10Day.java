package com.NBP.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeForRaport10Day {
    private String currency;
    private String symbol;
    private Double[] exchanges;
}
