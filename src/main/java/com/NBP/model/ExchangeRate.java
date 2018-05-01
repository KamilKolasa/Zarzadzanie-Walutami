package com.NBP.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate implements Serializable {
    private Long id;
    private String currency;
    private String symbol;
    private Double exchangeToday;
    private Double exchangeYesterday;

    public static ExchangeRate findExchangeRateWithSymbolInCollection(List<ExchangeRate> exchangeRates, String symbol) {
        return exchangeRates == null ? null :
                exchangeRates
                .stream()
                .filter(e -> e.getSymbol().equals(symbol))
                .findFirst()
                .orElse(null);
    }
}

