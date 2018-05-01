package com.NBP.dao;

import com.NBP.database.DbConnection;
import com.NBP.model.ExchangeForRaport;
import com.NBP.model.ExchangeForRaport10Day;
import com.NBP.model.ExchangeRate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateImpl implements ExchangeRateDao, Serializable {

    private static Connection connection = DbConnection.getInstance();

    @Override
    public void updateAll() {
        try {
            List<ExchangeRate> oldData = getAll();
            List<ExchangeRate> newData = DownloadDataJSON.getCurrencies();

            ExchangeRate oldExchange = null;
            for (ExchangeRate e : newData) {
                oldExchange = ExchangeRate.findExchangeRateWithSymbolInCollection(oldData, e.getSymbol());
                if (oldExchange != null) {
                    update(e);
                }
                else {
                    add(e);
                }
            }

            ExchangeRate newExchange = null;
            for (ExchangeRate e : oldData) {
                newExchange = ExchangeRate.findExchangeRateWithSymbolInCollection(newData, e.getSymbol());
                if (newExchange == null) {
                    delete(e.getSymbol());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ExchangeRate exchangeRate) {
        try {
            String sql = "UPDATE Exchange_rate SET exchange_today = ?, exchange_yesterday = ?, symbol = ? WHERE symbol = ?";
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setDouble(1, exchangeRate.getExchangeToday());
            prep.setDouble(2, exchangeRate.getExchangeYesterday());
            prep.setString(3, exchangeRate.getSymbol());
            prep.setString(4, exchangeRate.getSymbol());
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(ExchangeRate exchangeRate) {
        try {
            String sql = "INSERT INTO Exchange_rate (currency, exchange_today, exchange_yesterday, symbol) VALUES (?, ?, ?, ?)";
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setString(1, exchangeRate.getCurrency());
            prep.setDouble(2, exchangeRate.getExchangeToday());
            prep.setDouble(3, exchangeRate.getExchangeYesterday());
            prep.setString(4, exchangeRate.getSymbol());
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String symbol) {
        try {
            String sql = "DELETE FROM Exchange_rate WHERE symbol = ?";
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setString(1, symbol);
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> listExchangeRates = null;
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Exchange_rate";
                ResultSet rs = connection.createStatement().executeQuery(sql);

                listExchangeRates = new ArrayList<>();
                while (rs.next()) {
                    listExchangeRates.add(new ExchangeRate(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getDouble(4),
                            rs.getDouble(5)
                    ));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listExchangeRates;
    }

    @Override
    public List<String> getAllSymbol() {
        List<String> listSymbol = null;
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Exchange_rate";
                ResultSet rs = connection.createStatement().executeQuery(sql);

                listSymbol = new ArrayList<>();
                while (rs.next()) {
                    listSymbol.add(
                            rs.getString(3)
                    );
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listSymbol;
    }

    @Override
    public Optional<ExchangeRate> getBySymbol(String symbol) {
        Optional<ExchangeRate> exchangeRateOptional = null;
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Exchange_rate WHERE symbol = ?";
                PreparedStatement prep = connection.prepareStatement(sql);
                prep.setString(1, symbol);

                ResultSet rs = prep.executeQuery();
                if (rs.next()) {
                    exchangeRateOptional = Optional.ofNullable(new ExchangeRate(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getDouble(4),
                            rs.getDouble(5)
                    ));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exchangeRateOptional;
    }

    @Override
    public List<ExchangeRate> biggestIncreaseRates() {
        List<ExchangeRate> database = getAll();
        ExchangeRate er1 = null;
        ExchangeRate er2 = null;
        ExchangeRate er3 = null;
        List<ExchangeRate> list = new ArrayList<>();
        ExchangeRate substitute = ExchangeRate.builder().symbol("Brak waluty").currency("Nie ma waluty której wzróśł kurs").build();
        Double maxIncrease = Double.MIN_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < database.size(); j++) {
                switch (i) {
                    case 0:

                        Double today = database.get(j).getExchangeToday();
                        Double yesterday = database.get(j).getExchangeYesterday();

                        if ((today - yesterday) > 0 && (today - yesterday) > maxIncrease) {
                            maxIncrease = today - yesterday;
                            er1 = database.get(j);
                        }

                        if (er1 != null) {
                            list.add(0, er1);
                        } else {
                            list.add(0, substitute);
                        }

                        break;
                    case 1:

                        today = database.get(j).getExchangeToday();
                        yesterday = database.get(j).getExchangeYesterday();

                        if ((today - yesterday) > 0 && (today - yesterday) > maxIncrease && !er1.equals(database.get(j))) {
                            maxIncrease = today - yesterday;
                            er2 = database.get(j);
                        }

                        if (er2 != null) {
                            list.add(1, er2);
                        } else {
                            list.add(1, substitute);
                        }

                        break;
                    case 2:

                        today = database.get(j).getExchangeToday();
                        yesterday = database.get(j).getExchangeYesterday();

                        if ((today - yesterday) > 0 && (today - yesterday) > maxIncrease && !er1.equals(database.get(j)) && !er2.equals(database.get(j))) {
                            maxIncrease = today - yesterday;
                            er3 = database.get(j);
                        }

                        if (er3 != null) {
                            list.add(2, er3);
                        } else {
                            list.add(2, substitute);
                        }

                        break;
                }
            }
            maxIncrease = Double.MIN_VALUE;
        }
        return list;
    }

    @Override
    public List<ExchangeForRaport> raport() {
        List<ExchangeRate> list = getAll();
        List<ExchangeForRaport> raport = new ArrayList<>();
        for (ExchangeRate e : list) {
            BigDecimal bd = BigDecimal.valueOf(e.getExchangeToday()).subtract(BigDecimal.valueOf(e.getExchangeYesterday()));
            raport.add(new ExchangeForRaport(e.getCurrency(), e.getSymbol(), bd));
        }
        return raport;
    }

    @Override
    public List<ExchangeForRaport10Day> raport10Days(List<String> symbols) {
        if (symbols == null) {
            throw new NullPointerException("LIST SYMBOLS");
        }
        List<ExchangeForRaport10Day> list = new ArrayList<>();
        for (String s : symbols) {
            list.add(DownloadDataJSON.downloadCurrency(s));
        }

        return list;
    }
}
