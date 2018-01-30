package com.NBP.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeForRaport {
    private String currency;
    private String symbol;
    private BigDecimal differenceBetweenDays;
}
