package com.NBP.dao;

import com.NBP.model.ExchangeForRaport;
import com.NBP.model.ExchangeForRaport10Day;
import com.NBP.model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao {
    void updateAll();
    void update(ExchangeRate exchangeRate);
    void add(ExchangeRate exchangeRate);
    void delete(String symbol);
    List<ExchangeRate> getAll();
    List<String> getAllSymbol();
    Optional<ExchangeRate> getBySymbol(String symbol);
    List<ExchangeRate> biggestIncreaseRates();
    List<ExchangeForRaport> raport();//opisane w poleceniu w punkcie 3
    List<ExchangeForRaport10Day> raport10Days(List<String> symbols);//opisane w poleceniu w punkcie 4
}
