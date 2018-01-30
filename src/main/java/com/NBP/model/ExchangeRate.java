package com.NBP.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
}

